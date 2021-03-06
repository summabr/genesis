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
package net.java.dev.genesis.samples.useradmin.ui.thinlet;

import java.awt.Frame;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.BaseDialogThinlet;

public abstract class BaseDialogView extends BaseDialogThinlet {
   public BaseDialogView(Frame frame, String title, String xmlFile, boolean resizable,
         boolean modal) throws Exception {
      super(frame);
      getDialog().setModal(modal);
      getDialog().setResizable(resizable);
      getDialog().setTitle(UIUtils.getInstance().getBundle().getString(title));
      setAllI18n(true);
      setResourceBundle(UIUtils.getInstance().getBundle());
      add(parse(xmlFile));
   }
}