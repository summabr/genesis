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
package net.java.dev.genesis.aspect;

import net.java.dev.genesis.helpers.TypeChecker;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import org.codehaus.aspectwerkz.aspect.management.Mixins;

public class FormControllerFactoryAspect {
   /**
    * @Mixin(pointcut="formControllerFactoryIntroduction", isTransient=true, deploymentModel="perInstance")
    */
   public static class AspectFormControllerFactory implements FormControllerFactory {
      private FormController controller;
      private int maximumEvaluationTimes = 1;
      private boolean resetOnDataProviderChange;

      public AspectFormControllerFactory() {
         String timesAsString = (String)Mixins.getParameters(getClass(),
               getClass().getClassLoader()).get("maximumEvaluationTimes");

         if (timesAsString != null) {
            maximumEvaluationTimes = Integer.parseInt(timesAsString);
         }

         resetOnDataProviderChange = !"false".equals(Mixins.getParameters(
               getClass(), getClass().getClassLoader()).get(
               "resetOnDataProviderChange"));
      }

      public FormController getFormController(Object form) {
         if (controller == null) {
            controller = createFormController(form);

            controller.setForm(form);
            controller.setFormMetadata(getFormMetadata(form));
            controller.setMaximumEvaluationTimes(maximumEvaluationTimes);
            controller.setResetOnDataProviderChange(resetOnDataProviderChange);
         }

         return controller;
      }

      protected FormController createFormController(Object form) {
         return new DefaultFormController();
      }

      protected FormMetadata getFormMetadata(final Object form) {
         TypeChecker.checkFormMetadataFactory(form);

         return ((FormMetadataFactory)form).getFormMetadata(form.getClass());
      }
   }
}