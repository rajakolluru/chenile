package org.chenile.stm.action;

import org.chenile.stm.StateEntity;


/**
 * Implement this interface to become an STM entry or exit action 
 * @author Raja Shankar Kolluru
 *
 * 
 */
public interface STMAction<StateEntityType extends StateEntity> {
	public void execute(StateEntityType stateEntity) throws Exception;
}
