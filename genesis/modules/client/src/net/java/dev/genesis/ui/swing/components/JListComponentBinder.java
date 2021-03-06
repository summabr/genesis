/*
 * The Genesis Project
 * Copyright (C) 2005-2009  Summa Technologies do Brasil Ltda.
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

import java.awt.Component;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JListComponentBinder extends AbstractComponentBinder {
   private static final Log log = LogFactory.getLog(JListComponentBinder.class);
   private final boolean useListModelAdapter;

   public JListComponentBinder() {
      this(false);
   }

   public JListComponentBinder(boolean useListModelAdapter) {
      this.useListModelAdapter = useListModelAdapter;
   }

   public BoundDataProvider bind(SwingBinder binder, Component component,
      DataProviderMetadata dataProviderMetadata) {
      return new JListBoundMember(binder, (JList) component,
         dataProviderMetadata);
   }

   public class JListBoundMember extends AbstractBoundMember
         implements BoundField, BoundDataProvider {
      private final JList component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListSelectionListener listener;

      public JListBoundMember(SwingBinder binder, JList component,
         DataProviderMetadata dataProviderMetadata) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;

         this.component.addListSelectionListener(listener = createListSelectionListener());
      }

      protected JList getComponent() {
         return component;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected ListSelectionListener getListener() {
         return listener;
      }

      protected ListSelectionListener createListSelectionListener() {
         return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               if (event.getValueIsAdjusting()) {
                  return;
               }
               
               getBinder().updateFormSelection(getDataProviderMetadata(), getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         return getBinder().getIndexesFromUI(component.getSelectedIndices(),
               isBlank(component));
      }

      public void updateIndexes(int[] indexes) {
         if (component.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION
               && indexes.length > 1) {
            StringBuffer sb = new StringBuffer('['); // NOI18N
            
            for (int i = 0; i < indexes.length; i++) {
               sb.append(indexes[i]).append(", "); // NOI18N
            }

            sb.append(']'); // NOI18N

            throw new IllegalArgumentException(Bundle.getMessage(
                  JListComponentBinder.class,
                  "COMPONENT_X_IS_A_SINGLE_SELECTION_LIST_IT_CANT_BE_UPDATED_WITH_INDEXES_Y", getBinder(). // NOI18N
                  getName(component), sb.toString()));
         }
         
         if (Arrays.equals(getIndexes(), indexes)) {
            return;
         }

         boolean isBlank = isBlank(component);
         indexes = getBinder().getIndexesFromController(indexes, isBlank);

         deactivateListeners();

         try {
            setSelectedIndexes(isBlank, indexes);
         } finally {
            reactivateListeners();
         }
      }

      protected void setSelectedIndexes(boolean isBlank, int[] indexes) {
         if (indexes.length == 0 || (isBlank && indexes.length == 1 && indexes[0] < 0)) {
            component.clearSelection();
         } else {
            component.setSelectedIndices(indexes);
         }
      }
      
      public void updateList(List list) throws Exception {
         deactivateListeners();

         try {
            boolean isBlank = isBlank(component);

            int[] selected = component.getSelectedIndices();
            if (dataProviderMetadata.isResetSelection()) {
               selected = isBlank ? new int[] { 0 } : new int[] { -1 };
            } else {
               int maxSelectionSize = isBlank ? list.size() + 1 : list.size();
               int j = 0;
               for (int i = 0; i < selected.length; i++) {
                  if (selected[i] < maxSelectionSize) {
                     j++;
                  }
               }

               int[] temp = new int[j];
               System.arraycopy(selected, 0, temp, 0, j);

               selected = temp;
            }
            
            if (useListModelAdapter) {
               createListModelAdapter().setData(getData(list));
            } else {
               component.setListData(getData(list));
            }

            setSelectedIndexes(isBlank, selected);
         } finally {
            reactivateListeners();
         }
      }

      public String getValue() throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return null;
         }

         int[] indexes = component.getSelectedIndices();
         boolean isBlank = isBlank(component);

         if (isBlank && indexes.length == 1 && indexes[0] == 0) {
            return null;
         }
         
         int i = 0;
         for (; i < indexes.length; i++) {
            if (indexes[i] == 0) {
               continue;
            }
         }

         // This method is not useful for dataproviders,
         // so return the key of the first selected value;
         return getKey(component.getSelectedValues()[i]);
      }

      public void setValue(Object value) throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return;
         }

         if (value == null) {
            deactivateListeners();

            try {
               setSelectedIndexes(isBlank(component), new int[0]);
            } finally {
               reactivateListeners();
            }

            return;
         }

         boolean multi = isListOrArray(value);
         boolean singleSelection = component.getSelectionMode() == 
               ListSelectionModel.SINGLE_SELECTION;

         if (multi && singleSelection) {
            log.warn(
                  Bundle.getMessage(JListComponentBinder.class,
                  "CANNOT_UPDATE_X_COMPONENT_WITH_MULTIPLE_VALUES_BECAUSE_ITS_A_SINGLE_SELECTION_LIST", getBinder(). // NOI18N
                  getName(component)));
            return;
         }
         
         if (singleSelection) {
            int index = getIndexFor(value);
            
            deactivateListeners();

            try {
               setSelectedIndexes(isBlank(component), index < 0 ? new int[0] : 
                  new int[] {index});
            } finally {
               reactivateListeners();
            }

            return;
         }

         deactivateListeners();

         try {
            setSelectedIndexes(isBlank(component), getIndexes(value));
         } finally {
            reactivateListeners();
         }
      }

      
      protected int[] getIndexes(Object value) throws Exception {
         if (value.getClass().isArray()) {
            int remove = 0;
            Object[] array = (Object[]) value;
            int[] indexes = new int[array.length];
            for (int i = 0; i < array.length; i++) {
               int index = getIndexFor(array[i]);
               
               if (index < 0) {
                  remove++;
                  continue;
               }
               
               indexes[i - remove] = index;
            }
            
            if (remove > 0) {
               final int[] aux = indexes;
               indexes = new int[aux.length - remove];
               System.arraycopy(aux, 0, indexes, 0, indexes.length);
            }
            
            return indexes;
         }
         
         if (List.class.isAssignableFrom(value.getClass())) {
            int remove = 0;
            List list = (List) value;
            int[] indexes = new int[list.size()];
            int i = 0;
            for (Iterator iterator = list.iterator(); iterator.hasNext(); i++) {
               int index = getIndexFor(iterator.next());
               
               if (index < 0) {
                  remove++;
                  continue;
               }
               
               indexes[i - remove] = index;
            }

            if (remove > 0) {
               final int[] aux = indexes;
               indexes = new int[aux.length - remove];
               System.arraycopy(aux, 0, indexes, 0, indexes.length);
            }

            return indexes;
         }

         // never happens
         throw new IllegalArgumentException(Bundle.getMessage(
               JListComponentBinder.class,
               "ARGUMENT_IS_NOT_AN_ARRAY_OR_A_JAVA_UTIL_LIST")); // NOI18N
      }

      protected boolean isListOrArray(Object object) {
         return object.getClass().isArray() || List.class.isAssignableFrom(object.getClass()); 
      }

      protected int getIndexFor(Object value) throws Exception {
         boolean isBlank = isBlank(component);
         int found = -1;

         if (value != null) {
            String selectedKey = getKey(value);

            int size = component.getModel().getSize();

            for (int i = isBlank ? 1 : 0; i < size; i++) {
               Object o = component.getModel().getElementAt(i);

               if (selectedKey.equals(getKey(o))) {
                  found = i;

                  break;
               }
            }
         } else if (isBlank) {
            found++;
         }

         return found;
      }
      
      public void unbind() {
         if (listener != null) {
            component.removeListSelectionListener(listener);
         }
      }

      protected Object[] getData(List list) throws Exception {
         boolean isBlank = isBlank(component);
         Object[] values = new Object[isBlank ? (list.size() + 1) : list.size()];

         int i = 0;

         if (isBlank) {
            String blankLabel = (String) component
                  .getClientProperty(SwingBinder.BLANK_LABEL_PROPERTY);
            values[i] = (blankLabel == null) ? "" : blankLabel; // NOI18N
            i++;
         }

         for (Iterator iter = list.iterator(); iter.hasNext(); i++) {
            values[i] = iter.next();
         }

         return values;
      }

      protected ListModelAdapter createListModelAdapter() {
         return new ListModelAdapter() {
            public void setData(Object[] data) {
               DefaultListModel model = (DefaultListModel) component.getModel();
               model.clear();

               for (int i = 0; i < data.length; i++) {
                  model.addElement(data[i]);
               }
            }
         };
      }

      protected String getKey(Object value) throws Exception {
         return ComponentBinderHelper.getKey(getBinder(), component, getName(), 
               value);
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeListSelectionListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addListSelectionListener(listener);
         }
      }
   }
}
