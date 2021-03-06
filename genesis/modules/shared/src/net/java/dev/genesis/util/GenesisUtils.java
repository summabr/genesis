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
package net.java.dev.genesis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class GenesisUtils {

   public static Map normalizeMap(final Map map) {
      map.remove("class"); // NOI18N
      map.remove("context"); // NOI18N
      return map;
   }

   /**
    * Reads a string line containing <code>key1=value1 key2=value2</code>
    * and makes a Map.
    * 
    * @param attributesLine
    * @return
    */
   public static Map getAttributesMap(final String attributesLine) {
      return getAttributesMap(attributesLine, " \t\n\r\f", "="); // NOI18N
   }
   
   public static Map getAttributesMap(final String attributesLine, String pairsDelim, String keyValueDelim) {
      final StringTokenizer pairsTok = new StringTokenizer(attributesLine, pairsDelim);
      final Map attributesMap = new HashMap();
      while (pairsTok.hasMoreTokens()) {
         final StringTokenizer keyValueTok = new StringTokenizer(
               pairsTok.nextToken(), keyValueDelim);
         if (keyValueTok.countTokens() == 2) {
            attributesMap.put(keyValueTok.nextToken().trim(), keyValueTok
                  .nextToken().trim());
         }
      }
      return attributesMap;
   }
   
   public static Map getAttributesMap(String[] values) {
      final Map attributesMap = new HashMap(values.length);
      for (int i = 0; i < values.length; i++) {
         String[] value = values[i].split("\\s*=\\s*"); // NOI18N
        
         if (value.length != 2) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  GenesisUtils.class, "INVALID_ARGUMENT_X", values[i])); // NOI18N
         }
         attributesMap.put(value[0].trim(), value[1].trim());
      }
      return attributesMap;
   }
   
   public static boolean isBlank(String value) {
      return value == null || value.trim().length() == 0;
   }
}
