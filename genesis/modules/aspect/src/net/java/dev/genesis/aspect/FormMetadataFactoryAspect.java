/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.aspect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.util.GenesisUtils;
import net.java.dev.reusablecomponents.lang.Enum;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.annotation.Annotation;
import org.codehaus.aspectwerkz.annotation.AnnotationInfo;
import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotationProxy;

public class FormMetadataFactoryAspect {
   protected final CrossCuttingInfo ccInfo;

   public FormMetadataFactoryAspect(final CrossCuttingInfo ccInfo) {
      this.ccInfo = ccInfo;
   }

   /**
    * @Introduce formMetadataFactoryIntroduction deploymentModel=perJVM
    */
   public static class AspectFormMetadataFactory implements FormMetadataFactory {
      public interface AnnotationHandler {
         public void processFormAnnotation(final FormMetadata formMetadata,
               final Annotation annotation);

         public void processFieldAnnotation(final FormMetadata formMetadata,
               final FieldMetadata fieldMetadata, final Annotation annotation);

         public void processActionAnnotation(final FormMetadata formMetadata,
               final ActionMetadata actionMetadata, final Annotation annotation);
      }

      public static class MetadataAttribute extends Enum {
         public static class ConditionAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {

               final String value = ((UntypedAnnotationProxy) annotation)
                     .getValue();
               final int equalIndex = value.indexOf('=');
               if (equalIndex > 0) {
                  formMetadata.addNamedCondition(
                        value.substring(0, equalIndex), JXPathContext
                              .compile(value.substring(equalIndex + 1)));
               }
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processFormAnnotation(formMetadata, annotation);
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               processFormAnnotation(formMetadata, annotation);
            }
         }

         public static class EnabledWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EnabledWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processMemberAnnotation(formMetadata, fieldMetadata, annotation);
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               processMemberAnnotation(formMetadata, actionMetadata, annotation);
            }

            private void processMemberAnnotation(
                  final FormMetadata formMetadata,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               memberMetadata
                     .setEnabledCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class VisibleWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "VisibleWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processMemberAnnotation(formMetadata, fieldMetadata, annotation);
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               processMemberAnnotation(formMetadata, actionMetadata, annotation);
            }

            private void processMemberAnnotation(
                  final FormMetadata formMetadata,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               memberMetadata
                     .setVisibleCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class ValidateBeforeAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ValidateBefore cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ValidateBefore cannot be a field annotation");
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               actionMetadata.setValidateBefore(annotation != null);
            }
         }

         public static class CallWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "CallWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "CallWhen cannot be a field annotation");
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               actionMetadata
                     .setCallCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class ClearOnAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ClearOn cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata
                     .setClearOnCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ClearOn cannot be an action annotation");
            }
         }

         public static class DisplayOnlyAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "DisplayOnly cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setDisplayOnly(annotation != null);
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "DisplayOnly cannot be an action annotation");
            }
         }

         public static class EqualityComparatorAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EqualityComparator cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setEqualityComparator(getEqualityComparator(
                     fieldMetadata, ((UntypedAnnotationProxy) annotation)
                           .getValue()));
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EqualityComparator cannot be an action annotation");
            }

            private EqualityComparator getEqualityComparator(
                  final FieldMetadata fieldMetadata, final String attributesLine) {
               final Map attributesMap = GenesisUtils
                     .getAttributesMap(attributesLine);
               final String comparatorClass = (String) attributesMap
                     .remove("class");
               if (comparatorClass == null) {
                  return EqualityComparatorRegistry.getInstance()
                        .getDefaultEqualityComparatorFor(
                              fieldMetadata.getFieldClass(), attributesMap);
               }
               return EqualityComparatorRegistry.getInstance()
                     .getEqualityComparator(comparatorClass, attributesMap);
            }
         }

         public static class EmptyResolverAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyResolver cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setEmptyResolver(getEmptyResolver(fieldMetadata,
                     ((UntypedAnnotationProxy) annotation).getValue()));
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyResolver cannot be an action annotation");
            }

            private EmptyResolver getEmptyResolver(
                  final FieldMetadata fieldMetadata, final String attributesLine) {
               final Map attributesMap = GenesisUtils
                     .getAttributesMap(attributesLine);
               final String resolverClass = (String) attributesMap
                     .remove("class");
               if (resolverClass == null) {
                  return EmptyResolverRegistry.getInstance()
                        .getDefaultEmptyResolverFor(
                              fieldMetadata.getFieldClass(), attributesMap);
               }
               return EmptyResolverRegistry.getInstance().getEmptyResolver(
                     resolverClass, attributesMap);
            }
         }

         public static class EmptyValueAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyValue cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               final Converter converter = ConvertUtils.lookup(fieldMetadata
                     .getFieldClass());
               fieldMetadata.setConverter(converter);
               fieldMetadata.setEmptyValue(converter.convert(fieldMetadata
                     .getFieldClass(), ((UntypedAnnotationProxy) annotation)
                     .getValue()));
            }

            public void processActionAnnotation(
                  final FormMetadata formMetadata,
                  final ActionMetadata actionMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyValue cannot be an action annotation");
            }

         }

         public static final MetadataAttribute CONDITION = new MetadataAttribute(
               "Condition", new ConditionAnnotationHandler());
         public static final MetadataAttribute ENABLED_WHEN = new MetadataAttribute(
               "EnabledWhen", new EnabledWhenAnnotationHandler());
         public static final MetadataAttribute VISIBLE_WHEN = new MetadataAttribute(
               "VisibleWhen", new VisibleWhenAnnotationHandler());
         public static final MetadataAttribute VALIDATE_BEFORE = new MetadataAttribute(
               "ValidateBefore", new ValidateBeforeAnnotationHandler());
         public static final MetadataAttribute CALL_WHEN = new MetadataAttribute(
               "CallWhen", new CallWhenAnnotationHandler());
         public static final MetadataAttribute CLEAR_ON = new MetadataAttribute(
               "ClearOn", new ClearOnAnnotationHandler());
         public static final MetadataAttribute DISPLAY_ONLY = new MetadataAttribute(
               "DisplayOnly", new DisplayOnlyAnnotationHandler());
         public static final MetadataAttribute EQUALITY_COMPARATOR = new MetadataAttribute(
               "EqualityComparator", new EqualityComparatorAnnotationHandler());
         public static final MetadataAttribute EMPTY_RESOLVER = new MetadataAttribute(
               "EmptyResolver", new EmptyResolverAnnotationHandler());
         public static final MetadataAttribute EMPTY_VALUE = new MetadataAttribute(
               "EmptyValue", new EmptyValueAnnotationHandler());

         private final AnnotationHandler handler;

         public MetadataAttribute(String name, AnnotationHandler handler) {
            super(name);
            this.handler = handler;
         }

         public AnnotationHandler getHandler() {
            return handler;
         }

         public static MetadataAttribute get(String name) {
            return (MetadataAttribute) Enum.get(MetadataAttribute.class, name);
         }
      }

      private final Map cache = new HashMap();

      public FormMetadata getFormMetadata(final Class formClass) {
         FormMetadata formMetadata = (FormMetadata) cache.get(formClass);
         if (formMetadata == null) {
            formMetadata = new FormMetadata(formClass);
            processAnnotations(formMetadata);
            cache.put(formClass, formMetadata);
         }
         return formMetadata;
      }

      private void processAnnotations(final FormMetadata formMetadata) {
         processFormAnnotations(formMetadata);
         processFieldsAnnotations(formMetadata);
         processActionsAnnotations(formMetadata);
      }

      private void processFormAnnotations(final FormMetadata formMetadata) {
         final List annotations = Annotations.getAnnotationInfos(formMetadata
               .getFormClass());
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());
            if (attr != null) {
               attr.getHandler().processFormAnnotation(formMetadata,
                     info.getAnnotation());
            }

         }
      }

      private void processFieldsAnnotations(final FormMetadata formMetadata) {
         final PropertyDescriptor[] propertyDescriptors = PropertyUtils
               .getPropertyDescriptors(formMetadata.getFormClass());
         PropertyDescriptor propDesc;
         FieldMetadata fieldMetadata;
         for (int i = 0; i < propertyDescriptors.length; i++) {
            propDesc = propertyDescriptors[i];
            // Ignoring java.lang.Object.getClass()
            if (!propDesc.getName().equals("class")
                  && propDesc.getReadMethod() != null) {
               fieldMetadata = new FieldMetadata(propDesc.getName(), propDesc
                     .getPropertyType());
               formMetadata.addFieldMetadata(propDesc.getName(), fieldMetadata);
               processFieldAnnotations(formMetadata, fieldMetadata, propDesc
                     .getReadMethod());
            }
         }
      }

      private void processFieldAnnotations(final FormMetadata formMetadata,
            final FieldMetadata fieldMetadata, final Method fieldGetterMethod) {
         final List annotations = Annotations
               .getAnnotationInfos(fieldGetterMethod);
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());
            if (attr != null) {
               attr.getHandler().processFieldAnnotation(formMetadata,
                     fieldMetadata, info.getAnnotation());
            }
         }
      }

      private void processActionsAnnotations(final FormMetadata formMetadata) {
         Method[] methods = formMetadata.getFormClass().getDeclaredMethods();
         ActionMetadata actionMetadata;
         for (int i = 0; i < methods.length; i++) {
            if (Annotations.getAnnotation("Action", methods[i]) != null) {
               actionMetadata = new ActionMetadata(methods[i]);
               formMetadata.addActionMetadata(methods[i], actionMetadata);
               processActionAnnotations(formMetadata, actionMetadata,
                     methods[i]);
            }
         }
      }

      private void processActionAnnotations(final FormMetadata formMetadata,
            final ActionMetadata actionMetadata, final Method actionMethod) {
         final List annotations = Annotations.getAnnotationInfos(actionMethod);
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());
            if (attr != null) {
               attr.getHandler().processActionAnnotation(formMetadata,
                     actionMetadata, info.getAnnotation());
            }
         }
      }

   }
}