package org.chenile.stm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.State;
import org.chenile.stm.model.EventInformation;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.Transition;


/**
 * Provides information about available actions for a given state etc.
 * This is useful to provide a HATEOAS approach to STM
 * @author meratransport
 *
 */
public class STMActionsInfoProvider{
	private STMFlowStore stmFlowStore ;
	
	/**
	 * @param stmFlowStore the stmFlowStore to set
	 */
	public void setStmFlowStore(STMFlowStore stmFlowStore) {
		this.stmFlowStore = stmFlowStore;
	}

	public List<String> getAllowedActions(State state){
		List<String> actionList = new ArrayList<String>();
		List<TransitionMetadata> list = getAllowedActionsWithMetadata(state);
		for (TransitionMetadata tm: list) {
			actionList.add(tm.getAllowedAction());
		}
		return actionList;
	}
	
	public List<Map<String, String>> getAllowedActionsAndMetadata(State state) {
		List<Map<String, String>> transitionData = new ArrayList<>();
		Map<String, String> result = new HashMap<>();
		List<TransitionMetadata> list = getAllowedActionsWithMetadata(state);
		for (TransitionMetadata tm: list) {
			result = new HashMap<>(tm.getMetadata());
			result.put("allowedAction", tm.getAllowedAction());
			transitionData.add(result);
		}
		return transitionData;
	}
	
	public static class TransitionMetadata{
		private String allowedAction;
		public String getAllowedAction() {
			return allowedAction;
		}
		
		public Map<String, String> getMetadata() {
			return metadata;
		}
		
		private Map<String,String> metadata;
		public TransitionMetadata(Transition transition) {
			this.allowedAction = transition.getEventId();
			this.metadata = transition.getMetadata();
		}
	}
	
	public List<TransitionMetadata> getAllowedActionsWithMetadata(State state){
		List<TransitionMetadata> actionList = new ArrayList<TransitionMetadata>();
		StateDescriptor sd = stmFlowStore.getStateInfo(state);
		Map<String,Transition> transitions = sd.getTransitions();
		for (Transition transition: transitions.values()) {
			if (transition.isInvokableOnlyFromStm()) {
				continue;
			}
			String[] acls = transition.getAcls();
			if (acls == null || acls.length == 0) {
				actionList.add(new TransitionMetadata(transition));
				continue;
			}
			STMSecurityStrategy stmss = stmFlowStore.getSecurityStrategy(state.getFlowId());
			if (stmss == null) {
				actionList.add(new TransitionMetadata(transition));
				continue;
			}
			if (!stmss.isAllowed(acls)) continue;
			actionList.add(new TransitionMetadata(transition));
		}
		return actionList;
	}
	
	public String getMetadata(State state, String metaId) {
		StateDescriptor sd = stmFlowStore.getStateInfo(state);
		Map<String, String> metadata = sd.getMetadata();
		return metadata.get(metaId);
	}
	
	public EventInformation getEventInformation(String eventId) {
		return stmFlowStore.getEventInformation(eventId);
	}
}
