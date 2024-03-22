package org.chenile.utils.entity.service;

public interface EntityStore<T> {
	/**
	 * Store the entity into permanent store.
	 * @param entity
	 */
	public void store(T entity);
	/**
	 * Retrieve the entity by ID
	 * @param id
	 * @return
	 */
	public T retrieve(String id);
}
