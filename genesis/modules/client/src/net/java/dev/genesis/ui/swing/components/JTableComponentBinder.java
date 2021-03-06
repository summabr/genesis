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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.table.JTableIndexResolver;
import net.java.dev.genesis.ui.swing.components.table.JTableIndexResolverRegistry;
import net.java.dev.genesis.ui.swing.renderers.FormatterTableCellRenderer;
import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JTableComponentBinder extends AbstractComponentBinder {
   private static Log log = LogFactory.getLog(JTableComponentBinder.class);

   public BoundDataProvider bind(SwingBinder binder, Component component,
         DataProviderMetadata dataProviderMetadata) {
      JTableIndexResolver indexResolver = JTableIndexResolverRegistry.
            getInstance().
            get(component.getClass(), true);
      if (indexResolver == null) {
         log.error(Bundle.getMessage(JTableComponentBinder.class,
               "COULD_NOT_FIND_SUITABLE_JTABLEINDEXRESOLVER_IMPL_FOR_X_COMPONENT_Y", // NOI18N
               component.getClass(), binder.getName(component)));
         return null;
      }

      return new JTableComponentBoundDataProvider(binder, (JTable)component,
            dataProviderMetadata, indexResolver);
   }

   public class JTableComponentBoundDataProvider extends AbstractBoundMember
         implements BoundDataProvider {
      private final JTable component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListSelectionListener listener;
      private final TableCellRenderer renderer;
      private JTableIndexResolver indexResolver;

      public JTableComponentBoundDataProvider(SwingBinder binder,
            JTable component, DataProviderMetadata dataProviderMetadata,
            JTableIndexResolver indexResolver) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;
         this.indexResolver = indexResolver;
         this.component.getSelectionModel().addListSelectionListener(
               listener = createListSelectionListener());

         this.renderer = createCellRenderer();

         configureTableCellRenderer();
      }

      protected JTable getComponent() {
         return component;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected ListSelectionListener getListener() {
         return listener;
      }

      protected TableCellRenderer createCellRenderer() {
         return new FormatterTableCellRenderer();
      }

      protected void configureTableCellRenderer() {
         Enumeration en = component.getColumnModel().getColumns();

         while (en.hasMoreElements()) {
            TableColumn column = (TableColumn)en.nextElement();

            if (column.getCellRenderer() == null) {
               column.setCellRenderer(renderer);
            }
         }
      }

      protected ListSelectionListener createListSelectionListener() {
         return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               if (event.getValueIsAdjusting()) {
                  return;
               }

               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         int[] indexes = component.getSelectedRows();

         if (!indexResolver.needsConversion(component)) {
            return indexes;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = indexResolver.convertRowIndexToModel(component,
                     indexes[i]);
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }

         return indexes;
      }

      public void updateIndexes(int[] indexes) {
         fillViewIndexes(indexes);

         if (Arrays.equals(getIndexes(), indexes)) {
            return;
         }

         deactivateSelectionListener();

         try {
            ListSelectionModel sm = component.getSelectionModel();
            sm.clearSelection();

            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] == -1) {
                  continue;
               }

               sm.addSelectionInterval(indexes[i], indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void fillViewIndexes(final int[] indexes) {
         if (!indexResolver.needsConversion(component)) {
            return;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = indexResolver.convertRowIndexToView(component,
                     indexes[i]);
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }
      }

      public void updateList(List data) throws Exception {
         int[] selected = dataProviderMetadata.isResetSelection() ? new int[0]
               : getIndexes();

         createTableModelAdapter().setData(data);
         setSelectedIndexes(data.size(), selected);
      }

      protected void setSelectedIndexes(int listSize, int[] indexes) {
         fillViewIndexes(indexes);
         deactivateSelectionListener();

         try {
            component.clearSelection();
            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] >= listSize) {
                  continue;
               }

               component.getSelectionModel().addSelectionInterval(indexes[i],
                     indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void deactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }
      }

      protected void reactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().addListSelectionListener(listener);
         }
      }

      protected TableModelAdapter createTableModelAdapter() {
         return new TableModelAdapter() {
            public void setData(List data) throws Exception {
               DefaultTableModel model = (DefaultTableModel)component.getModel();

               int dataSize = data.size();
               if (model.getRowCount() != data.size()) {
                  model.setRowCount(dataSize);
               }

               int columnCount = component.getColumnModel().getColumnCount();
               if (model.getColumnCount() != columnCount) {
                  model.setColumnCount(columnCount);
               }

               int i = 0;
               TableColumn column;
               String propertyName;
               for (Iterator iter = data.iterator(); iter.hasNext(); i++) {
                  Object bean = iter.next();

                  for (int j = 0; j < model.getColumnCount(); j++) {
                     column = component.getColumnModel().getColumn(j);
                     int modelIndex = column.getModelIndex();

                     Object value = getBinder().isVirtual(
                           propertyName = getIdentifier(column)) ? bean
                           : getProperty(bean, propertyName);
                     model.setValueAt(value, i, modelIndex);
                  }
               }
            }
         };
      }

      protected String getIdentifier(TableColumn column) {
         String identifier = null;
         String[] names = (String[])component.getClientProperty(
               SwingBinder.COLUMN_NAMES);

         if (names != null && names.length > column.getModelIndex()) {
            identifier = names[column.getModelIndex()];

            if (identifier != null) {
               return identifier;
            }
         }

         identifier = (String)column.getIdentifier();
         if (identifier == null) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  JTableComponentBinder.class,
                  "COLUMN_NUMBER_X_FROM_TABLE_Y_DOES_NOT_HAVE_AN_IDENTIFIER", // NOI18N
                  new Integer(column.getModelIndex()), getBinder().getName(
                  getComponent())));
         }

         return identifier;
      }

      public void unbind() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }

         if (renderer != null) {
            Enumeration en = component.getColumnModel().getColumns();

            while (en.hasMoreElements()) {
               TableColumn column = (TableColumn)en.nextElement();

               if (column.getCellRenderer() == renderer) {
                  column.setCellRenderer(null);
               }
            }
         }
      }
   }
}
