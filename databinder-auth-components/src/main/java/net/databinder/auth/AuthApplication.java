package net.databinder.auth;

import java.security.MessageDigest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.cookies.CookieDefaults;

import net.databinder.auth.data.DataUser;

/**
 * Application-specific authorization settings. Many components of Databinder authentication
 * require that this be implemented by the current WebApplication instance.
 * @author Nathan Hamblen
 */
public interface AuthApplication<T extends DataUser> {
	/**
	 * @return class to be used for signed in users
	 */
	Class<T> getUserClass();
	/** 
	 * @return DataUser for the given username. 
	 */
	T getUser(String username);
	/**
	 * @return page to sign in users
	 */
	Class< ? extends WebPage> getSignInPageClass();
	/**
	 * Cryptographic salt to be used in authentication. The default getDigest()
	 * implementation uses this value.
	 * @return app-specific salt
	 */
	abstract byte[] getSalt();
	
	/** @return application-salted hashing digest */
	MessageDigest getDigest();
	
	/**
	 * Get the restricted token for a user, passing an appropriate location parameter. 
	 * @param user source of token
	 * @return restricted token
	 */
	String getToken(T user);
	
	/**
	 * @return the cookie settings for the signin (aka. remember-me) cookies.
	 */
	CookieDefaults getSignInCookieDefaults();
}
