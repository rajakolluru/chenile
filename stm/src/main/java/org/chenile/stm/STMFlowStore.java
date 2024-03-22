package org.chenile.stm;

import java.util.List;
import java.util.Map;

import org.chenile.stm.action.STMAction;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.model.EventInformation;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.Transition;

/**
 * An interface that is used by an STM implementation to obtain the STD configuration.
 * <p>
 * @author Raja Shankar Kolluru
 *
 */
public interface STMFlowStore {
	/**
	 * Obtains the correct state descriptor to start operations with given the current state.
	 * @param state
	 * @return the initial state for the particular flow if the flow was specified. initial state of the default flow if flow unspecified.
	 */
	public State getInitialState(State state) throws STMException;
	/**
	 * 
	 * @param state
	 * @return the state info.
	 */
	public StateDescriptor getStateInfo(State state);
	/**
	 * 
	 * @param state
	 * @return the entry action for the state.
	 */

	public STMAction<?> getEntryAction(State state);
	/**
	 * 
	 * @param state
	 * @return the exit action for the state
	 */

	public STMAction<?> getExitAction(State state);	
	
	public STMTransitionAction<?> getTransitionAction(Transition transition);
	
	/**
	 * 
	 * @return
	 */
	public StateEntityRetrievalStrategy<? extends StateEntity> getDefaultRetrievalStrategy();
	
	/**
	 * 
	 * @param flowId
	 * @return
	 */
	public List<Map<String,Object>> getStatesInfoForFlow(String flowId);
	
	/**
	 * 
	 * @return
	 */
	public List<Map<String,Object>> getStatesInfoForFlow();
	
	/**
	 * 
	 * @param flowId
	 * @return
	 */
	public STMSecurityStrategy getSecurityStrategy(String flowId);
	public EventInformation getEventInformation(String eventId);
}