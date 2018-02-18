package net.databinder.components.tree.hib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;

import net.databinder.components.tree.data.DataTreeObject;
import net.databinder.models.hib.HibernateObjectModel;

/**
 * Provides data tree objects to a tree implementation.
 * <p>
 * Can be set to root-less. In root-less mode, it returns the children of the original root as roots.
 * (The original root here refers to the object of the root model (see {@link DataProvider#DataProvider(HibernateObjectModel)}.)
 * 
 * @author Conny Kuehne
 */
public class DataProvider<T extends DataTreeObject<T>> implements ITreeProvider<T> {

	private static final long serialVersionUID = 1L;

	private HibernateObjectModel<T> rootModel;

	private boolean rootLess = false;

	/**
	 * Construct with the root model.
	 */
	public DataProvider(HibernateObjectModel<T> rootModel) {
		this.rootModel = rootModel;
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public void detach() {
		rootModel.detach();
	}

	@Override
	public Iterator<T> getRoots() {
		T root = rootModel.getObject();
		if (rootLess) {
			if (root == null || root.getChildren() == null) {
				return Collections.emptyIterator();
			}
			return root.getChildren().iterator();
		} else {
			List<T> roots = new ArrayList<>(1);
			roots.add(rootModel.getObject());
			return roots.iterator();
		}
	}

	@Override
	public boolean hasChildren(T dto) {

		return dto.getChildren() != null && !dto.getChildren().isEmpty();
	}

	@Override
	public Iterator<T> getChildren(final T dto) {
		return dto.getChildren().iterator();
	}

	@Override
	public IModel<T> model(T dto) {
		return new HibernateObjectModel<>(dto);
	}

	/**
	 * @param rootLess
	 */
	public void setRootLess(boolean rootLess) {
		this.rootLess = rootLess;
	}

	/**
	 * @return <code>true</code> if only the children of the (original) root should be returned as roots,
	 *         <code>false</code> if the root is returned.
	 * 
	 * @see DataProvider#DataProvider(HibernateObjectModel)
	 */
	public boolean isRootLess() {
		return rootLess;
	}
}
