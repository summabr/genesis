/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.plugins.netbeans.projecttype.classpath;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProjectType;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisSources;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ClassPathProviderImpl implements ClassPathProvider {
   private static final Object[][] sharedCompilePaths = new Object[][] {
      new Object[] {"genesis", new String[] {"genesis-shared-", 
                     "genesis-annotation-jdk5"}},
      new Object[] {"lib/hibernate", new String[] {"hibernate"}},
      new Object[] {"lib/commons",  new String[] {"commons-beanutils",
                     "commons-logging", "reusable-components"}}
   };
   private static final Object[][] desktopClientCompilePaths = new Object[][] {
      new Object[] {"genesis", new String[] {"genesis-shared-",
                    "genesis-client", "genesis-annotation-jdk5"}},
      new Object[] {"lib/hibernate", new String[] {"hibernate"}},
      new Object[] {"lib/commons", new String[] {"commons-beanutils",
                  "commons-digester", "commons-jxpath", "commons-logging",
                  "commons-validator", "jakarta-oro", "reusable-components"}},
      new Object[] {"lib/thinlet", new String[] {"thinlet"}}
   };
   private static final Object[][] webClientCompilePaths = new Object[][] {
      new Object[] {"genesis", new String[] {"genesis-shared-",
               "genesis-client", "genesis-annotation-jdk5"}},
      new Object[] {"lib/hibernate", new String[] {"hibernate"}},
      new Object[] {"lib/commons", new String[] {"commons-beanutils",
               "commons-jxpath", "commons-logging", "commons-validator",
               "reusable-components"}},
      new Object[] {"lib/j2ee", new String[] {"j2ee", "servlet-api"}}
   };
   private static final String[] classPathTypes = new String[] {ClassPath.BOOT,
         ClassPath.COMPILE, ClassPath.EXECUTE, ClassPath.SOURCE};

   private static abstract class MutableClassPathImplementation 
         implements ClassPathImplementation {
      private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
      private List paths;

      public List getResources() {
         if (paths == null) {
            paths = new ArrayList();

            Collection urls = getURLs();

            if (urls != null) {
               for (Iterator i = urls.iterator(); i.hasNext();) {
                  URL url = (URL)i.next();
                  paths.add(ClassPathSupport.createResource(url));
               }
            }
         }

         return paths;
      }

      public void addPropertyChangeListener(PropertyChangeListener listener) {
         pcs.addPropertyChangeListener(listener);
      }

      public void removePropertyChangeListener(PropertyChangeListener listener) {
         pcs.removePropertyChangeListener(listener);
      }

      public void reload() {
         paths = null;
         pcs.firePropertyChange(ClassPathImplementation.PROP_RESOURCES, null, null);
      }

      protected abstract Collection getURLs();
   }

   private final Map classpaths = new HashMap();
   private final Map registeredPaths = new HashMap();
   private final Collection implementations = new ArrayList();
   private final GenesisProject project;
   
   private ClassPath bootClassPath;
   
   public ClassPathProviderImpl(final GenesisProject project) {
      this.project = project;
   }

   public synchronized void register() {
      Sources sources = (Sources)project.getLookup().lookup(Sources.class);

      sources.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            synchronized (ClassPathProviderImpl.this) {
               classpaths.clear();

               Collection impls = new ArrayList(implementations);
               for (Iterator i = impls.iterator(); i.hasNext();) {
                  ((MutableClassPathImplementation)i.next()).reload();
               }
            }
         }
      });

      SourceGroup[] groups = sources.getSourceGroups(
            JavaProjectConstants.SOURCES_TYPE_JAVA);

      for (int i = 0; i < groups.length; i++) {
         FileObject root = groups[i].getRootFolder();

         for (int j = 0; j < classPathTypes.length; j++) {
            ClassPath cp = findClassPath(root, classPathTypes[j]);

            Collection paths = (Collection)registeredPaths.get(classPathTypes[j]);

            if (paths == null) {
               registeredPaths.put(classPathTypes[j], paths = new HashSet());
            }

            paths.add(cp);
         }
      }

      for (int i = 0; i < classPathTypes.length; i++) {
         Collection paths = (Collection)registeredPaths.get(classPathTypes[i]);

         if (paths == null) {
            continue;
         }

         GlobalPathRegistry.getDefault().register(classPathTypes[i], 
               (ClassPath[])paths.toArray(new ClassPath[paths.size()]));
      }
   }

   public synchronized void unregister() {
      for (int i = 0; i < classPathTypes.length; i++) {
         Collection paths = (Collection)registeredPaths.get(classPathTypes[i]);

         if (paths == null) {
            continue;
         }

         GlobalPathRegistry.getDefault().unregister(classPathTypes[i], 
               (ClassPath[])paths.toArray(new ClassPath[paths.size()]));
      }
   }

   public synchronized ClassPath findClassPath(FileObject fo, String type) {
      if (ClassPath.BOOT.equals(type)) {
         return getBootClassPath();
      }
      
      ClassPath cp = getClassPath(fo, type);
      
      return cp;
   }
   
   private ClassPath getBootClassPath() {
      if (bootClassPath == null) {
         MutableClassPathImplementation impl = new MutableClassPathImplementation() {
            protected Collection getURLs() {
               return getBootClassPathURLs();
            }
         };

         implementations.add(impl);
         bootClassPath = ClassPathFactory.createClassPath(impl);
      }

      return bootClassPath;
   }

   private Collection getBootClassPathURLs() {
      JavaPlatform platform = JavaPlatformManager.getDefault()
            .getDefaultPlatform();

      JavaPlatform[] platforms = JavaPlatformManager.getDefault()
            .getPlatforms(null, new Specification("j2se",
            new SpecificationVersion(Utils.getSourceLevel(
            project.getHelper()))));

      if (platforms.length > 0) {
         platform = platforms[0];

         for (int i = 0; i < platforms.length; i++) {
            if (platforms[i].getJavadocFolders().isEmpty()) {
               platform = platforms[i];
               break;
            }
         }

         for (int i = 0; i < platforms.length; i++) {
            if (platforms[i].getSourceFolders().getRoots().length > 0) {
               platform = platforms[i];
               break;
            }
         }
      }

      Collection entries = platform.getBootstrapLibraries().entries();
      Collection urls = new ArrayList(entries.size());

      for (Iterator i = entries.iterator(); i.hasNext();) {
         ClassPath.Entry entry = (ClassPath.Entry)i.next();
         urls.add(entry.getURL());
      }

      return urls;
   }

   private ClassPath getClassPath(FileObject fo, final String type) {
      Map classpathsPerType = (Map)classpaths.get(type);

      if (classpathsPerType == null) {
         classpathsPerType = new WeakHashMap();
         classpaths.put(type, classpathsPerType);
      }

      for (final Iterator i = classpathsPerType.entrySet().iterator();
            i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();
         FileObject root = (FileObject)entry.getKey();

         if (root == fo || FileUtil.isParentOf(root, fo)) {
            return (ClassPath)entry.getValue();
         }
      }

      final GenesisSources sources = (GenesisSources)project.getLookup().lookup(
            GenesisSources.class);
      SourceGroup[] groups = sources.getSourceGroups(
            JavaProjectConstants.SOURCES_TYPE_JAVA);

      for (int i = 0; i < groups.length; i++) {
         final FileObject root = groups[i].getRootFolder();

         if (root == fo || FileUtil.isParentOf(root, fo)) {
            MutableClassPathImplementation impl = new MutableClassPathImplementation() {
               protected Collection getURLs() {
                  return createClassPath(root, type, sources);
               }
            };

            implementations.add(impl);

            ClassPath classPath = ClassPathFactory.createClassPath(impl);
            classpathsPerType.put(root, classPath);

            return classPath;
         }
      }
      
      return null;
   }
   
   private Collection createClassPath(FileObject root, String type,
         GenesisSources sources) {
      if (ClassPath.SOURCE.equals(type)) {
         return createSourceClassPath(root, sources);
      } else if (ClassPath.COMPILE.equals(type)) {
         return createCompileClassPath(root, sources);
      } else if (ClassPath.EXECUTE.equals(type)) {
         return createExecuteClassPath(root, sources);
      }
      
      return null;
   }
   
   private Collection createSourceClassPath(FileObject root,
         GenesisSources sources){
      Collection files = new ArrayList();
      files.add(FileUtil.toFile(root));
      
      if (sources.getClientSourcesRoot() == root) {
         if (sources.getSharedSourcesRoot() != null) {
            files.add(FileUtil.toFile(sources.getSharedSourcesRoot()));
         }
         
         addClassPath(files, findSourcesRootNode(findSourceGroupFor("client")));
      } else if (sources.getSharedSourcesRoot() == root) {
         addClassPath(files, findSourcesRootNode(findSourceGroupFor("shared")));
      } else {
         addClassPath(files, findSourcesRootNode(findSourceGroupFor(root)));
      }

      return toURLs(files);
   }

   private Collection createCompileClassPath(FileObject root,
         GenesisSources sources) {
      if (root == sources.getSharedSourcesRoot()) {
         return createCompileClassPath("shared", root, sources,
               sharedCompilePaths);
      } else if (root == sources.getClientSourcesRoot()) {
         GenesisProjectKind kind = Utils.getKind(project);
         
         if (kind == GenesisProjectKind.DESKTOP) {
            return createCompileClassPath("client", root, sources,
                  desktopClientCompilePaths);
         } else if (kind == GenesisProjectKind.WEB) {
            return createCompileClassPath("client", root, sources,
                  webClientCompilePaths);
         }
      }

      Collection files = new ArrayList();
      addClassPath(files, findCompilationPathsRootNode(findSourceGroupFor(root)));

      return toURLs(files);
   }

   private Collection createCompileClassPath(String name, FileObject root,
         GenesisSources sources, Object[][] paths) {
      Collection files = new ArrayList();
      
      for (int i = 0; i < paths.length; i++) {
         Object[] filesPerRoot = paths[i];
         final String[] filePrefixes = (String[])filesPerRoot[1];
         
         File rootDir = new File(project.getEvaluator().evaluate(
               "${genesis.home}/" + filesPerRoot[0]));
         File[] filteredFiles = rootDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
               for (int j = 0; j < filePrefixes.length; j++) {
                  if (pathname.getName().startsWith(filePrefixes[j])) {
                     return true;
                  }
               }
               
               return false;
            }
         });
         
         for (int j = 0; j < filteredFiles.length; j++) {
            files.add(filteredFiles[j]);
         }
      }
      
      addClassPath(files, findCompilationPathsRootNode(findSourceGroupFor(name)));

      return toURLs(files);
   }
   
   private Collection createExecuteClassPath(FileObject root,
         GenesisSources sources) {
      Collection files = new ArrayList();
      
      ClassPath cp = findClassPath(root, ClassPath.COMPILE);

      if (cp != null) {
         FileObject[] roots = cp.getRoots();

         for (int i = 0; i < roots.length; i++) {
            files.add(FileUtil.toFile(roots[i]));
         }
      }
      
      AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(project,
            JavaProjectConstants.ARTIFACT_TYPE_JAR);
      
      for (int i = 0; i < artifacts.length; i++) {
         FileObject[] artifactFiles = artifacts[i].getArtifactFiles();
         
         for (int j = 0; j < artifactFiles.length; j++) {
            files.add(FileUtil.toFile(artifactFiles[j]));
         }
      }
      
      addClassPath(files, findExecutionPathsRootNode());
      
      return toURLs(files);
   }
   
   private Element findSourceGroupFor(String name) {
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "source-folders");
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      nl = ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, name);
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      return (Element)nl.item(0);
   }
   
   private Element findSourceGroupFor(FileObject root) {
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "source-folders");
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      nl = ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-group");
      
      for (int i = 0; i < nl.getLength(); i++) {
         Element group = (Element)nl.item(i);
         
         NodeList paths = group.getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-path");
         
         for (int j = 0; j < paths.getLength(); j++) {
            NodeList path = paths.item(j).getChildNodes();
            
            if (path.getLength() != 1) {
               continue;
            }
            
            if (root == Utils.resolveFileObject(project, path.item(0)
                  .getNodeValue())) {
               return group;
            }
         }
      }
      
      return null;
   }
   
   private NodeList findSourcesRootNode(Element e) {
      if (e == null) {
         return null;
      }
      
      NodeList nl = e.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "source-dependencies");
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      return ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "source-folder");
   }
   
   private NodeList findCompilationPathsRootNode(Element e) {
      if (e == null) {
         return null;
      }
      
      NodeList nl = e.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "compilation");
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      return ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "path");
   }
   
   private NodeList findExecutionPathsRootNode() {
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "execution");
      
      if (nl.getLength() != 1) {
         return null;
      }
      
      return ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE,
            "path");
   }
   
   private void addClassPath(Collection files, NodeList nl) {
      if (nl == null) {
         return;
      }
      
      for (int i = 0; i < nl.getLength(); i++) {
         NodeList subNodes = nl.item(i).getChildNodes();
         
         if (subNodes.getLength() != 1) {
            continue;
         }

         files.add(Utils.resolveFile(project, subNodes.item(0).getNodeValue()));
      }
   }

   private Collection toURLs(Collection files) {
      Collection urls = new ArrayList(files.size());

      for (Iterator i = files.iterator(); i.hasNext();) {
         File file = (File) i.next();

         if (file == null) {
            continue;
         }

         URL url;

         try {
            url = file.toURI().toURL();
         } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
            continue;
         }

         if (FileUtil.isArchiveFile(url)) {
            urls.add(FileUtil.getArchiveRoot(url));
            continue;
         }
         
         String external = url.toExternalForm();

         if (external.endsWith("/")) {
            urls.add(url);
            continue;
         }

         try {
            urls.add(new URL(external + '/'));
         } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
         }
      }

      return urls;
   }
}