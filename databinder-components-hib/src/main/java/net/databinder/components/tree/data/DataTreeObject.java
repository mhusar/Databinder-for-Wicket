package net.databinder.components.tree.data;

import java.util.Collection;

import net.databinder.components.tree.hib.DataProvider;

/**
 * Interface for objects used as tree nodes.
 * 
 * @see DataProvider
 * 
 * @author Thomas Kappler
 * @author ckuehne
 * 
 * @param <T> the type of the actual node object represented by this data tree object
 */
public interface DataTreeObject<T extends DataTreeObject<T>> {

	/** @return the children of this. */
	Collection<T> getChildren();

	/** Adds a new child node to this. */
	void addChild(T child);

	/** @return the parent of this. */
	T getParent();
}
