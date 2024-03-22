package org.chenile.stm.impl;

import java.util.Map;

import org.chenile.stm.STM;
import org.chenile.stm.STMFlowStore;
import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.action.STMAutomaticStateComputation;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.model.AutomaticStateDescriptor;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.Transition;

/**
 *
 * The main STM implementation.
 * This requires the injection of a flow configurator to provide the STD
 * information to the STM. 
 * The chief method is the proceed() method 
 * <p>
 * 
 * @author Raja Shankar Kolluru
 *
 *
 * @param <StateEntityType>
 */
public class STMImpl<StateEntityType extends StateEntity> implements STM<StateEntityType> {
	private STMFlowStore stmFlowStore;
	@SuppressWarnings("rawtypes")
	private STMInternalTransitionInvoker<?> stmInternalTransitionInvoker = new STMInternalTransitionInvokerImpl();

	public void setStmFlowStore(STMFlowStore stmFlowStore) {
		this.stmFlowStore = stmFlowStore;
	}

	public STMFlowStore getStmFlowStore() {
		return this.stmFlowStore;
	}

	/**
	 * 
	 */
	public StateEntityType proceed(StateEntityType stateEntity) throws Exception {
		return proceed(stateEntity, null, null);
	}

	public StateEntityType proceed(StateEntityType stateEntity, Object transitionParam) throws Exception {
		return proceed(stateEntity, null, transitionParam);
	}

	/**
	 * This method recursively calls itself to transition as far down the flow
	 * of states as possible. It returns in either one of the following cases:
	 * The state reached is a final state. (i.e. a state with no
	 * transitions) in which case the exit action of the final state is called
	 * and the call chain ends.
	 * The state reached is a manual state (or a view state) in which case
	 * the entry action is called for that particular state and the state is
	 * returned. This method must be reinvoked with a starting event Id to make
	 * it proceed.
	 * <p>
	 * All states in between would be traversed by the machine recursively. All
	 * the entry and exit actions would be called along with the action that
	 * actually computes the event.
	 * <p>
	 * If the machine is called again after the final state is reached, it exits
	 * without doing anything.
	 */

	public StateEntityType proceed(StateEntityType stateEntity, String startingEventId, Object transitionParam)
			throws Exception {
		return internalProceed(stateEntity, startingEventId, transitionParam, true);
	}

	@SuppressWarnings("unchecked")
	private StateEntityType internalProceed(StateEntityType stateEntity, String startingEventId, Object transitionParam,
			boolean doAuthChecks) throws Exception {
		State startState = stateEntity.getCurrentState();

		STMTransitionAction<StateEntityType> transitionAction = null;

		if (isBeginning(startState)) {
			// perhaps this entity is in persistent store and needs to be
			// brought out and merged
			stateEntity = retrieveMergeFromPersistentStorage(stateEntity, startingEventId);
			startState = stateEntity.getCurrentState();
			if (isBeginning(startState)) {
				// transition to initial state.
				State initState = stmFlowStore.getInitialState(startState);
				stateEntity.setCurrentState(initState);
				executeEndStateEntryAction(startState, initState, stateEntity);
				startState = initState;
			}
		}
		StateDescriptor startStateDescriptor = stmFlowStore.getStateInfo(startState);
		if (startStateDescriptor.isFinalState() && startingEventId != null) {
			throw new STMException("Event " + startingEventId + " not valid for retrieved state " + startState,
					STMException.INVALID_EVENTID, startState);
		}
		if (startStateDescriptor.isFinalState())
			return stateEntity;
		if (!startStateDescriptor.isManualState()) {
			startingEventId = obtainEvent(stateEntity, startStateDescriptor);
		}
		if (startingEventId == null)
			return stateEntity;

		Object[] obj = obtainEndStateAndTransition(stateEntity, startState, startingEventId, doAuthChecks);
		State endState = (State) obj[0];
		transitionAction = (STMTransitionAction<StateEntityType>) obj[1];
		Transition transition = (Transition)obj[2];

		executeStartStateExitAction(startState, stateEntity);

		if (transitionAction != null)
			transitionAction.doTransition(stateEntity, transitionParam, startState, startingEventId,endState,
					stmInternalTransitionInvoker, transition);

		stateEntity.setCurrentState(endState);
		executeEndStateEntryAction(startState, endState, stateEntity);

		return doEndStateChecks(endState, stateEntity, transitionParam);
	}

	protected StateEntityType retrieveMergeFromPersistentStorage(StateEntityType originalStateEntity,
			String startEventId) throws Exception {

		@SuppressWarnings("unchecked")
		StateEntityRetrievalStrategy<StateEntityType> retrievalStrategy = (StateEntityRetrievalStrategy<StateEntityType>) stmFlowStore
				.getDefaultRetrievalStrategy();
		if (retrievalStrategy == null)
			return originalStateEntity;
		// retrieve the state entity from the persistent store.
		// if it is in final state or if the intended transition is not allowed
		// for the state entity
		// then the original entity is returned untouched.
		// else the retrieved entity is merged with the original one to make a
		// new state entity
		// which is returned
		StateEntityType retrievedEntity = retrievalStrategy.retrieve(originalStateEntity);

		if (retrievedEntity == null)
			return originalStateEntity;

		State retrievedState = retrievedEntity.getCurrentState();
		if (retrievedState == null) {

			return retrievedEntity;
			// throw new STMException("Found an invalid state in the database ",
			// STMException.INVALID_STATE);
		}

		StateDescriptor retrievedStateDescriptor = stmFlowStore.getStateInfo(retrievedState);

		Map<String, Transition> allowedTransitions = retrievedStateDescriptor.getTransitions();
		if (allowedTransitions == null || allowedTransitions.size() == 0)
			return retrievalStrategy.merge(originalStateEntity, retrievedEntity, null);
		if (retrievedStateDescriptor.checkIfonlyRetrievalTransitions()) {
			if (allowedTransitions.size() != 1)
				throw new STMException("Found more than one retrieval transition in the STD for " + retrievedState,
						STMException.INVALID_STD);
			Transition retrievalTransition = allowedTransitions.values().iterator().next();
			StateEntityType ret = retrievalStrategy.merge(originalStateEntity, retrievedEntity, null);
			// automatically transition from the current state to the new state
			// as stipulated by the
			// retrieval transition.

			String newFlowId = retrievalTransition.getNewFlowId();
			if (newFlowId == null)
				newFlowId = retrievedState.getFlowId(); // default to the same
														// flow as
														// retrievedState
			State newState = new State(retrievalTransition.getNewStateId(), newFlowId);
			executeStartStateExitAction(retrievedState, ret);
			if (retrievalTransition.getTransitionAction() != null) {
				@SuppressWarnings("unchecked")
				STMTransitionAction<StateEntityType> stmta = (STMTransitionAction<StateEntityType>) retrievalTransition
						.getTransitionAction();
				stmta.doTransition(ret, null, retrievedState, startEventId,newState, stmInternalTransitionInvoker,retrievalTransition);
			}
			ret.setCurrentState(newState);
			executeEndStateEntryAction(retrievedState, newState, ret);
			return ret;
		}

		if (!allowedTransitions.containsKey(startEventId)) {
			throw new STMException("Event " + startEventId + " not valid for retrieved state " + retrievedState,
					STMException.INVALID_EVENTID, retrievedState);
		}
		return retrievalStrategy.merge(originalStateEntity, retrievedEntity, startEventId);
	}

	private boolean isBeginning(State startState) {
		return (startState == null) || (startState.getFlowId() == null) || (startState.getStateId() == null);
	}

	private void executeStartStateExitAction(State startState, StateEntityType stateEntity) throws Exception {
		if (isBeginning(startState))
			return;

		@SuppressWarnings("unchecked")
		STMAction<StateEntityType> exitActionOfStartState = (STMAction<StateEntityType>) stmFlowStore
				.getExitAction(startState);
		if (exitActionOfStartState != null) {
			exitActionOfStartState.execute(stateEntity);
		}
	}

	private void executeEndStateEntryAction(State startState, State endState, StateEntityType stateEntity)
			throws Exception {
		@SuppressWarnings("unchecked")
		STMAction<StateEntityType> entryActionOfEndState = (STMAction<StateEntityType>) stmFlowStore
				.getEntryAction(endState);

		if (entryActionOfEndState != null)
			entryActionOfEndState.execute(stateEntity);
	}

	private StateEntityType doEndStateChecks(State endState, StateEntityType stateEntity, Object transitionParam)
			throws Exception {
		// check if the new state can also be evaluated recursively. If it is
		// action state then it can be
		// else we need to just return the entity in the new state
		StateDescriptor endStateDescriptor = stmFlowStore.getStateInfo(endState);

		if (endStateDescriptor.isFinalState()) {
			@SuppressWarnings("unchecked")
			STMAction<StateEntityType> exitActionForEndState = (STMAction<StateEntityType>) stmFlowStore
					.getExitAction(endState);
			if (exitActionForEndState != null)
				exitActionForEndState.execute(stateEntity);
		}

		if (!endStateDescriptor.isManualState()) {
			stateEntity = proceed(stateEntity, transitionParam);
		}
		return stateEntity;
	}

	/**
	 * Obtains the end state and transition for the combination of start state
	 * and event Id. Since it returns a tuple, this is accomplished using an
	 * Object array. The object array is somewhat of a kludge.<br>
	 * 
	 * @param stateEntity
	 * @param startState
	 * @param startingEventId
	 * @return
	 * @throws Exception
	 */

	private Object[] obtainEndStateAndTransition(StateEntityType stateEntity, State startState, String startingEventId,
			boolean doAuthChecks) throws Exception {
		StateDescriptor startStateDescriptor = stmFlowStore.getStateInfo(startState);

		if (startStateDescriptor == null)
			throw new STMException(
					"Missing state " + startState + ".Are you manipulating entity state outside of the STM?",
					STMException.INVALID_STATE, startState);
		Map<String, Transition> transitions = startStateDescriptor.getTransitions();

		if (transitions == null || transitions.size() == 0) {

			if (startingEventId != null)
				throw new STMException("Event " + startingEventId + " not valid for state " + startState,
						STMException.INVALID_EVENTID, startState);

			return new Object[] { startState, null }; // end state remains the
														// same.
		}
		if (startingEventId == null)
			startingEventId = obtainEvent(stateEntity, startStateDescriptor);

		if (startingEventId == null) {
			throw new STMException("Null event IDs are not valid", STMException.INVALID_EVENTID, startState);
		}
		Transition transition = transitions.get(startingEventId);
		if (transition == null) {
			throw new STMException(
					"No eligible transitions found for " + startState + " for event id = " + startingEventId,
					STMException.UNAVAILABLE_TRANSITION, startState);
		}

		String newStateId = transition.getNewStateId();
		String newFlowId = transition.getNewFlowId();
		if (newFlowId == null) {
			newFlowId = startState.getFlowId();
		}
		State endState = null;
		if (newStateId == null)
			endState = startState;
		else
			endState = new State(newStateId, newFlowId);
		
		//STMTransitionAction<StateEntityType> transitionAction = (STMTransitionAction<StateEntityType>) transition
				//.getTransitionAction();
		STMTransitionAction<?> transitionAction = stmFlowStore.getTransitionAction(transition);

		if (doAuthChecks && !isTransitionAuthorized(startStateDescriptor, transition)) {
			throw new STMException(
					"Transition " + transition.getEventId() + " is not invokable by the current principal.",
					STMException.INVALID_TRANSITION);
		}
		return new Object[] { endState, transitionAction, transition };
	}

	protected boolean isTransitionAuthorized(StateDescriptor startStateDescriptor, Transition transition)
			throws STMException {
		// suppress the check for auto states
		if (!startStateDescriptor.isManualState())
			return true;
//		// don't allow if this transition is invokable only from another STM
//		if (transition.isInvokableOnlyFromStm())
//			throw new STMException(
//					"Transition " + transition.getEventId() + " is not invokable except from another STM.",
//					STMException.INVALID_TRANSITION);
		// check if the transition can be invoked by the principal.
		STMSecurityStrategy securityStrategy = stmFlowStore.getSecurityStrategy(startStateDescriptor.getFlowId());
		if (securityStrategy == null)
			return true;

		String[] acls = transition.getAcls();
		if (acls == null || acls.length == 0)
			return true;
		return securityStrategy.isAllowed(acls);
	}

	protected String obtainEvent(StateEntityType stateEntity, StateDescriptor stateDescriptor) throws Exception {
		if (!stateDescriptor.isManualState()) {
			return obtainActionEvent(stateEntity, (AutomaticStateDescriptor) stateDescriptor);
		} else {
			return obtainViewEvent(stateEntity, stateDescriptor);
		}
	}

	/**
	 * A possible hook for a sub class to generate an event from a manual state.
	 * 
	 * @param stateEntity
	 *            - the current state
	 * @param manualStateDescriptor
	 *            - the state descriptor that maps with the current state.
	 * @return the event.
	 */
	protected String obtainViewEvent(StateEntity stateEntity, StateDescriptor manualStateDescriptor) {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected String obtainActionEvent(StateEntityType stateEntity, AutomaticStateDescriptor actionStateDescriptor)
			throws Exception {
		STMAutomaticStateComputation<StateEntityType> action = (STMAutomaticStateComputation<StateEntityType>) actionStateDescriptor
				.getComponent();

		if (action == null)
			return STM.SUCCESS; // automatic states without components attached
								// to them are assumed
		// to have a "do nothing" action that always returns SUCCESS

		return action.execute(stateEntity);
	}

	private class STMInternalTransitionInvokerImpl<STE extends StateEntity>
			implements STMInternalTransitionInvoker<STE> {

		@Override
		public STE proceed(STM<STE> stm, STE stateEntity, String startingEventId, Object transitionParam)
				throws Exception {
			STMImpl<STE> stmimpl = (STMImpl<STE>) stm;
			return stmimpl.internalProceed(stateEntity, startingEventId, transitionParam, false);

		}

	}

}
