package uk.ac.manchester.cs.goblin.config;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ListReorderer<E> {

	private List<E> list;

	ListReorderer(List<E> list) {

		this.list = list;
	}

	void reorder(List<E> newOrder) {

		checkContentsMatch(newOrder);

		list.clear();
		list.addAll(newOrder);
	}

	private void checkContentsMatch(List<E> newOrder) {

		if (!new HashSet<E>(list).equals(new HashSet<E>(newOrder))) {

			throw new RuntimeException("Invalid reordering!");
		}
	}
}