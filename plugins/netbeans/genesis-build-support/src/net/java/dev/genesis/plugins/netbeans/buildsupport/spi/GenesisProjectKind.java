/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.plugins.netbeans.buildsupport.spi;

import net.java.dev.reusablecomponents.lang.Enum;

public class GenesisProjectKind extends Enum {
   public static final GenesisProjectKind DESKTOP = new GenesisProjectKind("desktop");
   public static final GenesisProjectKind FREEFORM = new GenesisProjectKind("freeform");
   public static final GenesisProjectKind WEB = new GenesisProjectKind("web");
   
   private GenesisProjectKind(String name) {
      super(name);
   }
}