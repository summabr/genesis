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
package net.java.dev.genesis.tests.hibernate;

import java.util.Iterator;
import java.util.List;


import net.java.dev.genesis.command.hibernate.AbstractHibernateCriteria;
import net.java.dev.genesis.helpers.CriteriaPropertyHelper;
import net.java.dev.genesis.tests.TestCase;
import net.sf.hibernate.expression.Expression;


public class HibernateCriteriaTest extends TestCase {

   private final DbActions dbActions = new DbActions();

   public void tearDown() throws Exception {
      dbActions.deleteAll();
   }

   public void test() throws Exception {
      dbActions.insert(3);
      CriteriaTester tester = new CriteriaTester();
      HibernateBeanForm form = new HibernateBeanForm();
      form.setCodigo("");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      List results = tester.find();
      assertEquals(results.size(), 3);

      form.setCodigo("2");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      for (Iterator iter = results.iterator(); iter.hasNext();) {
         System.out.println(iter.next());
         
      }
      assertEquals(results.size(), 1);

      form.setCodigo("abc");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      assertTrue(results.isEmpty());
   }

   public static class CriteriaTester extends AbstractHibernateCriteria {

      public void setCodigo(String codigo) {
         System.out.println("Setting codigo ......... " + codigo);
         getCriteria().add(Expression.eq("codigo", codigo));
      }

      /**
       * @Criteria net.java.dev.genesis.tests.hibernate.HibernateBean
       */
      public List find() throws Exception {
         return getCriteria().list();
      }

   }

   /**
    * @Form
    */
   public static class HibernateBeanForm {

      private Long pk;

      private String codigo;

      public String getCodigo() {
         return codigo;
      }

      public void setCodigo(String codigo) {
         this.codigo = codigo;
      }

      public Long getPk() {
         return pk;
      }

      public void setPk(Long pk) {
         this.pk = pk;
      }
   }

}