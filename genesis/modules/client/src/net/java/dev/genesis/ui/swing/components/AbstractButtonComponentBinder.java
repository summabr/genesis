/*
 * The Genesis Project
 * Copyright (C) 2005-2007  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.Component;

import javax.swing.AbstractButton;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;

public class AbstractButtonComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
         FieldMetadata fieldMetadata) {
      return new AbstractButtonComponentBoundField(binder, 
            (AbstractButton) component, fieldMetadata);
   }

   public class AbstractButtonComponentBoundField extends AbstractBoundMember
         implements BoundField {
      private final AbstractButton component;
      private final FieldMetadata fieldMetadata;

      public AbstractButtonComponentBoundField(SwingBinder binder, 
            AbstractButton component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
      }

      protected AbstractButton getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      public void setValue(Object value) {
         component.setText(format(fieldMetadata, value));
      }

      public String getValue() {
         return component.getText();
      }
   }
}