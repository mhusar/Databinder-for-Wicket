/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2008  Nathan Hamblen nathan@technically.us
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
package net.databinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.cookies.CookieDefaults;
import org.apache.wicket.util.cookies.CookieUtils;

/**
 * Request cycle with cookie convenience methods that reflects removal immediately.
 */
// TODO [migration]: maybe better solved with RequestCycleListener? Or with authenticationStrategy and without custom
// cookie handling.
public class CookieRequestCycle extends RequestCycle {
	public CookieRequestCycle(RequestCycleContext context) {
		super(context);
	}

	/** cache of cookies from request */ 
	private Map<String, Cookie> cookies;
	
	/** Cookie utils with default settings */
	private CookieUtils cookieUtils;

	/**
	 * Return or build cache of cookies cookies from request.
	 */
	protected Map<String, Cookie> getCookies() {
		if (cookies == null) {
			WebRequest webRequest = (WebRequest)RequestCycle.get().getRequest();
			List<Cookie> requestCookies = webRequest.getCookies();
			cookies = new HashMap<String, Cookie>(requestCookies.size());
			for (Cookie cookie : requestCookies) {
				cookies.put(cookie.getName(), cookie);
			}
		}
		return cookies;
	}

	/**
	 * Retrieve cookie from request, so long as it hasn't been cleared. Cookies  cleared by
	 * clearCookie() are still contained in the current request's cookie array, but this method
	 * will not return them.
	 * @param name cookie name
	 * @return cookie requested, or null if unavailable
	 */
	public Cookie getCookie(String name) {
		return getCookies().get(name);
	}
	
	/**
	 * Applies scope to cookies set by this application. Base implementation
	 * sets the path to / . Override if limiting scope to a path, or expanding
	 * it to a broader domain.
	 * @param cookie to have its scope set
	 */
	public void applyScope(Cookie cookie) {
		cookie.setPath("/");
	}

	/**
	 * Sets a new a cookie with an expiration time of zero to an clear an old one from the 
	 * browser, and removes any copy from this request's cookie cache. Subsequent calls to 
	 * <tt>getCookie(String name)</tt> during this request will not return a cookie of that name. 
	 * @param name cookie name
	 */
	public void clearCookie(String name) {
		
		// remove from local cache
		getCookies().remove(name); 
		
		// remove from browser
		CookieUtils cookieUtils = getCookieUtils();
		cookieUtils.remove(name);
	}
	
	/**
	 * Make sure you always return a valid CookieUtils
	 * 
	 * @return CookieUtils
	 */
	private CookieUtils getCookieUtils() {
		if (cookieUtils == null) {
			CookieDefaults settings = new CookieDefaults();
			settings.setHttpOnly(true);
			cookieUtils = new CookieUtils(settings);
		}
		return cookieUtils;
	}

}
