/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui;

import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;

public class ActionInvoker {
   public static void invoke(final Object target, final String actionName)
         throws Exception {
      getFormController(target).invokeAction(actionName, null);
   }

   public static void refresh(final Object target) throws Exception {
      getFormController(target).update();
   }

   private static FormController getFormController(final Object target) 
         throws Exception {
      if (!(target instanceof FormControllerFactory)) {
         throw new IllegalArgumentException(target + " should implement " + 
               "FormControllerFactory; probably it should have been annotated " +
               "with @Form or your aop.xml/weaving process should be " +
               "properly configured.");
      }

      final FormController controller = ((FormControllerFactory)target)
            .retrieveFormController(target);

      if (!controller.isSetup()) {
         controller.setup();
      }

      return controller;
   }
}