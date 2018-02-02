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
package net.databinder.auth.components;

import java.util.HashMap;
import java.util.Map;

import net.databinder.auth.AuthSession;
import net.databinder.auth.AuthApplication;
import net.databinder.auth.components.DataSignInPageBase.ReturnPage;
import net.databinder.auth.data.DataUser;
import net.databinder.auth.valid.EqualPasswordConvertedInputValidator;
import net.databinder.components.NullPlug;
import net.databinder.models.BindingModel;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Registration with username, password, and password confirmation.
 * Replaceable String resources: <pre>
 * data.auth.username
 * data.auth.password
 * data.auth.passwordConfirm
 * data.auth.remember
 * data.auth.register
 * data.auth.update
 * data.auth.username.taken * </pre> * Must be overriden in a containing page
 * or a subclass of this panel.
 */
public abstract class DataProfilePanelBase<T extends DataUser> extends Panel {
	private ReturnPage returnPage;
	private Form<T> form;
	private RequiredTextField<String> username;
	private PasswordTextField password, passwordConfirm;
	private CheckBox rememberMe;
	
	/** @return component used in base page, if needed in subclass */
	protected RequiredTextField getUsername() { return username; }
	/** @return component used in base page, if needed in subclass */
	protected PasswordTextField getPassword() { return password; }
	/** @return component used in base page, if needed in subclass */
	protected PasswordTextField getPasswordConfirm() { return passwordConfirm; }
	/** @return component used in base page, if needed in subclass */
	protected CheckBox getRememberMe() { return rememberMe; }
	/** @return form used in base page, if needed elsewhere */
	public Form<T> getForm() { return form; }

	public DataProfilePanelBase(String id, ReturnPage returnPage) {
		super(id);
		this.returnPage = returnPage;
		add(form = profileForm("registerForm", getAuthSession().getUserModel()));
		form.add(new Profile("profile"));
	}
	
	/** @return new form component to be used within this panel */
	protected abstract Form<T> profileForm(String id, IModel<T> userModel);

	/** @return user from form component */
	protected T getUser() {
		return form.getModelObject();
	}

	/** @return true if form is bound to existing user, is not registration form */
	@SuppressWarnings("unchecked")
	protected boolean existing() {
		BindingModel model = ((BindingModel<T>)((IChainingModel<T>)form.getModel()).getChainedModel());
		return model != null && model.isBound();
	}

	/** Contents of the profile form. */
	protected class Profile extends WebMarkupContainer {
		
		public Profile(String id) {
			super(id);
			add(highFormSocket("highFormSocket"));
			add(feedbackBorder("username-border")
					.add(username = new RequiredTextField<String>("username")));
			username.add(new UsernameValidator());
			username.setLabel(new ResourceModel("data.auth.username", "Username"));
			add(new SimpleFormComponentLabel("username-label", username));
			add(feedbackBorder("password-border")
					.add(password = new PasswordTextField("password", new Model<String>()) {
				public boolean isRequired() {
					return !existing();
				}
			}));
			password.setLabel(new ResourceModel("data.auth.password", "Password"));
			add(new SimpleFormComponentLabel("password-label", password));
			add(feedbackBorder("passwordConfirm-border")
					.add(passwordConfirm = new PasswordTextField("passwordConfirm", new Model<String>()) {
				public boolean isRequired() {
					return !existing();
				}
				@Override
				protected void onModelChanged() {
					setPassword((String) getModelObject());
				}
			}));
			form.add(new EqualPasswordConvertedInputValidator(password, passwordConfirm));
			passwordConfirm.setLabel(new ResourceModel("data.auth.passwordConfirm", "Retype Password"));
			add(new SimpleFormComponentLabel("passwordConfirm-label", passwordConfirm));
			
			add(new WebMarkupContainer("rememberMeRow") { 
				public boolean isVisible() {
					return !existing();
				}
			}.add(
				rememberMe = new CheckBox("rememberMe", new Model<Boolean>(Boolean.TRUE))
			).add(
				new Label("text", new ResourceModel("data.auth.remember", "Always sign in automatically"))
			));
			
			add(lowFormSocket("lowFormSocket"));
			
			add(new Button("submit") {
			}.add(new AttributeModifier("value", new AbstractReadOnlyModel() {
				public Object getObject() {
					return existing() ? getString("auth.data.update", null, "Update Account") : 
						getString("data.auth.register", null, "Register");
				}
			})));
		}
	}
	
	protected void setPassword(String password) {
		if (password != null)
			getUser().getPassword().change(password);
	}
	
	/** Subclasses call this after form submission. Returns user to prior page if possible, otherwise home. */
	protected void afterSubmit() {
		getAuthSession().signIn(getUser(), (Boolean) rememberMe.getModelObject());

		if (returnPage == null) {
			if (!continueToOriginalDestination())
				setResponsePage(getApplication().getHomePage());
		} else
			setResponsePage(returnPage.get());
	}

	/** @return true if username is available (can not load via AuthApplication, or is current user). */
	public static boolean isAvailable(String username) {
		AuthApplication authSettings = (AuthApplication)Application.get();
		
		DataUser found = (DataUser) authSettings.getUser(username), 
			current = ((AuthSession)WebSession.get()).getUser();
		return found == null || found.equals(current);
	}
	
	/** Username is valid if isAvailable(username) returns true */
	public static class UsernameValidator extends StringValidator {
		@Override
		protected void onValidate(IValidatable validatable) {
			String username = (String) validatable.getValue();
			if (username != null && !isAvailable(username)) {
				Map<String, Object> m = new HashMap<String, Object>(1);
				m.put("username", username);
				error(validatable,"data.auth.username.taken",  m);
			}
		}
	}
	
	/** @return content to appear above form, base class returns feedback panel */
	protected Component highFormSocket(String id) {
		return new FeedbackPanel(id)
			.add(new AttributeModifier("class", true, new Model<String>("feedback")));
	}

	/** @return content to appear below form, base class returns blank */
	protected Component lowFormSocket(String id) {
		return new NullPlug(id);
	}

	/** @return border to go around each form component, base returns FormComponentFeedbackBorder.  */
	protected Border feedbackBorder(String id) {
		return new FormComponentFeedbackBorder(id);
	}

	/** @return casted session */
	protected AuthSession<T> getAuthSession() {
		return (AuthSession<T>) Session.get();
	}
}
