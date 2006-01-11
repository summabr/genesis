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
package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.io.IOException;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class Utils {
   public static final String ICON_PATH = "net/java/dev/genesis/plugins/" +
         "netbeans/projecttype/ui/resources/project_icon.gif";
   public static String RUN_LOCAL_TARGET = "run:local";
   public static String RUN_REMOTE_TARGET = "run:remote";
   public static String WEBSTART_TARGET = "all.with.webstart";
   public static String CLEAN_WEBSTART_TARGET = "clean-webstart";

   private Utils() {
   }

   public static FileObject getBuildFile(Project project, boolean showMessages) {
      FileObject build = project.getProjectDirectory().getFileObject(
            GeneratedFilesHelper.BUILD_XML_PATH);

      if (build == null || !build.isValid()) {
         if (showMessages) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                  NbBundle.getMessage(GenesisActionProvider.class,
                  "LBL_No_Build_XML_Found"), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
         }
         
         return null;
      }

      return build;
   }

   public static void invokeAction(Project project, String[] targets) {
      FileObject buildFile = Utils.getBuildFile(project, true);

      if (buildFile == null) {
         return;
      }

      try {
         ActionUtils.runTarget(buildFile, targets, null);
      } catch (IOException e) {
         ErrorManager.getDefault().notify(e);
      }
   }

   public static GenesisProjectExecutionMode getExecutionMode(
         GenesisProject project) {
      boolean local = "true".equals(project.getEvaluator().getProperty(
            "local.mode"));
      boolean remote = !"false".equals(project.getEvaluator().getProperty(
            "remote.mode"));

      return local ? (remote ? GenesisProjectExecutionMode.LOCAL_AND_REMOTE :
            GenesisProjectExecutionMode.LOCAL_MODE_ONLY) : 
            GenesisProjectExecutionMode.REMOTE_MODE_ONLY;
   }

   public static boolean usesWebstart(GenesisProject project) {
      String needsWebstart = project.getEvaluator().getProperty("needs.webstart");

      return (needsWebstart != null) ? "true".equals(needsWebstart) :
            getExecutionMode(project) != 
            GenesisProjectExecutionMode.LOCAL_MODE_ONLY;
   }
}
