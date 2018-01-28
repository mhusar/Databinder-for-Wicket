package net.databinder.models.hib;

import java.io.Serializable;

import org.hibernate.query.Query;

/**
 * Binds a query's parameters to the properties of an object, as needed.
 * 
 * @param R the result type of the query
 * @param T the type of the object to bind
 * 
 * @author Jonathan
 * @author ckuehne
 */
public class PropertyQueryBinder<R, T extends Serializable> extends AbstractPropertyQueryBinder<R, T> {

	private static final long serialVersionUID = 1L;

	private final T t;

	/**
	 * @param object
	 *            The object to bind properties of
	 */
	public PropertyQueryBinder(final T t) {
		this.t = t;
	}

	/**
	 * @param query
	 *            The query to bind
	 */
	public void bind(final Query<R> query) {
		bind(query, t);
	}
}