/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.controller;

import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.thinlet.ThinletBinder;

import org.apache.commons.jxpath.JXPathContext;

public class DefaultFormControllerTest extends GenesisTestCase {
   private DefaultFormController controller;
   private MockForm form;
   
   public DefaultFormControllerTest() {
      super("DefaultFormController Unit Test");
   }

   protected void setUp() throws Exception {
      controller = new DefaultFormController();
      form = new MockForm();
      controller.setForm(form);
   }

   public void testSetup() throws Throwable {
      final Map map = new HashMap();
      controller = new DefaultFormController() {
         protected void evaluate(boolean firstCall) throws Exception {
            map.put("evaluate(firstCall)", Boolean.valueOf(firstCall));
         }
      };
      controller.setForm(form);
      controller.setup();

      // Assert controller is setup
      assertTrue(controller.isSetup());
      // Assert controller was evaluated
      assertEquals(Boolean.TRUE, map.get("evaluate(firstCall)"));
      // Assert Script context was created
      assertNotNull("Script context is null", controller.getContext());
      // Assert FormState was created
      assertNotNull("No FormState", controller.getFormState());
      // Assert VariablesMap and Form's Context are the same object
      assertSame(controller.getVariablesMap(), form.getContext());
      // Assert FormState is declared on script context
      assertSame(controller.getFormState(), controller.getVariablesMap().get(
            FormController.CURRENT_STATE_KEY));
      // Assert FormMetadata is declared on script context
      assertSame(controller.getFormMetadata(), controller.getVariablesMap().get(
            FormController.FORM_METADATA_KEY));

      Exception ex = null;

      try {
         controller.setup();
      } catch (Exception e) {
         ex = e;
      }

      // Assert that an IllegalStateException occured
      assertTrue(ex instanceof IllegalStateException);
   }

   public void testCreateJXPathContext() {
      JXPathContext ctx = controller.createJXPathContext();

      // Assert script context was created
      assertNotNull(ctx);
      // Assert functions
      assertNotNull(ctx.getFunctions());
      // Assert variables
      assertNotNull(ctx.getVariables());
      // Assert context bean and form are the same object
      assertSame(form, ctx.getContextBean());
   }

   public void testFormControllerListeners() {
      FormControllerListener listener = new ThinletBinder(null, null, form);
      
      // Assert no listeners
      assertEquals(0, controller.getFormControllerListeners().size());
      
      controller.addFormControllerListener(listener);
      assertTrue(controller.getFormControllerListeners().contains(listener));
      assertEquals(1, controller.getFormControllerListeners().size());
      
      controller.removeFormControllerListener(listener);
      assertTrue(!controller.getFormControllerListeners().contains(listener));
      assertEquals(0, controller.getFormControllerListeners().size());
      
   }
}