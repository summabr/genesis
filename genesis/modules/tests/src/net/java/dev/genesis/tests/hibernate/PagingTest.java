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

import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.tests.TestCase;


public class PagingTest extends TestCase {

	private final DbActions dbActions = new DbActions();

	public void tearDown() throws Exception {
		dbActions.deleteAll();
	}

	public void testEmptyPage() throws Exception {
		Page page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingCriteria(5, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(5, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testOnePage() throws Exception {
		dbActions.insert(7);
		Page page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingCriteria(20, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(20, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testMoreThanOnePage() throws Exception {
		dbActions.insert(17);
		Page page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(2, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(2, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testExactlyTwoPages() throws Exception {
		dbActions.insert(20);
		Page page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(2, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(3, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(2, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(3, 10);
		assertEquals(page.getPageNumber(), 2);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testPageNotExists() throws Exception {
		Page page = dbActions.getPageUsingCriteria(5, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 0);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(5, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 0);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

}