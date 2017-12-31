/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.apache.wicket.MetaDataKey;

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
public class DataTreeExpansion<T> implements Set<DataTreeObject<T>>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final MetaDataKey<DataTreeExpansion<?>> KEY = new MetaDataKey<DataTreeExpansion<?>>() {
		private static final long serialVersionUID = 1L;
	};

	private Set<HibernateObjectModel<DataTreeObject<T>>> objects = new HashSet<>();

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
	public boolean add(DataTreeObject<T> dto) {
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
			return objects.add(modelOf((DataTreeObject<T>) o));
		} else {
			return objects.remove(modelOf((DataTreeObject<T>) o));
		}
	}

	@Override
	public boolean contains(Object o) {
		@SuppressWarnings("unchecked")
		HibernateObjectModel<DataTreeObject<T>> dto = modelOf((DataTreeObject<T>) o);

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
	public Iterator<DataTreeObject<T>> iterator() {
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
	public boolean addAll(Collection<? extends DataTreeObject<T>> c) {
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

	private static <T> HibernateObjectModel<DataTreeObject<T>> modelOf(
			DataTreeObject<T> persistentObject) {
		return new HibernateObjectModel<>(persistentObject);
	}
}
