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

import net.java.dev.genesis.command.hibernate.AbstractHibernateCommand;
import net.java.dev.genesis.paging.Page;


public class DbActions extends AbstractHibernateCommand {

	/**
	 * @Transactional
	 */
	public int deleteAll() throws Exception {
		return getSession().delete("from HibernateBean");
	}

	/**
	 * @Transactional
	 */
	public void insert(int number) throws Exception {
		HibernateBean bean;
		for (int i = 0; i < number; i++) {
			bean = new HibernateBean();
			bean.setCode(String.valueOf(i));
			getSession().save(bean);
		}
	}

	/**
	 * @Remotable
	 */
	public Page getPageUsingCriteria(int pageNumber, int resultsPerPage)
			throws Exception {
		return getPage(getSession().createCriteria(HibernateBean.class),
				pageNumber, resultsPerPage);
	}

	/**
	 * @Remotable
	 */
	public Page getPageUsingQuery(int pageNumber, int resultsPerPage)
			throws Exception {
		return getPage(getSession().createQuery("from HibernateBean"),
				pageNumber, resultsPerPage);
	}
}