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
package net.java.dev.genesis.ui.swt.widgets;

import java.util.Arrays;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.PropertyMisconfigurationException;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.widgets.ComboWidgetBinder;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class ComboWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Combo combo;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;
   private MockBean[] beans;

   public ComboWidgetBinderTest() {
      super("Combo Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      combo = new Combo(root = new Shell(), SWT.NONE);
      combo.setData(SWTBinder.KEY_PROPERTY, "key");
      combo.setData(SWTBinder.VALUE_PROPERTY, "value");
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));

      beans = new MockBean[] { new MockBean("one", "One"),
            new MockBean("two", "Two"), new MockBean("three", "Three"),
            new MockBean("four", "Four"), new MockBean("five", "Five") };

      String[] values = new String[beans.length];

      for (int i = 0; i < beans.length; i++) {
         values[i] = beans[i].getValue();
         setKey(i, getKey(beans[i]));
      }

      combo.setItems(values);

      widgetBinder = binder.getWidgetBinder(combo);
   }

   protected void setKey(int index, String key) throws Exception {
      combo.setData(SWTBinder.KEY_PROPERTY + '-' + index, key);
   }

   protected String getKey(Object value) throws Exception {
      String keyPropertyName = (String) combo.getData(SWTBinder.KEY_PROPERTY);

      if (keyPropertyName != null) {
         Object o = (value == null) ? null : PropertyUtils.getProperty(value,
               keyPropertyName);

         return binder.format(getName(), keyPropertyName, o);
      } else if (EnumHelper.getInstance().isEnum(value)) {
         return value.toString();
      }

      throw new PropertyMisconfigurationException("Property 'key' "
            + "must be configured for the widget named " + getName());
   }

   protected String getValue(Widget widget, Object value) throws Exception {
      String valueProperty = (String) widget.getData(SWTBinder.VALUE_PROPERTY);
      if (value instanceof String) {
         return (String) value;
      } else if (valueProperty == null) {
         return binder.format(binder.getLookupStrategy().getName(widget), null,
               value);
      }

      return binder.format(binder.getLookupStrategy().getName(widget),
            valueProperty, PropertyUtils.getProperty(value, valueProperty));
   }

   public void testSelectIndexes() {
      assertTrue(widgetBinder instanceof ComboWidgetBinder);
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, combo, dataMeta));

      simulateSelect(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      simulateSelect(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testSelectIndexesWithBlank() {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, combo, dataMeta));

      simulateSelect(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      simulateSelect(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      simulateSelect(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testUpdateIndexes() {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundDataProvider = widgetBinder.bind(binder, combo,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testUpdateIndexesWithBlank() {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testUpdateList() throws Exception {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(3, combo.getSelectionIndex());

      value = beans[0];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(0, combo.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      combo.setData(SWTBinder.KEY_PROPERTY, "key");

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(3, combo.getSelectionIndex());

      value = beans[1];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(1, combo.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }
   
   private void simulateSelect(int index) {
      if (index < 0) {
         combo.deselectAll();
      } else {
         combo.select(index);
      }
      
      Event event = new Event();
      event.widget = combo;
      event.button = 1;
      event.type = SWT.Selection;
      combo.notifyListeners(event.type, event);
   }
}