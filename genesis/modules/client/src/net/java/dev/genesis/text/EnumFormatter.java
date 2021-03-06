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
package net.java.dev.genesis.text;

import java.util.MissingResourceException;

import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.ui.UIUtils;
import org.apache.commons.logging.LogFactory;

public class EnumFormatter implements Formatter {
   public String format(final Object o) {
      if (o == null) {
         return ""; // NOI18N
      }

      String className = EnumHelper.getInstance().getDeclaringClass(o).getName();
      className = className.substring(className.lastIndexOf('.') + 1);
      className = className.substring(className.lastIndexOf('$') + 1);

      final String key = className + '.' + EnumHelper.getInstance().getName(o);

      try {
         return UIUtils.getInstance().getBundle().getString(key);
      } catch (MissingResourceException mre) {
         LogFactory.getLog(getClass()).info("Not found: " + key); // NOI18N
         return EnumHelper.getInstance().getName(o);
      }
   }
}