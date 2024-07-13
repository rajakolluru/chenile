package org.chenile.workflow.service.stmcmds;

import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.utils.entity.model.ExtendedStateEntity;
import org.chenile.utils.entity.service.EntityStore;

public class GenericRetrievalStrategy<T extends ExtendedStateEntity> implements StateEntityRetrievalStrategy<T> {
	private EntityStore<T> entityStore;
	public void setEntityStore(EntityStore<T> entityStore) {
		this.entityStore = entityStore;
	}
	@Override
	public T retrieve(T entity) throws Exception {	
		if (entity.getId() == null) {
			return null;
		}
		return entityStore.retrieve(entity.getId());
	}
	@Override
	public T merge(T entity, T persistedEntity, String eventId) throws Exception {
		// the incoming entity has id only. hence nothing to  merge
		return persistedEntity;
	}
}
