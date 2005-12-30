/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.ComponentBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.thinlet.PropertyMisconfigurationException;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

public class AbstractComponentBinder implements ComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return null;
   }

   public BoundAction bind(SwingBinder binder, Component component,
      ActionMetadata actionMetatada) {
      return null;
   }

   public BoundDataProvider bind(SwingBinder binder, Component component,
      DataProviderMetadata dataProviderMetadata) {
      return null;
   }

   public abstract class AbstractBoundMember implements BoundMember {
      private final JComponent component;
      private final SwingBinder binder;
      private final Set enabledWidgetGroupSet = new HashSet();
      private final Set visibleWidgetGroupSet = new HashSet();

      public AbstractBoundMember(SwingBinder binder, JComponent component) {
         this.component = component;
         this.binder = binder;

         createGroups();
      }

      protected SwingBinder getBinder() {
         return binder;
      }

      protected Set getEnabledWidgetGroupSet() {
         return enabledWidgetGroupSet;
      }

      protected Set getVisibleWidgetGroupSet() {
         return visibleWidgetGroupSet;
      }

      protected void createGroups() {
         createWidgetGroup();
         createEnabledGroup();
         createVisibleGroup();
      }

      protected void createWidgetGroup() {
         createGroup(component.getClientProperty("widgetGroup"), true, true);
      }

      protected void createEnabledGroup() {
         createGroup(component.getClientProperty("enabledGroup"), true, false);
      }

      protected void createVisibleGroup() {
         createGroup(component.getClientProperty("visibleGroup"), false, true);
      }

      protected void createGroup(Object group, boolean enabled, boolean visible) {
         if (group == null) {
            return;
         }

         if (group instanceof String) {
            String[] componentNames = ((String) group).split(",");

            for (int i = 0; i < componentNames.length; i++) {
               Component c =
                  binder.getLookupStrategy()
                           .lookup(binder.getRoot(), componentNames[i]);

               if (c != null) {
                  if (enabled) {
                     enabledWidgetGroupSet.add(c);
                  }

                  if (visible) {
                     visibleWidgetGroupSet.add(c);
                  }
               }
            }
         } else if (group instanceof Collection) {
            Collection groupCollection = (Collection) group;

            if (enabled) {
               enabledWidgetGroupSet.addAll(groupCollection);
            }

            if (visible) {
               visibleWidgetGroupSet.addAll(groupCollection);
            }
         } else {
            // TODO: message
            throw new IllegalArgumentException();
         }
      }

      protected boolean isBlank(JComponent component) {
         return isBoolean(component, "blank");
      }

      protected boolean isBoolean(JComponent component, String propertyName) {
         final Object booleanObject = component.getClientProperty(propertyName);

         boolean ret;

         if (booleanObject == null) {
            ret = false;
         } else if (booleanObject instanceof String) {
            ret = Boolean.valueOf(booleanObject.toString()).booleanValue();
         } else if (booleanObject instanceof Boolean) {
            ret = ((Boolean) booleanObject).booleanValue();
         } else {
            throw new PropertyMisconfigurationException("Property '" +
               propertyName + "' " + "for the component named " +
               component.getName() + " must " +
               "either be left empty or contain a boolean value");
         }

         return ret;
      }

      public void setEnabled(boolean enabled) {
         component.setEnabled(enabled);

         for (Iterator iter = enabledWidgetGroupSet.iterator(); iter.hasNext();) {
            ((JComponent) iter.next()).setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         component.setVisible(visible);

         for (Iterator iter = visibleWidgetGroupSet.iterator(); iter.hasNext();) {
            ((JComponent) iter.next()).setVisible(visible);
         }
      }

      public String getName() {
         return binder.getLookupStrategy().getName(component);
      }

      public void unbind() {
      }
   }
}