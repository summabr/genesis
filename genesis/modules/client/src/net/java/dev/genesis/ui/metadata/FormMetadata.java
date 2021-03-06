/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptExpression;

public class FormMetadata {
   private final Class formClass;
   private final Script script;

   private final Map namedConditions;
   private final Map fieldMetadatas;
   private final Map methodMetadatas;
   private Map actionMetadatas;
   private Map dataProviderMetadatas;
   private Map dataProviderIndexes;

   public FormMetadata(final Class formClass, final Script script) {
      this.formClass = formClass;
      this.script = script;
      this.namedConditions = new HashMap();
      this.fieldMetadatas = new HashMap();
      this.methodMetadatas = new HashMap();
   }

   public Class getFormClass() {
      return formClass;
   }

   public Script getScript() {
      return script;
   }

   public Map getFieldMetadatas() {
      return new HashMap(fieldMetadatas);
   }

   public Map getNamedConditions() {
      return new HashMap(namedConditions);
   }

   public Map getMethodMetadatas(){
      return new HashMap(methodMetadatas);
   }

   public Map getActionMetadatas() {
      if (actionMetadatas == null) {
         actionMetadatas = new HashMap();
         for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
            MethodMetadata methodMeta = (MethodMetadata) iter.next();
            if (methodMeta.getActionMetadata() == null) {
               continue;
            }
            actionMetadatas.put(methodMeta.getMethodEntry(), methodMeta.getActionMetadata());
         }
      }
      return new HashMap(actionMetadatas);
   }

   public Map getDataProviderMetadatas() {
      if (dataProviderMetadatas == null || dataProviderIndexes == null) {
         initializeDataProviders();
      }
      return new HashMap(dataProviderMetadatas);
   }

   public Map getDataProviderIndexes() {
      if (dataProviderMetadatas == null || dataProviderIndexes == null) {
         initializeDataProviders();
      }
      return new HashMap(dataProviderIndexes);
   }

   private void initializeDataProviders() {
      dataProviderMetadatas = new HashMap();
      dataProviderIndexes = new HashMap();

      MethodMetadata methodMeta;
      DataProviderMetadata dataMeta;

      for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
         methodMeta = (MethodMetadata) iter.next();
         dataMeta = methodMeta.getDataProviderMetadata();

         if (dataMeta == null) {
            continue;
         }

         dataProviderMetadatas.put(methodMeta.getMethodEntry(), dataMeta);

         if (dataMeta.getIndexField() == null) {
            continue;
         }

         dataProviderIndexes.put(dataMeta.getIndexField().getFieldName(), dataMeta);
      }
   }

   public ScriptExpression getNamedCondition(final String namedConditionName) {
      return (ScriptExpression) namedConditions.get(namedConditionName);
   }

   public FieldMetadata getFieldMetadata(final String fieldName) {
      return (FieldMetadata) fieldMetadatas.get(fieldName);
   }
   
   public MethodMetadata getMethodMetadata(final MethodEntry methodEntry) {
      return (MethodMetadata) methodMetadatas.get(methodEntry);
   }
   
   public MethodMetadata getMethodMetadata(final String methodName) {
      return (MethodMetadata) methodMetadatas.get(new MethodEntry(methodName, new String[0]));
   }

   public DataProviderMetadata getDataProviderForIndex(final String fieldName) {
      return (DataProviderMetadata) dataProviderIndexes.get(fieldName);
   }

   public void addNamedCondition(final String key, final ScriptExpression value) {
      namedConditions.put(key, value);
   }

   public void addFieldMetadata(final String fieldName,
         final FieldMetadata fieldMetadata) {
      fieldMetadatas.put(fieldName, fieldMetadata);
   }
   
   public void addMethodMetadata(final Method method, final MethodMetadata methodMetadata) {
      methodMetadatas.put(new MethodEntry(method), methodMetadata);
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(" = {\n"); // NOI18N

      for (Iterator iter = namedConditions.entrySet().iterator(); iter
            .hasNext();) {
         Map.Entry element = (Map.Entry) iter.next();
         buffer.append("\t"); // NOI18N
         buffer.append(element.getKey());
         buffer.append("="); // NOI18N
         buffer.append(element.getValue());
         buffer.append("\n"); // NOI18N
      }

      for (Iterator iter = fieldMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t"); // NOI18N
         buffer.append(iter.next());
         buffer.append("\n"); // NOI18N
      }
      
      for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t"); // NOI18N
         buffer.append(iter.next());
         buffer.append("\n"); // NOI18N
      }

      buffer.append("}"); // NOI18N

      return buffer.toString();
   }
}