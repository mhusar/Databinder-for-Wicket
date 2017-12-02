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

package net.databinder.hib.conv;

import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;

import net.databinder.hib.DataRequestCycleListener;

/**
 * Listener for data conversations (see {@link DataConversationRequestCycle}).
 * 
 * @author Conny Kuehne
 */
public class DataConversationRequestCycleListener extends DataRequestCycleListener {
	
	/**
	 * Adds {@link PageRequestHandlerTracker} to allow for retrieving the response page for the request.
	 */
	@Override
	public void onBeginRequest(RequestCycle cycle) {
		cycle.getListeners().add(new PageRequestHandlerTracker());
	}

	/**
	 * Called by DataStaticService when a session is needed and does not already exist. Opens a new thread-bound
	 * Hibernate session.
	 */
	@Override
	public void dataSessionRequested(Object key) {
		RequestCycle cycle = RequestCycle.get();
		DataConversationRequestCycle dataRequestCycle = (DataConversationRequestCycle) cycle.getMetaData(dataRequestCycleKey);
		if (dataRequestCycle == null) {
			dataRequestCycle = new DataConversationRequestCycle();
			cycle.setMetaData(dataRequestCycleKey, dataRequestCycle);
		}
		dataRequestCycle.dataSessionRequested(key);
	}
}
