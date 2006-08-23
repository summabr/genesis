/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class AbstractButtonComponentBinderTest extends GenesisTestCase {
   private AbstractButton button;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private MockForm form;
   private ActionMetadata actionMeta;
   
   public AbstractButtonComponentBinderTest() {
      super("Abstract Button Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      button = SwingUtils.newButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      actionMeta = (ActionMetadata) form.getFormMetadata().getActionMetadatas().get("someAction");
      componentBinder = binder.getWidgetBinder(button);
   }

   public void testButtonClick() {
      assertTrue(componentBinder instanceof AbstractButtonComponentBinder);
      
      assertNull(componentBinder.bind(binder, button,
            (DataProviderMetadata) null));
      assertNull(componentBinder.bind(binder, button, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, button, actionMeta));

      button.doClick();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }
}
