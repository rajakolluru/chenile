package org.chenile.stm.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.State;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.exception.STMException;

import java.util.Set;


public class StateDescriptor implements TransientActionsAwareDescriptor{

	protected String id;
	protected boolean initialState;
	protected STMAction<?> entryAction;
	protected boolean finalState;
	protected Map<String,String> metadata = new HashMap<String, String>();
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public void addMetaData(String name, String value){
		metadata.put(name, value);
	}

	public Map<String, String> getMetadata(){
		return Collections.unmodifiableMap(metadata);
	}

	public void setFinalState(boolean endState) {
		this.finalState = endState;
	}

	public STMAction<?> getEntryAction() {
		return entryAction;
	}

	public void setEntryAction(STMAction<?> entryAction) {
		this.entryAction = entryAction; 
	}

	public STMAction<?> getExitAction() {
		return exitAction;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public void setExitAction(STMAction<?> exitAction) {
		this.exitAction = exitAction;
	}

	protected STMAction<?> exitAction;
	/**
	 * Is this state manual? (or a view state?)
	 */
	protected boolean manualState = false;


	public boolean isManualState() {
		return manualState;
	}

	public void setManualState(boolean manualState) {
		this.manualState = manualState;
	}

	private Map<String,Transition> transitions = new HashMap<String, Transition>();
	private String flowId;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public StateDescriptor() {
		super();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setInitialState(boolean initialState) {
		this.initialState = initialState;
	}

	public boolean isInitialState() {
		return initialState;
	}

	public void setTransitions(Map<String,Transition> transitions) {
		this.transitions = transitions;
	}

	// @XmlTransient
	public Map<String,Transition> getTransitions() {
		return transitions;
	}

	public void addTransition(Transition transition) throws STMException {
		// ensure that the transitions have explicit newStateId etc.
		if(transition.getNewFlowId() == null) transition.setNewFlowId(this.flowId);
		if (transition.getNewStateId() == null) transition.setNewStateId(this.id);
		// for retrieval transitions the new state cannot be the same as the old state
		if (transition.isRetrievalTransition() && transition.getNewStateId().equals(this.id)){
			throw new STMException("Retrieval transitions must have a \"to state\" that is different from the \"from state\"",
					STMException.INVALID_TRANSITION);
		}
		transitions.put(transition.getEventId(), transition);
	}

	
	
	
	@Override
	public String toString() {
		return "StateDescriptor [id=" + id + ", initialState=" + initialState
				+ ", transitions=" + transitions + "]";
	}
	
	public boolean checkIfonlyRetrievalTransitions() {
		for (Transition t: transitions.values()){
			if(!t.isRetrievalTransition()) return false; 
		}
		return true;
	}
	
	public void validate() throws STMException{
		for (Transition t: transitions.values()){
			if(t.isRetrievalTransition() && transitions.size() != 1) {
				throw new STMException("Invalid state definition for id " + id,STMException.INVALID_STATE); 
			}
		}
	}
	
	public void validate(STMFlowStore flowStore) throws STMException{
		validate();
		for (Transition t: transitions.values()){
			// make sure that each transition points to a new state that is defined in the state machine
			State newState = new State(t.getNewStateId(),t.getNewFlowId());
			StateDescriptor s = flowStore.getStateInfo(newState);
			if (s == null)
				throw new STMException("New State " + newState + " pointed by transition " + t.getEventId() + 
						" is not defined in the flow store.", STMException.INVALID_STATE,new State(getId(),getFlowId())); 
		}
	}
	
	public Set<String> getAllTransitionsIds(){
		return transitions.keySet();
	}
	
	public void merge(StateDescriptor sd) {
		if (exitAction != null && sd.getExitAction() != null){
			System.err.println("Warning: Exit action of " + sd.getId() + " seems to be duplicated!!");
		}
		if (sd.getTransitions() != null)
			this.transitions.putAll(sd.getTransitions()); // merge the transitions
		if (transitions != null && transitions.size() > 0 )
			this.finalState = false;
		else
			this.finalState = true;
	}
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<state>\n");
		sb.append("<id>" + id + "</id>\n");
		sb.append("<metadata>\n");
		for(Entry<String, String> entry:metadata.entrySet()){
			sb.append("<"+entry.getKey()+" value="+entry.getValue()+">\n");
		}
		sb.append("</metadata>\n");
		sb.append("<transitions>");
		for (Transition t: transitions.values()){
			sb.append(t.toXml());
		}
		sb.append("</transitions>");
		sb.append("</state>\n");
		return sb.toString();
	}
}