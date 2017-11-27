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
package net.databinder.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

/**
 * Creates web response objects that do not rewrite URLs for cookieless support. Buffered or
 * basic responses are created according to the application configuration. This factory is
 * used by DataApplicationBase when cookieless support is off, but may also be used independently.
 * @see net.databinder.DataApplicationBase
 * @author Nathan Hamblen
 */
public class NorewriteWebResponse {

	public static WebResponse getNew(Application app, final WebRequest webRequest,
			final HttpServletResponse httpServletResponse) {
		// TODO [migration]: does unbuffered case work?
		return app.getRequestCycleSettings().getBufferResponse() ? 
				new Buffered(webRequest, httpServletResponse) : 
				new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse);
	}
	
	static class Buffered extends BufferedWebResponse {
		public Buffered(final WebRequest webRequest, final HttpServletResponse httpServletResponse) {
			// TODO [migration]: does this work?
			super(new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse));
		}

		@Override
		public String encodeURL(CharSequence url) {
			return url.toString();
		}
	}
	// FIXME [migration]
//	static class Unbuffered extends WebResponse {
//		public Unbuffered(final HttpServletResponse httpServletResponse)
//		{ 
//			super(httpServletResponse); 
//		}
//		@Override
//		public CharSequence encodeURL(CharSequence url) {
//			return url;
//		}
//	}
}
