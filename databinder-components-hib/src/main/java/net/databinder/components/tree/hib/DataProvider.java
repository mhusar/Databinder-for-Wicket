package net.databinder.components.tree.hib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;

import net.databinder.components.tree.data.DataTreeObject;
import net.databinder.models.hib.HibernateObjectModel;

/**
 * Provides data tree objects to a tree implementation.
 * 
 * @author Conny Kuehne
 */
public class DataProvider<T extends DataTreeObject<T>> implements ITreeProvider<T> {

	private static final long serialVersionUID = 1L;

	private HibernateObjectModel<T> rootModel;

	/**
	 * Construct.
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

		List<T> roots = new ArrayList<>(1);
		roots.add(rootModel.getObject());
		return roots.iterator();
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
}
