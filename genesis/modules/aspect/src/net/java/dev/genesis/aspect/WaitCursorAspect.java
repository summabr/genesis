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

import java.awt.Cursor;

import net.java.dev.genesis.ui.thinlet.BaseThinlet;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

public class WaitCursorAspect {
   public Object waitCursorExecution(final JoinPoint jp) throws Throwable {
      final BaseThinlet o = (BaseThinlet) jp.getTargetInstance();
      final Cursor oldCursor = o.getCursor();

      Object ret = null;

      try {
         o.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         ret = jp.proceed();
      } finally {
         o.setCursor(oldCursor);
      }

      return ret;
   }
}