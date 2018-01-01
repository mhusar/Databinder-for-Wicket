/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.databinder.components.tree.hib;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.databinder.components.tree.data.DataTreeObject;
import net.databinder.models.hib.HibernateObjectModel;

/**
 * Expansion state for a tree whose node type is {@link DataTreeObject}.
 * <p>
 * Wraps each data tree object into a {@link HibernateObjectModel} before storing it.
 * This means that the operations of this class rely on {@link HibernateObjectModel#hashCode()} and
 * {@link HibernateObjectModel#equals(Object)}.
 * 
 * @author ckuehne
 * @author svenmeier (based on FooExpansion in wicket examples)
 */
public class DataTreeExpansion<T extends DataTreeObject<T>> implements Set<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private Set<HibernateObjectModel<T>> objects = new HashSet<>();

	private boolean inverse;

	/**
	 * Expand all nodes.
	 */
	public void expandAll() {
		objects.clear();

		inverse = true;
	}

	/**
	 * Collapses all nodes.
	 */
	public void collapseAll() {
		objects.clear();

		inverse = false;
	}

	@Override
	public boolean add(T dto) {
		if (inverse) {
			return objects.remove(modelOf(dto));
		} else {
			return objects.add(modelOf(dto));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {

		if (inverse) {
			return objects.add(modelOf((T) o));
		} else {
			return objects.remove(modelOf((T) o));
		}
	}

	@Override
	public boolean contains(Object o) {
		@SuppressWarnings("unchecked")
		HibernateObjectModel<T> dto = modelOf((T) o);

		if (inverse) {
			return !objects.contains(dto);
		} else {
			return objects.contains(dto);
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> A[] toArray(A[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	private static <T extends DataTreeObject<T>> HibernateObjectModel<T> modelOf(
			T persistentObject) {

		return new HibernateObjectModel<>(persistentObject);
	}
}
