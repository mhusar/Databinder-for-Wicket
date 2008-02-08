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
package net.databinder.auth.hib;

import javax.servlet.http.HttpServletRequest;

import net.databinder.auth.AuthApplication;
import net.databinder.auth.AuthSession;
import net.databinder.auth.components.hib.DataSignInPage;
import net.databinder.auth.data.DataUser;
import net.databinder.auth.data.UserBase;
import net.databinder.hib.DataApplication;
import net.databinder.hib.Databinder;

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequest;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;

/**
 * Adds basic authentication functionality to DataApplication. This class is a derivative
 * of Wicket's AuthenticatedWebApplication, brought into the DataApplication hierarchy
 * and including light user specifications in IUser. You are encouraged to override
 * getUserClass() to implement your own user entity, possibly by extending UserBase.
 * It is also possible to use Databinder authentication without extending this base class 
 * by implementing IAuthSettings.
 * <p>Text appearing in authentication components can be overriden for any language, using
 * resource keys listed in their documentation. Except as otherwise noted, these resources
 * can be housed in the application class's properties file, so that subclasses of  the pages
 * and panels are not necessarily required.
 * @see IAuthSettings
 * @see IUser
 * @see UserBase
 * @author Nathan Hamblen
 */
public abstract class AuthDataApplication extends DataApplication 
implements IUnauthorizedComponentInstantiationListener, IRoleCheckingStrategy, AuthApplication {

	/**
	 * Internal initialization. Client applications should not normally override
	 * or call this method.
	 */
	@Override
	protected void internalInit() {
		super.internalInit();
		authInit();
	}
	
	/**
	 * Sets Wicket's security strategy for role authorization and appoints this 
	 * object as the unauthorized instatiation listener. Called automatically on start-up.
	 */
	protected void authInit() {
		getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
		getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
	}

	/**
	 * @return new AuthDataSession
	 * @see AuthDataSession
	 */
	@Override
	public Session newSession(Request request, Response response) {
		return new AuthDataSession(request);
	}
	/**
	 * Adds to the configuration whatever IUser class is defined.
	 */
	@Override
	protected void configureHibernate(AnnotationConfiguration config) {
		super.configureHibernate(config);
		config.addAnnotatedClass(getUserClass());
	}
	
	/**
	 * Sends to sign in page if not signed in, otherwise throws UnauthorizedInstantiationException.
	 */
	public void onUnauthorizedInstantiation(Component component) {
		if (((AuthSession)Session.get()).isSignedIn()) {
			throw new UnauthorizedInstantiationException(component.getClass());
		}
		else {
			throw new RestartResponseAtInterceptPageException(getSignInPageClass());
		}	
	}
	
	/**
	 * Passes query on to the IUser object if signed in.
	 */
	public final boolean hasAnyRole(Roles roles) {
		DataUser user = ((AuthSession)Session.get()).getUser();
		return user == null ? false : user.hasAnyRole(roles);
	}

	/**
	 * Please override to use your own IUser implementation. This base implementation 
	 * will be removed in a future version.
	 * @return class to be used for signed in users
	 */
	@SuppressWarnings("deprecation")
	public abstract Class< ? extends DataUser> getUserClass();
	
	/**
	 * Return user object by matching against a "username" property. Override
	 * if you have a differently named property.
	 * @return IUser for the given username. 
	 */
	public DataUser getUser(String username) {
		return (DataUser) Databinder.getHibernateSession().createCriteria(getUserClass())
			.add(Restrictions.eq("username", username)).uniqueResult();
	}

	/**
	 * Override if you need to customize the sign-in page.
	 * @return page to sign in users
	 */
	public Class< ? extends WebPage> getSignInPageClass() {
		return DataSignInPage.class;
	}
	
	/**
	 * Get the restricted token for a user, using IP addresses as location parameter. This implementation
	 * combines the "X-Forwarded-For" header with the remote address value so that unique
	 * values result with and without proxying. (The forwarded header is not trusted on its own
	 * because it can be most easily spoofed.)
	 * @param user source of token
	 * @return restricted token
	 */
	public String getToken(DataUser.CookieAuth user) {
		HttpServletRequest req = ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest();
		String fwd = req.getHeader("X-Forwarded-For");
		if (fwd == null)
			fwd = "nil";
		return user.getToken(fwd + "-" + req.getRemoteAddr());
	}
	
	/**
	 * Cryptographic salt to be used in authentication. The default IUser
	 * implementation uses this value. If your implementation does not require
	 * a salt value (!), return null.
	 * @return application-specific salt
	 */
	public abstract byte[] getSalt();
}
