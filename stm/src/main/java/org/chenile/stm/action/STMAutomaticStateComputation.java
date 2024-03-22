package org.chenile.stm.action;

import org.chenile.stm.StateEntity;

/**
 * 
 * @author rajakolluru
 * Implemented by a class that wants to automatically transition the state from one to the other based on
 * certain properties of the entity.
 * @param <StateEntityType>
 */

public interface STMAutomaticStateComputation<StateEntityType extends StateEntity> {
	public String execute(StateEntityType stateEntity) throws Exception;
}
