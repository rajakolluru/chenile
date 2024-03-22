package org.chenile.stm.action;

import org.chenile.stm.StateEntity;

public interface StateEntityRetrievalStrategy<StateEntityType extends StateEntity> {
	/**
	 * Retrieves a state entity from a persistent store like DB.
	 * @param stateEntity
	 * @return
	 * @throws Exception
	 */
	StateEntityType retrieve(StateEntityType stateEntity) throws Exception;
	/**
	 * Merges a state entity with another one obtained from persistent store 
	 * @param stateEntity
	 * @return
	 * @throws Exception
	 */
	StateEntityType merge(StateEntityType stateEntity, StateEntityType persistentEntity,
			String eventId) throws Exception;
}
