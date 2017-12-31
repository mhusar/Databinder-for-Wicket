/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006 Nathan Hamblen nathan@technically.us
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.databinder.components.tree;

import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.model.IModel;

/**
 * A nested tree that allows selecting one node at a time.
 * 
 * @author ckuehne
 * @author svenmeier (based on svenmeier's tree examples)
 * 
 * @param <T> The node type
 */
public class SelectableNestedTree<T> extends NestedTree<T> {
	private static final long serialVersionUID = 1L;

	private IModel<T> selected;

	/**
	 * Constructs with default windows theme.
	 *
	 * @param id component id
	 * @param provider provider of the tree
	 */
	public SelectableNestedTree(String id, ITreeProvider<T> provider) {
		this(id, provider, null);
	}

	/**
	 * Constructs with default {@link WindowsTheme}.
	 * 
	 * @param id component id
	 * @param provider provider of the tree
	 * @param state expansion state
	 */
	public SelectableNestedTree(String id, ITreeProvider<T> provider,
			IModel<? extends Set<T>> state) {

		this(id, provider, state, new WindowsTheme());
	}

	/**
	 * Construct.
	 * 
	 * @param id component id
	 * @param provider provider of the tree
	 * @param state expansion state
	 * @param theme the layout theme
	 */
	public SelectableNestedTree(String id, ITreeProvider<T> provider,
			IModel<? extends Set<T>> state, Behavior theme) {

		super(id, provider, state);
		add(theme);
	}

	@Override
	protected Component newContentComponent(String id, IModel<T> node) {
		return new SelectableFolder<T>(id, this, node);
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if (selected != null) {
			selected.detach();
		}
	}

	protected boolean isSelected(T t) {
		IModel<T> modelT = getProvider().model(t);

		try {
			return selected != null && selected.equals(modelT);
		} finally {
			modelT.detach();
		}
	}

	public void select(T t, final AjaxRequestTarget target) {
		if (selected != null) {
			updateNode(selected.getObject(), target);

			selected.detach();
			selected = null;
		}
		selected = getProvider().model(t);
		updateNode(t, target);
		onSelect(selected, target);
	}

	public T getSelected() {
		return selected == null ? null : selected.getObject();
	}

	public void onSelect(IModel<T> selected, AjaxRequestTarget target) {
		// NOOP
	}

	/**
	 * Updates the currently selected node if there is one selected. To be used during ajax requests.
	 * 
	 * @param target
	 */
	public void updateNodeSelectedNode(final IPartialPageRequestHandler target) {
		T t = getSelected();
		if (t != null) {
			updateNode(t, target);
		}
	}
}
