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
public class DataProvider<T> implements ITreeProvider<DataTreeObject<T>> {

	private static final long serialVersionUID = 1L;

	private HibernateObjectModel<DataTreeObject<T>> rootModel;

	/**
	 * Construct.
	 */
	public DataProvider(HibernateObjectModel<DataTreeObject<T>> rootModel) {
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
	public Iterator<DataTreeObject<T>> getRoots() {
		
		List<DataTreeObject<T>> roots = new ArrayList<>(1);
		roots.add(rootModel.getObject());
		return roots.iterator();
	}

	@Override
	public boolean hasChildren(DataTreeObject<T> dto) {
		// return for foo also: dto.getParent() == null
		return dto.getChildren() != null && !dto.getChildren().isEmpty();
	}

	
	@Override
	public Iterator<DataTreeObject<T>> getChildren(final DataTreeObject<T> dto) {
		return (Iterator<DataTreeObject<T>>) dto.getChildren().iterator();
	}

	/**
	 * Creates a {@link DataModel}.
	 */
	@Override
	public IModel<DataTreeObject<T>> model(DataTreeObject<T> dto) {
		return new HibernateObjectModel<>(dto);
	}
}
