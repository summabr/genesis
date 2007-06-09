/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.resolvers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.GenesisTestCase;

public class EmptyResolverRegistryTest extends GenesisTestCase {
   public void testGetDefaultEmptyResolverForClass() {
      assertEquals(DefaultEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Object.class).getClass());
      assertEquals(StringEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(String.class).getClass());
      assertEquals(CollectionEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Collection.class).getClass());
      assertEquals(MapEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Map.class).getClass());
   }

   public void testGetDefaultEmptyResolverForClassMap() {
      assertEquals(StringEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(String.class, null)
            .getClass());

      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEmptyResolver)EmptyResolverRegistry.getInstance()
            .getDefaultEmptyResolverFor(String.class, attributes)).isTrim());
   }

   public void testGetEmptyResolver() {
      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEmptyResolver)EmptyResolverRegistry.getInstance()
            .getEmptyResolver(StringEmptyResolver.class.getName(), attributes))
            .isTrim());
   }
}