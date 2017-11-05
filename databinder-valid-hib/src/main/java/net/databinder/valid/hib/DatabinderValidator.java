/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2008  Nathan Hamblen nathan@technically.us

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
package net.databinder.valid.hib;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

/**
 * Checks a base model and property name against javax Validator.
 * @author Nathan Hamblen
 * @author Rodolfo Hansen
 * @author Conny Kuehne
 *
 * @param <T> Type parameter for the validator.
 * @deprecated Use wicket-bean-validation project instead
 */
public class DatabinderValidator<T>  extends Behavior implements INullAcceptingValidator<T> {
  private static final long serialVersionUID = 1L;

  /** base model, may be null until first call to onValidate. */
	private IModel<T> base;
	/** property of base to validate, may be null until first call to onValidate. */
	private String property;
	/** component added to */
	private FormComponent<T> component;

	/**
	 * Validator for a property of an entity.
	 * @param base entity to validate
	 * @param property property of base to validate
	 */
	public DatabinderValidator(final IModel<T> base, final String property) {
		this.base = base;
		this.property = property;
	}

	/**
	 * Construct instance that attempts to determine the base object and property
	 * to validate form the component it is added to. This is only possible for
	 * components that depend on a parent CompoundPropertyModel or their own
	 * PropertyModels. The attempt is not made until the first validation check
	 * in {@link #onValidate(IValidatable)} (to allow the full component
	 * hierarchy to be constructed). Do not use an instance for more than
	 * one component.
	 */
	public DatabinderValidator() { }

	/**
	 * Checks the component against Hibernate Validator. If the base model and property were not supplied in the
	 * constructor, they will be determined from the component this validator was added to.
	 */
	@SuppressWarnings("unchecked")
	public void validate(final IValidatable<T> comp) {
		if (base == null || property == null) {
			final ModelProp<T> mp = getModelProp(component);
			base = mp.model;
			property = mp.prop;
		}
		final T t = base.getObject();
		if (t==null)
			return;
		// TODO [migration]: avoid calling buildDefaultValidatorFactory often; see wicket-bean-validation project -> DefaultValidatorProvider
		Set<?> constraintViolations = Validation.buildDefaultValidatorFactory().getValidator().validateValue(t.getClass(), property, comp.getValue());
		
		for (ConstraintViolation<?> cv : (Set<ConstraintViolation<?>>) constraintViolations) {
			// TODO [migration]: try messageTemplate, e.g., javax.validation.constraints.NotNull.message
			comp.error(new ValidationError().setMessage(cv.getPropertyPath().toString() + " " + cv.getMessage()));
		}

	}
	
	/** Retains component for possible use in onValidate.
	 * @param component component assigned to this validator. */
	@Override
		public void bind(Component component) {
			this.component = (FormComponent<T>) component;
		}

	private static class ModelProp<T> { IModel<T> model; String prop; }

	/** @return base object and property derived from this component */
	@SuppressWarnings("unchecked")
  private static <T> ModelProp<T> getModelProp(final FormComponent<T> formComponent) {
		final IModel<T> model = formComponent.getModel();
		final ModelProp<T> mp = new ModelProp<T>();
		if (model instanceof PropertyModel) {
			final PropertyModel<T> propModel = (PropertyModel<T>) model;
			mp.model = (IModel<T>) propModel.getChainedModel();
			mp.prop = propModel.getPropertyExpression();
		} else if (model instanceof IWrapModel) {
			mp.model = ((IWrapModel)model).getWrappedModel();
			mp.prop = formComponent.getId();
		} else {
      throw new UnrecognizedModelException(formComponent, model);
    }
		return mp;
	}

	/**
	 * Add immediately to a form component. Note that the component's model
	 * object must be available for inspection at this point or an exception will
	 * be thrown. (For a CompoundPropertyModel, this means the hierarchy must
	 * be established.) This is only possible for components that depend on a
	 * parent CompoundPropertyModel or their own PropertyModels.
	 * @param <T> Type Safe implementation for any given FormComponent.
	 * @param formComponent component to add validator to
	 * @throws UnrecognizedModelException if no usable model is present
	 */
	public static <T> void addTo(final FormComponent<T> formComponent) {
		final ModelProp<T> mp = getModelProp(formComponent);
		formComponent.add(new DatabinderValidator<T>(mp.model, mp.prop));
	}

	/**
   * Add immediately to a form component. Note that the component's model
   * object must be available for inspection at this point or an exception will
   * be thrown. (For a CompoundPropertyModel, this means the hierarchy must
   * be established.) This is only possible for components that depend on a
   * parent CompoundPropertyModel or their own PropertyModels.
   * @param <T> Type Safe implementation for any given FormComponent.
   * @param formComponent component to add validator to
	 * @param validator ClassValidator for the newly asigned instance
   * @throws UnrecognizedModelException if no usable model is present
   */
  public static <T> void addTo(final FormComponent<T> formComponent, final Validator validator) {
    final ModelProp<T> mp = getModelProp(formComponent);
    formComponent.add(new DatabinderValidator<T>(mp.model, mp.prop));
  }

	public static class UnrecognizedModelException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnrecognizedModelException(final Component formComponent, final IModel<?> model) {
			super("DatabinderValidator doesn't recognize the model "
				+ model + " of component " + formComponent.toString());
		}
	}
}
