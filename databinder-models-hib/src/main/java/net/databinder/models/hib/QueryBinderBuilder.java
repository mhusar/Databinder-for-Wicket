/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006 Nathan Hamblen nathan@technically.us
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.databinder.models.hib;

import org.hibernate.query.Query;
import org.hibernate.Session;

/**
 * Converts a string and QueryBinder(s) into a builder so only builders need to
 * be dealt with.
 */
public class QueryBinderBuilder<R> implements QueryBuilder<R> {

	private static final long serialVersionUID = 1L;
	private String query;
	private QueryBinder<R>[] binders;
	private Class<R> queryResultType;

	/**
	 * Constructs.
	 * 
	 * @param query
	 * @param queryResultType
	 * @param binders
	 */
	@SafeVarargs
	public QueryBinderBuilder(Class<R> queryResultType, String query, QueryBinder<R>... binders) {
		this.query = query;
		this.binders = binders;
		this.queryResultType = queryResultType;
	}

	public Query<R> build(Session sess) {
		Query<R> q = sess.createQuery(query, queryResultType);
		for (QueryBinder<R> b : binders)
			b.bind(q);
		return q;
	}
}
