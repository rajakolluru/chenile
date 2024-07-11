package org.chenile.utils.entity.service;

public interface EntityStore<T> {
	/**
	 * Store the entity into permanent store.
	 * @param entity the entity that needs to be persisted
	 */
	public void store(T entity);
	/**
	 * Retrieve the entity by ID
	 * @param id the ID of the entity
	 * @return the entity from the store
	 */
	public T retrieve(String id);
}
