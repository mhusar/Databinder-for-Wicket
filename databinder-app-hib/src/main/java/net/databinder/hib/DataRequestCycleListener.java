/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * Note: this class contains code adapted from wicket-contrib-database. 
 */

package net.databinder.hib;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * <p>
 * Opens Hibernate sessions and transactions as required and closes them at a request's end. Uncomitted transactions are
 * rolled back. Uses keyed Hibernate session factories from Databinder service.
 * </p>
 * 
 * @author Conny Kuehne
 */
public class DataRequestCycleListener extends AbstractRequestCycleListener implements HibernateSessionStarter {

	/**
	 * Meta data key for the keys for session factories that have been opened for this request
	 */
	protected final DataRequestCycleKey<DataRequestCycle> dataRequestCycleKey = new DataRequestCycleKey<>();
	
	private static class DataRequestCycleKey<T extends DataRequestCycle> extends MetaDataKey<T> {
		private static final long serialVersionUID = 1L;
	}


	/**
	 * Called by DataStaticService when a session is needed and does not already exist. Opens a new thread-bound
	 * Hibernate session.
	 */
	public void dataSessionRequested(Object key) {
		RequestCycle cycle = RequestCycle.get();
		DataRequestCycle dataRequestCycle = cycle.getMetaData(dataRequestCycleKey);
		if (dataRequestCycle == null) {
			dataRequestCycle = new DataRequestCycle();
			cycle.setMetaData(dataRequestCycleKey, dataRequestCycle);
		}
		dataRequestCycle.dataSessionRequested(key);
	}

	/**
	 * Closes all Hibernate sessions opened for this request. If a transaction has not been committed, it will be rolled
	 * back before closing the session.
	 * 
	 * @see net.databinder.components.hib.DataForm#onSubmit()
	 */
	@Override
	public void onEndRequest(RequestCycle cycle) {
		DataRequestCycle dataRequestCycle = getDataRequestCycle(cycle);
		if (dataRequestCycle != null) {
			dataRequestCycle.onEndRequest();
		}
	}

	/**
	 * Closes and reopens sessions for this request cycle. Unrelated models may try to load themselves after this point.
	 */
	// TODO [migration]: test!
	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		DataRequestCycle dataRequestCycle = getDataRequestCycle(cycle);
		if (dataRequestCycle != null) {
			dataRequestCycle.onException(ex);
		}
		return null;
	}
	

	public DataRequestCycle getDataRequestCycle(RequestCycle cycle) {
		return cycle.getMetaData(dataRequestCycleKey);
	}
	
}
