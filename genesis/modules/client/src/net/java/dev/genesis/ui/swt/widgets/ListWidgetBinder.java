/*
 * The Genesis Project
 * Copyright (C) 2006-2009  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt.widgets;

import java.util.Iterator;
import java.util.List;

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Widget;

public class ListWidgetBinder extends AbstractWidgetBinder {
   private static final Log log = LogFactory.getLog(ListWidgetBinder.class);

   public BoundDataProvider bind(SWTBinder binder, Widget widget,
         DataProviderMetadata dataProviderMetadata) {
      return new ListBoundMember(binder, (org.eclipse.swt.widgets.List) widget,
            dataProviderMetadata);
   }

   public class ListBoundMember extends AbstractBoundMember implements
         BoundField, BoundDataProvider {
      private final org.eclipse.swt.widgets.List widget;
      private final DataProviderMetadata dataProviderMetadata;
      private final SelectionListener listener;

      public ListBoundMember(SWTBinder binder,
            org.eclipse.swt.widgets.List widget,
            DataProviderMetadata dataProviderMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.dataProviderMetadata = dataProviderMetadata;

         this.widget.addSelectionListener(listener = createSelectionListener());
      }

      protected org.eclipse.swt.widgets.List getWidget() {
         return widget;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected SelectionListener getListener() {
         return listener;
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         return getBinder().getIndexesFromUI(widget.getSelectionIndices(),
               isBlank(widget));
      }

      public void updateIndexes(int[] indexes) {
         indexes = getBinder().getIndexesFromController(indexes,
               isBlank(widget));
         widget.deselectAll();
         if (indexes != null && indexes.length > 0
               && (indexes.length != 1 || indexes[0] > -1)) {
            widget.setSelection(indexes);
         }
      }

      public void updateList(List list) throws Exception {
         boolean isBlank = isBlank(widget);

         int[] selected = widget.getSelectionIndices();
         if (dataProviderMetadata.isResetSelection()) {
            selected = isBlank ? new int[] { 0 } : new int[] { -1 };
         }

         widget.deselectAll();
         widget.setItems(getData(list));
         widget.setSelection(selected);
      }

      public String getValue() throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return null;
         }

         int[] indexes = widget.getSelectionIndices();
         boolean isBlank = isBlank(widget);

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
         return getKey(i);
      }

      public void setValue(Object value) throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return;
         }
         
         if (value == null) {
            widget.deselectAll();
            return;
         }

         boolean multi = isListOrArray(value);
         boolean singleSelection = (widget.getStyle() & SWT.SINGLE) != 0;
         if (multi && singleSelection) {
            log.warn("Cannot update " + getBinder().getName(widget) // NOI18N
                  + " widget with multiple values because it's a single " + // NOI18N
                  "selection list."); // NOI18N
            return;
         }
         
         if (singleSelection) {
            widget.deselectAll();
            widget.select(getIndexFor(value));
            return;
         }

         widget.deselectAll();
         widget.select(getIndexes(value));
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
               ListWidgetBinder.class,
               "ARGUMENT_IS_NOT_AN_ARRAY_OR_A_JAVA_UTIL_LIST")); // NOI18N
      }

      protected boolean isListOrArray(Object object) {
         return object.getClass().isArray() || List.class.isAssignableFrom(object.getClass()); 
      }
 
      protected int getIndexFor(Object value) throws Exception {
         boolean isBlank = isBlank(widget);

         int found = -1;

         if (value != null) {
            String selectedKey = getKey(value);

            int size = widget.getItemCount();

            for (int i = isBlank ? 1 : 0; i < size; i++) {
               if (selectedKey.equals(getKey(i))) {
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
            widget.removeSelectionListener(listener);
         }
      }

      protected String[] getData(List list) throws Exception {
         boolean isBlank = isBlank(widget);
         String[] values = new String[isBlank ? (list.size() + 1) : list.size()];

         int i = 0;

         if (isBlank) {
            String blankLabel = (String) widget
                  .getData(SWTBinder.BLANK_LABEL_PROPERTY);
            values[i] = (blankLabel == null) ? "" : blankLabel; // NOI18N
            i++;
         }

         for (Iterator iter = list.iterator(); iter.hasNext(); i++) {
            Object value = iter.next();
            values[i] = getValue(widget, value);
            setKey(i, getKey(value));
         }

         return values;
      }

      protected void setKey(int index, String key) throws Exception {
         widget.setData(SWTBinder.KEY_PROPERTY + '-' + index, key);
      }

      protected String getKey(int index) throws Exception {
         return (String) widget.getData(SWTBinder.KEY_PROPERTY + '-' + index);
      }

      protected String getValue(Widget widget, Object value) throws Exception {
         return WidgetBinderHelper.format(getBinder(), widget, value);
      }

      protected String getKey(Object value) throws Exception {
         return WidgetBinderHelper.getKey(getBinder(), widget, getName(),
               value);
      }
   }
}
