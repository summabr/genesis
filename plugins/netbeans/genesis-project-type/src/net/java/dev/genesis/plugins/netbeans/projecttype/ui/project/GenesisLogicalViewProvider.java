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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.project;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProjectExecutionMode;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProjectType;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.GenericSources;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GenesisLogicalViewProvider implements LogicalViewProvider {
   private final GenesisProject project;

   private class GenesisLogicalProviderRootNode extends AbstractNode {
      public GenesisLogicalProviderRootNode() {
         super(new GenesisLogicalProviderChildren(), Lookups.singleton(project));
         setName();
         setIconBaseWithExtension(Utils.ICON_PATH);
         setShortDescription(project.getProjectDirectory().getPath());

         ProjectUtils.getInformation(project).addPropertyChangeListener(
               new PropertyChangeListener() {
                  public void propertyChange(PropertyChangeEvent event) {
                     if (!ProjectInformation.PROP_DISPLAY_NAME.equals(
                           event.getPropertyName())) {
                        return;
                     }

                     setName();
                     fireNameChange(null, null);
                  }
            });

         try {
            FileSystem fs = project.getProjectDirectory().getFileSystem();
            FileStatusListener fsl = FileUtil.weakFileStatusListener(
                  new FileStatusListener() {
                     public void annotationChanged(FileStatusEvent e) {
                        if (e.isIconChange()) {
                           fireOpenedIconChange();
                           fireIconChange();
                        }
                     }
                  }, fs);
            fs.addFileStatusListener(fsl);
         } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
         }
      }

      private void setName() {
         setName(ProjectUtils.getInformation(project).getDisplayName());
      }

      @Override
      public Action[] getActions(boolean b) {
         ResourceBundle bundle = NbBundle.getBundle(
               GenesisLogicalViewProvider.class);
         
         List<Action> actions = new ArrayList<Action>();
         
         actions.add(CommonProjectActions.newFileAction());
         actions.add(null);
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_BUILD, bundle.getString(
               "LBL_BuildAction_Name"), null));
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_REBUILD, bundle.getString(
               "LBL_RebuildAction_Name"), null));
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_CLEAN, bundle.getString(
               "LBL_CleanAction_Name"), null));

         Collection<Action> antActions = getAntActions();

         if (!antActions.isEmpty()) {
            actions.add(null);
            actions.addAll(antActions);
         }

         actions.add(null);

         addRunActions(actions, bundle);
         addWebstartActions(actions, bundle);

         actions.add(null);
         actions.add(CommonProjectActions.setAsMainProjectAction());
         actions.add(CommonProjectActions.closeProjectAction());
         actions.add(null);
         actions.add(SystemAction.get(FindAction.class));
         
         Lookup lookup = Lookups.forPath("Projects/Actions");
         Lookup.Template<Object> query = new Lookup.Template<Object>(Object.class);
         Result<Object> result = lookup.lookup(query);

         if (!result.allItems().isEmpty()) {
            Iterator it = result.allInstances().iterator();

            if (it.hasNext()) {
               actions.add(null);
            }

            while (it.hasNext()) {
               Object next = it.next();

               if (next instanceof Action) {
                  actions.add((Action)next);
               } else if (next instanceof JSeparator) {
                  actions.add(null);
               }
            }
         }

         actions.add(null);
         actions.add(SystemAction.get(ToolsAction.class));
         actions.add(null);

         actions.add(CommonProjectActions.customizeProjectAction());
         
         return actions.toArray(new Action[actions.size()]);
      }

      private List<Action> getAntActions() {
         final List<Action> actions = new ArrayList<Action>();
         Element data = project.getHelper().getPrimaryConfigurationData(true);
         NodeList nl = data.getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "view");

         if (nl.getLength() != 1) {
            return actions;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "context-menu");

         if (nl.getLength() != 1) {
            return actions;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "action");

         for (int i = 0; i < nl.getLength(); i++) {
            NodeList labelNodes = ((Element)nl.item(i)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "label");

            if (labelNodes.getLength() != 1) {
               continue;
            }

            String label = labelNodes.item(0).getChildNodes().item(0)
                  .getNodeValue();

            NodeList targetNodes = ((Element)nl.item(i)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "target");

            if (targetNodes.getLength() == 0) {
               continue;
            }

            Collection<String> targets = new ArrayList<String>(targetNodes.getLength());

            for (int j = 0; j < targetNodes.getLength(); j++) {
               targets.add(targetNodes.item(j).getChildNodes().item(0)
                  .getNodeValue());
            }

            actions.add(new CustomAntAction(project, label,
                  targets.toArray(new String[targets.size()])));
         }

         return actions;
      }

      private void addRunActions(final List<Action> actions, final ResourceBundle bundle) {
         GenesisProjectExecutionMode executionMode = 
               Utils.getExecutionMode(project);

         if (executionMode != GenesisProjectExecutionMode.LOCAL_AND_REMOTE) {
            actions.add(ProjectSensitiveActions.projectCommandAction(
                  ActionProvider.COMMAND_RUN, bundle.getString(
                  "LBL_RunAction_Name"), null));
         } else {
            actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_RunLocalAction_Name"), new String[] {
                  Utils.RUN_LOCAL_TARGET}));
            actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_RunRemoteAction_Name"), new String[] {
                  Utils.RUN_REMOTE_TARGET}));
         }
      }

      private void addWebstartActions(List<Action> actions, ResourceBundle bundle) {
         if (!Utils.usesWebstart(project)) {
            return;
         }

         actions.add(null);
         actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_BuildWebstartAction_Name"), new String[] {
                  Utils.WEBSTART_TARGET}));
         actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_CleanBuildWebstartAction_Name"), new String[] {
                  Utils.CLEAN_WEBSTART_TARGET}));
      }

      @Override
      public Image getOpenedIcon(int type) {
         return getAnnotatedIcon(super.getOpenedIcon(type), type);
      }

      @Override
      public Image getIcon(int type) {
         return getAnnotatedIcon(super.getIcon(type), type);
      }

      private Image getAnnotatedIcon(Image icon, int type) {
         try {
            return project.getProjectDirectory().getFileSystem().getStatus()
                  .annotateIcon(icon, type, Collections.singleton(
                  project.getProjectDirectory()));
         } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
         }

         return icon;
      }
   }

   private static class CustomAntAction extends AbstractAction {
      private final GenesisProject project;
      private final String displayName;
      private final String[] targets;

      public CustomAntAction(final GenesisProject project, 
            final String displayName, final String[] targets) {
         this.project = project;
         this.displayName = displayName;
         this.targets = targets;
      }

      public void actionPerformed(ActionEvent e) {
         Utils.invokeAction(project, targets);
      }

      @Override
      public Object getValue(String key) {
         if (Action.NAME.equals(key)) {
            return displayName;
         }

         return super.getValue(key);
      }

      @Override
      public boolean isEnabled() {
         return Utils.getBuildFile(project, false) != null;
      }
   }

   private class GenesisLogicalProviderChildren extends Children.Array {
      private final Collection<Node> nodes = new ArrayList<Node>();

      public GenesisLogicalProviderChildren() {
         project.getHelper().addAntProjectListener(new AntProjectListener() {
            public void configurationXmlChanged(AntProjectEvent e) {
               reload();
            }

            public void propertiesChanged(AntProjectEvent e) {
               reload();
            }
         });

         project.getEvaluator().addPropertyChangeListener(
               new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
               reload();
            }
         });
      }

      @Override
      protected synchronized Collection<Node> initCollection() {
         createNodes();
         return nodes;
      }

      private synchronized void reload() {
         createNodes();
         refresh();
      }

      private void createNodes() {
         nodes.clear();
         String clientSourcesDir = Utils.getClientSourcesDir(project);

         if (clientSourcesDir != null) {
            addSources(NbBundle.getMessage(GenesisLogicalViewProvider.class, 
                  Utils.getClientSourcesDisplayKey(project)), clientSourcesDir);
         }

         String sharedSourcesDir = Utils.getSharedSourcesDir(project);

         if (sharedSourcesDir != null) {
            addSources(NbBundle.getMessage(GenesisLogicalViewProvider.class, 
                  "LBL_Shared_Sources_Display_Name"), sharedSourcesDir);
         }

         addCustomSourceFolders();

         try {
            nodes.add(DataObject.find(Utils.resolveFileObject(project,
                  GeneratedFilesHelper.BUILD_XML_PATH)).getNodeDelegate()
                  .cloneNode());
         } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
         }
      }

      private void addSources(String displayName, String sourcesDir) {
         nodes.add(PackageView.createPackageView(GenericSources.group(project, 
               Utils.resolveFileObject(project, sourcesDir), 
               sourcesDir, displayName, null, null)));
      }

      private void addCustomSourceFolders() {
         Element data = project.getHelper().getPrimaryConfigurationData(true);
         NodeList nl = data.getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "view");

         if (nl.getLength() != 1) {
            return;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "items");

         if (nl.getLength() != 1) {
            return;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, 
               "source-folder");

         for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node node = nl.item(i);
            org.w3c.dom.Node styleNode = node.getAttributes().getNamedItem(
                  "style");
            boolean tree = true;

            if (styleNode == null || "packages".equals(styleNode.getNodeValue())) {
               tree = false;
            }

            NodeList subnodes = ((Element)node).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "label");

            if (subnodes.getLength() != 1) {
               continue;
            }

            subnodes = subnodes.item(0).getChildNodes();

            if (subnodes.getLength() != 1) {
               continue;
            }

            String displayName = subnodes.item(0).getNodeValue();

            subnodes = ((Element)node).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "location");

            if (subnodes.getLength() != 1) {
               continue;
            }

            subnodes = subnodes.item(0).getChildNodes();

            if (subnodes.getLength() != 1) {
               continue;
            }

            String location = subnodes.item(0).getNodeValue();

            if (tree) {
               try {
                  nodes.add(new TreeNode(((DataFolder)DataObject.find(Utils
                        .resolveFileObject(project, location))), displayName));
               } catch (DataObjectNotFoundException ex) {
                  ErrorManager.getDefault().notify(ex);
               }
            } else {
               addSources(displayName, location);
            }
         }
      }

   }

   private static final class TreeNode extends FilterNode {
      private static final DataFilter filter = new VisibilityQueryDataFilter();
      private final String displayName;

      public TreeNode(final DataFolder folder, final String displayName) {
         super(folder.getNodeDelegate(), folder.createNodeChildren(filter));
         this.displayName = displayName;
      }

      @Override
      public String getDisplayName() {
         return displayName;
      }
   }
    
   private static final class VisibilityQueryDataFilter 
         implements ChangeListener, ChangeableDataFilter {
      private final Collection<ChangeListener> listeners = new ArrayList<ChangeListener>();
      
      public VisibilityQueryDataFilter() {
         VisibilityQuery.getDefault().addChangeListener(this);
      }
      
      public boolean acceptDataObject(DataObject dataObject) {
         return VisibilityQuery.getDefault().isVisible(
               dataObject.getPrimaryFile());
      }
      
      public void stateChanged(ChangeEvent e) {
         if (listeners.isEmpty()) {
            return;
         }

         ChangeEvent event = new ChangeEvent(this);

         for (Iterator i = listeners.iterator(); i.hasNext();) {
            ChangeListener listener = (ChangeListener)i.next();
            listener.stateChanged(event);
         }
      }
      
      public void addChangeListener(ChangeListener listener) {
         listeners.add(listener);
      }
      
      public void removeChangeListener(ChangeListener listener) {
         listeners.remove(listener );
      }
   }

   public GenesisLogicalViewProvider(final GenesisProject project) {
      this.project = project;
   }

   public Node createLogicalView() {
      return new GenesisLogicalProviderRootNode();
   }

   public Node findPath(Node root, Object target) {
     Project project = root.getLookup().lookup(Project.class);

     if (project == null) {
         return null;
     }

     if (target instanceof FileObject) {
         FileObject fo = (FileObject) target;
         Project owner = FileOwnerQuery.getOwner(fo);

         if (!project.equals(owner)) {
             return null;
         }

         Node[] nodes = root.getChildren().getNodes(true);

         for (int i = 0; i < nodes.length; i++) {
             Node result = PackageView.findPath(nodes[i], target);
             if (result != null) {
                 return result;
             }
         }
     }

     return null;
   }
}