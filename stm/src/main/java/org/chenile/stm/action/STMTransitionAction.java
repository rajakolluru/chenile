package org.chenile.stm.action;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.model.Transition;

/**
 * This interface is useful to implement the actual logic to update the state to the new state ID.
 * STM calls this method (if set) for every state transition. This can be used for both entry and exit actions.
 * The entry action is called BEFORE the state is entered. The exit method is called at the time the state is exited out of.
 * In an entry action, if the start state is null then the end state would be the initial state for the particular flow.
 * This can be used for making decisions around inserting a new record etc.
 * <p>Please see {@link STMImpl} for more documentation around the exit action.
 *
 * @param <StateEntityType>
 */
public interface STMTransitionAction<StateEntityType extends StateEntity> {
	public abstract void doTransition(StateEntityType stateEntity, Object transitionParam, 
			State startState, String eventId, State endState,STMInternalTransitionInvoker<?> stm, Transition transition) throws Exception;
}
