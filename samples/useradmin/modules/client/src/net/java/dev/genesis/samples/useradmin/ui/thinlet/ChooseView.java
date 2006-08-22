/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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

public class ChooseView extends BaseView {

   public ChooseView() throws Exception {
      super("ChooseView.title", "choose-view.xml", 210, 90, false);
   }

   protected void onClose() {
      System.exit(0);
   }

   public void onThinlet() throws Exception {
      getFrame().dispose();
      new net.java.dev.genesis.samples.useradmin.ui.thinlet.UserListView()
            .display();
   }

   public void onSwing() throws Exception {
      getFrame().dispose();
      new net.java.dev.genesis.samples.useradmin.ui.swing.UserListView()
            .display();
   }

   public void onSWT() throws Exception {
      getFrame().dispose();
      new net.java.dev.genesis.samples.useradmin.ui.swt.UserListView()
            .display();
   }

   public void onExit() {
      onClose();
   }
}