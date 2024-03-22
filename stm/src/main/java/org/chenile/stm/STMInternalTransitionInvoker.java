package org.chenile.stm;


/**
 * An interface that is available for the STM Transition action.
 * It is the only way to invoke actions that are available only for other STMs.
 * @author rajakolluru
 *
 */
public interface STMInternalTransitionInvoker<StateEntityType extends StateEntity> {
	public StateEntityType proceed(STM<StateEntityType> stm, StateEntityType stateEntityType, String startingEventId, 
			Object actionParam) throws Exception;
}
