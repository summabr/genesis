/*
 * The Genesis Project
 * Copyright (C) 2004-2009  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.ui;

import java.util.ArrayList;
import java.util.List;

import net.java.dev.genesis.commons.beanutils.converters.DefaultConverter;
import net.java.dev.genesis.equality.DefaultEqualityComparator;
import net.java.dev.genesis.equality.StringEqualityComparator;
import net.java.dev.genesis.reflection.FieldEntry;
import net.java.dev.genesis.resolvers.DefaultEmptyResolver;
import net.java.dev.genesis.resolvers.StringEmptyResolver;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.StringConverter;

public class FormMetadataFactoryTest extends TestCase {

   private FieldMetadata codeField;
   private FieldMetadata nameField;
   private FieldMetadata objField;
   private FieldMetadata numberField;
   private FieldMetadata fieldField;
   private FieldMetadata descriptionField;
   private ActionMetadata saveMethod;
   private ActionMetadata resetMethod;
   private ActionMetadata cancelMethod;
   private ActionMetadata compositeVisibleWhenMethod;
   private MethodMetadata calculateMethod;
   private MethodMetadata provideSomeListMethod;
   private DataProviderMetadata provideAnotherListMethod;
   private DataProviderMetadata provideCodesListMethod;

   public void setUp() throws Exception {
      super.setUp();
      final FormMetadata formMetadata = getFormMetadata(new FooForm());

      codeField = formMetadata.getFieldMetadata("code");
      nameField = formMetadata.getFieldMetadata("name");
      objField = formMetadata.getFieldMetadata("obj");
      numberField = formMetadata.getFieldMetadata("number");
      fieldField = formMetadata.getFieldMetadata("field");
      descriptionField = formMetadata.getFieldMetadata("description");
      saveMethod = formMetadata.getMethodMetadata("save").getActionMetadata();
      resetMethod = formMetadata.getMethodMetadata("reset").getActionMetadata();
      cancelMethod = formMetadata.getMethodMetadata("cancel").getActionMetadata();
      compositeVisibleWhenMethod = formMetadata.getMethodMetadata(
            "compositeVisibleWhen").getActionMetadata();
      calculateMethod = formMetadata.getMethodMetadata("calculate");
      provideSomeListMethod = formMetadata.getMethodMetadata("provideSomeList");
      provideAnotherListMethod = formMetadata.getMethodMetadata("provideAnotherList").getDataProviderMetadata();
      provideCodesListMethod = formMetadata.getMethodMetadata("provideCodesList").getDataProviderMetadata();
   }

   public void testParseFormMetadata() {
      getFormMetadata(new FooForm());
   }

   public void testConditions() {
      final FormMetadata formMetadata = getFormMetadata(new FooForm());
      assertEquals("string-length(normalize-space(code)) != 1", formMetadata
            .getNamedCondition("codeCondition").toString());
      assertEquals("string-length(normalize-space(code)) != 0", formMetadata
            .getNamedCondition("objCondition").toString());
   }

   public void testClearOn() {

      assertEquals("string-length(normalize-space(code)) != 1", codeField
            .getClearOnCondition().toString());

      assertNull(nameField.getClearOnCondition());

      assertNull(objField.getClearOnCondition());

      assertNull(numberField.getClearOnCondition());

      assertNull(fieldField.getClearOnCondition());

      assertNull(descriptionField.getClearOnCondition());
   }

   public void testEnabledCondition() {

      assertEquals("string-length(normalize-space(code)) != 0", codeField
            .getEnabledCondition().toString());

      assertNull(nameField.getEnabledCondition());

      assertNull(objField.getEnabledCondition());

      assertEquals("string-length(normalize-space(code)) != 0", numberField
            .getEnabledCondition().toString());

      assertNull(fieldField.getEnabledCondition());

      assertNull(descriptionField.getEnabledCondition());

      assertNull(saveMethod.getEnabledCondition());
      assertNull(resetMethod.getEnabledCondition());
      assertEquals("string-length(normalize-space(description)) != 2", cancelMethod
            .getEnabledCondition().toString());
   }

   public void testVisibleCondition() {
      assertNull(codeField.getVisibleCondition());
      assertNull(nameField.getVisibleCondition());
      assertNull(objField.getVisibleCondition());
      assertNull(numberField.getVisibleCondition());
      assertEquals("string-length(normalize-space(description)) != 0",
            fieldField.getVisibleCondition().toString());
      assertNull(saveMethod.getVisibleCondition());
      assertEquals("string-length(normalize-space(description)) != 1",
            resetMethod.getVisibleCondition().toString());
      assertNull(cancelMethod.getVisibleCondition());
      assertNull(descriptionField.getVisibleCondition());
      assertEquals("true", String.valueOf(compositeVisibleWhenMethod.getVisibleCondition()));
   }

   public void testValidateBefore() {
      assertTrue(saveMethod.isValidateBefore());
      assertFalse(resetMethod.isValidateBefore());
      assertFalse(cancelMethod.isValidateBefore());
   }

   public void testCallWhen(){
      assertEquals("string-length(normalize-space(description)) != 3",
            calculateMethod.getCallCondition().toString());
      assertEquals("string-length(normalize-space(description)) != 4",
            provideSomeListMethod.getCallCondition().toString());
   }

   public void testDataProvider(){
      FieldEntry objectField = provideSomeListMethod.getDataProviderMetadata().getObjectField();
      assertEquals("name", objectField.getFieldName());
      assertEquals(String.class.getName(), objectField.getFieldTypeName());
      assertFalse(objectField.isArray());
      assertFalse(objectField.isCollection());
      assertFalse(objectField.isMultiple());

      objectField = provideAnotherListMethod.getObjectField();
      assertEquals("ids", objectField.getFieldName());
      assertEquals(Long[].class.getName(), objectField.getFieldTypeName());
      assertTrue(objectField.isArray());
      assertFalse(objectField.isCollection());
      assertTrue(objectField.isMultiple());

      objectField = provideCodesListMethod.getObjectField();
      assertEquals("codes", objectField.getFieldName());
      assertEquals(List.class.getName(), objectField.getFieldTypeName());
      assertFalse(objectField.isArray());
      assertTrue(objectField.isCollection());
      assertTrue(objectField.isMultiple());
   }

   public void testEmptyResolver() {

      assertEquals(codeField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);
      assertTrue(((StringEmptyResolver) codeField.getEmptyResolver()).isTrim());

      assertEquals(nameField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(objField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);
      assertTrue(((StringEmptyResolver) objField.getEmptyResolver()).isTrim());

      assertEquals(numberField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(fieldField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(descriptionField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);

      assertSame(fieldField.getEmptyResolver(), numberField.getEmptyResolver());
      assertSame(descriptionField.getEmptyResolver(), codeField
            .getEmptyResolver());
   }

   public void testEqualityComparator() {

      assertEquals(codeField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);
      assertTrue(((StringEqualityComparator) codeField.getEqualityComparator())
            .isTrim());

      assertEquals(nameField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(objField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);
      assertTrue(((StringEqualityComparator) objField.getEqualityComparator())
            .isTrim());

      assertEquals(numberField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(fieldField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(descriptionField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);

      assertSame(fieldField.getEqualityComparator(), numberField
            .getEqualityComparator());

      assertNotSame(descriptionField.getEqualityComparator(), codeField
            .getEqualityComparator());

   }

   public void testConverter() {

      assertNotNull(codeField.getConverter());
      assertEquals(codeField.getConverter().getClass(), StringConverter.class);

      assertNotNull(nameField.getConverter());
      assertEquals(nameField.getConverter().getClass(), StringConverter.class);

      assertNotNull(objField.getConverter());
      assertEquals(objField.getConverter().getClass(), DefaultConverter.class);

      assertNotNull(numberField.getConverter());
      assertEquals(numberField.getConverter().getClass(),
            IntegerConverter.class);
 
      assertNotNull(fieldField.getConverter());
      assertEquals(fieldField.getConverter().getClass(), DefaultConverter.class);

      assertNotNull(descriptionField.getConverter());
      assertEquals(descriptionField.getConverter().getClass(),
            StringConverter.class);

   }

   public void testNameClash() {
      try {
         getFormMetadata(new NameClashForm());
         fail("An IllegalArgumentException should be thrown when name " +
               "clashes occur");
      } catch (IllegalArgumentException iae) {
         //Expected
      }
   }

   public void testOverriddenProperty() {
      assertNull("@NotBound property isn't ignored", getFormMetadata(
            new OverriddenForm()).getFieldMetadata("code"));
   }

   public void testBrokenObjectField() {
      try {
         getFormMetadata(new BrokenObjectFieldForm());
         fail("An IllegalArgumentException should be thrown when objectField " +
               "is not a read-write property");
      } catch (IllegalArgumentException iae) {
         //Expected
      }
   }

   public void testBrokenIndexField() {
      try {
         getFormMetadata(new BrokenIndexFieldForm());
         fail("An IllegalArgumentException should be thrown when indexField " +
               "is not a read-write property");
      } catch (IllegalArgumentException iae) {
         //Expected
      }
   }

   public void testBrokenEnabledWhen() {
      try {
         getFormMetadata(new BrokenEnabledWhenForm());
         fail("An IllegalArgumentException should be thrown when @EnabledWhen " +
               "has no conditions");
      } catch (ScriptException se) {
         fail("An ScriptException should not be thrown");
      } catch (IllegalArgumentException iae) {
         //Expected
      }
   }

   /**
    * @Form
    */
   public static class FooForm {

      private String code;
      private String name;
      private Object obj;
      private int number;
      private Object field;
      private String description;
      private Long[] ids;
      private List codes;

      /**
       * @Condition
       * 		codeCondition=string-length(normalize-space(code)) != 1
       * @ClearOn
       * 		string-length(normalize-space(code)) != 1
       * @EnabledWhen
       * 		string-length(normalize-space(code)) != 0
       * @EqualityComparator
       * 		trim=true
       * @EmptyResolver
       */
      public String getCode() {
         return code;
      }

      public void setCode(String code) {
         this.code = code;
      }

      /**
       * @EnabledWhen
       * 		string-length(normalize-space(code)) != 0
       * @return
       */
      public int getNumber() {
         return number;
      }

      public void setNumber(int number) {
         this.number = number;
      }

      /**
       * @EqualityComparator
       * 		class=net.java.dev.genesis.equality.DefaultEqualityComparator
       * @EmptyResolver
       * 		class=net.java.dev.genesis.resolvers.DefaultEmptyResolver
       */
      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      /**
       * @Condition
       * 		objCondition=string-length(normalize-space(code)) != 0
       * @EqualityComparator
       * 		class=net.java.dev.genesis.equality.StringEqualityComparator trim=true
       * @EmptyResolver
       * 		class=net.java.dev.genesis.resolvers.StringEmptyResolver trim=true
       */
      public Object getObj() {
         return obj;
      }

      public void setObj(Object obj) {
         this.obj = obj;
      }

      /**
       * @EmptyResolver
       */
      public String getDescription() {
         return description;
      }

      public void setDescription(String description) {
         this.description = description;
      }

      /**
       * @EmptyResolver
       * @VisibleWhen
       * 		string-length(normalize-space(description)) != 0
       */
      public Object getField() {
         return field;
      }

      public void setField(Object field) {
         this.field = field;
      }

      /**
       * @Action
       * @ValidateBefore
       */
      public void save() {
      }

      /**
       * @Action
       * @VisibleWhen
       * 		string-length(normalize-space(description)) != 1
       */
      public void reset() {
      }

      /**
       * @Action
       * @EnabledWhen
       * 		string-length(normalize-space(description)) != 2
       */
      public void cancel() {

      }

      /**
       * @Action
       * @CallWhen
       * 		string-length(normalize-space(description)) != 3
       */
      public void calculate() {
      }

      /**
       * @Action
       * @VisibleWhen({"a", "a", "b", "b", "javascript", "true"})
       */
      public void compositeVisibleWhen() {
      }

      /**
       * @DataProvider objectField=name
       * @CallWhen
       * 		string-length(normalize-space(description)) != 4
       */
      public List provideSomeList() {
         return new ArrayList();
      }

      /**
       * @DataProvider objectField=ids
       */
      public List provideAnotherList() {
         return new ArrayList();
      }

      /**
       * @DataProvider objectField=codes
       */
      public List provideCodesList() {
         return new ArrayList();
      }

      public List getCodes() {
         return codes;
      }

      public void setCodes(List codes) {
         this.codes = codes;
      }

      public Long[] getIds() {
         return ids;
      }

      public void setIds(Long[] ids) {
         this.ids = ids;
      }
   }
   
   /**
    * @Form
    */
   public static class NameClashForm {
      public Object getName() {
         return null;
      }

      /**
       * @Action
       */
      public void name() {
      }
   }
   
   /**
    * @Form
    */
   public static class OverriddenForm extends FooForm {
      /**
       * @NotBound
       */
      public String getCode() {
         return super.getCode();
      }
   }

   /**
    * @Form
    */
   public static class BrokenObjectFieldForm {
      public Object getObjectField() {
         return null;
      }

      /**
       * @DataProvider objectField=objectField
       */
      public List aProvider() {
         return null;
      }
   }

   /**
    * @Form
    */
   public static class BrokenIndexFieldForm {
      public void setIndexField(Object o) {
      }

      /**
       * @DataProvider indexField=indexField
       */
      public List aProvider() {
         return null;
      }
   }

   /**
    * @Form
    */
   public static class BrokenEnabledWhenForm {
      /**
       * @Action
       * @EnabledWhen
       */
      public void action() {
      }
   }
}
