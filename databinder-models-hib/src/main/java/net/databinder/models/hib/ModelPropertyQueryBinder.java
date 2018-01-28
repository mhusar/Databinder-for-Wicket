package net.databinder.models.hib;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.hibernate.query.Query;

/**
 * A query binder that sets query parameters to corresponding properties taken
 * from the given Wicket model object.
 * 
 * @author Jonathan
 */
public class ModelPropertyQueryBinder<R, T> extends AbstractPropertyQueryBinder<R, T>
		implements IDetachable {

	private static final long serialVersionUID = -6544558086991812867L;

	protected final IModel<T> model;

	/**
	 * 
	 * @param model the model
	 */
	public ModelPropertyQueryBinder(final IModel<T> model) {
		this.model = model;
	}

	public void detach() {
		model.detach();
	}

	public void bind(final Query<R> query) {
		bind(query, model.getObject());
	}
}