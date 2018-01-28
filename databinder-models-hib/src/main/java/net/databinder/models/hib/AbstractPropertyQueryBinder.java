package net.databinder.models.hib;


import org.apache.wicket.core.util.lang.PropertyResolver;
import org.hibernate.query.Query;

/**
 * Base class for classes that bind queries using object properties.
 * 
 * @author Jonathan
 */
public abstract class AbstractPropertyQueryBinder<R, T> implements QueryBinder<R> {

	private static final long serialVersionUID = 145077736634107819L;

	/**
	 * @param query
	 *            The query to bind
	 * @param object
	 *            The object to pull properties from
	 */
	protected void bind(final Query<R> query, final T t) {
		for (final String parameter : query.getParameterMetadata().getNamedParameterNames()) {
			query.setParameter(parameter, PropertyResolver.getValue(parameter, t));
		}
	}
}
