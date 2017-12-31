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

package net.databinder.components.tree;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

/**
 * Tree folder that is selectable.
 * 
 * @author ckuehne
 * @author svenmeier (based on svenmeier's tree examples)
 *
 * @param <T>
 */
public class SelectableFolder<T> extends Folder<T> {
	private static final long serialVersionUID = 1L;
	
	private SelectableNestedTree<T> tree;

	public SelectableFolder(String id, SelectableNestedTree<T> tree, IModel<T> model) {
		super(id, tree, model);
		this.tree = tree;
	}

	/**
	 * Always be clickable.
	 */
	@Override
	protected boolean isClickable() {
		return true;
	}

	@Override
	protected void onClick(AjaxRequestTarget target) {
		tree.select(getModelObject(), target);
	}

	@Override
	protected boolean isSelected() {
		return tree.isSelected(getModelObject());
	}
}