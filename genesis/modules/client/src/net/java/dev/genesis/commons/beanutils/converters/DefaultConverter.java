/*
 * The Genesis Project
 * Copyright (C) 2005-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.beanutils.converters;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class DefaultConverter implements Converter {
   private final boolean returnDefaultValue;
   private final Object defaultValue;

   public DefaultConverter() {
      this(true, null);
   }

   public DefaultConverter(final Object defaultValue) {
      this(true, defaultValue);
   }

   public DefaultConverter(final boolean returnDefaultValue) {
      this(returnDefaultValue, null);
   }

   protected DefaultConverter(final boolean returnDefaultValue,
         final Object defaultValue) {
      this.returnDefaultValue = returnDefaultValue;
      this.defaultValue = defaultValue;
   }

   public Object convert(Class clazz, Object obj) {
      if (obj == null) {
         return null;
      }

      if (clazz.isAssignableFrom(obj.getClass())) {
         return obj;
      }

      if (returnDefaultValue) {
         return defaultValue;
      }
      throw new ConversionException(Bundle.getMessage(DefaultConverter.class,
               "X_CANNOT_BE_CONVERTED_TO_Y_ITS_TYPE_IS_Z", new Object[]{ // NOI18N
            obj, clazz.getName(), obj.getClass().getName()}));
   }
}