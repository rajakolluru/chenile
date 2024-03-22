package org.chenile.stm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.STMFlowStoreImpl;

public class FlowDescriptor implements TransientActionsAwareDescriptor{
	private String id;
	private boolean isDefault = false;
	private Map<String, StateDescriptor> states =  new HashMap<String, StateDescriptor>();
	private String initialState;
	private  STMAction<?> entryAction;
	// by default don't execute entry or exit actions for auto states.
	private boolean skipEntryExitActionsForAutoStates = true;
	private STMSecurityStrategy stmSecurityStrategy;
	
	public boolean isSkipEntryExitActionsForAutoStates() {
		return skipEntryExitActionsForAutoStates;
	}

	public void setSkipEntryExitActionsForAutoStates(
			boolean skipEntryExitActionsForAutoStates) {
		this.skipEntryExitActionsForAutoStates = skipEntryExitActionsForAutoStates;
	}
	
	public FlowDescriptor skipEntryExitActionsForAutoStates(
			boolean skipEntryExitActionsForAutoStates) {
		this.skipEntryExitActionsForAutoStates = skipEntryExitActionsForAutoStates;
		return this;
	}

	public STMAction<?> getEntryAction() {
		return entryAction;
	}

	public void setEntryAction(STMAction<?> entryAction) {
		this.entryAction = entryAction;
	}
	
	public FlowDescriptor entryAction(STMAction<?> entryAction) {
		this.entryAction = entryAction;
		return this;
	}

	public STMAction<?> getExitAction() {
		return exitAction;
	}

	public void setExitAction(STMAction<?> exitAction) {
		this.exitAction = exitAction;
	}
	
	public FlowDescriptor exitAction(STMAction<?> exitAction) {
		this.exitAction = exitAction;
		return this;
	}


	private  STMAction<?> exitAction;

	private StateEntityRetrievalStrategy<? extends StateEntity> retrievalStrategy;
	private STMFlowStoreImpl flowStore;

	public void setId(String id) {
		this.id = id;
	}
	
	public FlowDescriptor id(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public FlowDescriptor makeDefault() {
		this.isDefault = true;
		if (this.flowStore != null)
			flowStore.setDefaultFlow(this);
		return this;
	}
	
	public boolean isDefault() {
		return isDefault;
	}

	public void setStates(Map<String, StateDescriptor> states) {
		this.states = states;
	}

	public Map<String, StateDescriptor> getStates() {
		return states;
	}

	
	public void addsd(StateDescriptor sd) throws STMException {
		StateDescriptor currSd = states.get(sd.getId());
		if (currSd != null){
			currSd.merge(sd);
			sd = currSd;
		}else{
			states.put(sd.getId(), sd);
			sd.setFlowId(this.id);
		}
		sd.setFlowId(this.id);
		sd.setFlow(this);
		if (sd.isInitialState())
			initialState = sd.getId();
		if (sd.getTransitions() == null || sd.getTransitions().size() == 0){
			sd.setFinalState(true);
		}
		sd.validate();
		
	}
	
	public FlowDescriptor addState(StateDescriptor sd) throws STMException{
		addsd(sd);
		return this;
	}


	public void setInitialState(String initialState) {
		this.initialState = initialState;
	}
	
	public FlowDescriptor initialState(String initialState) {
		this.initialState = initialState;
		return this;
	}

	public String getInitialState() {
		return initialState;
	}
	
	public void validate(STMFlowStore flowStore) throws Exception {
		if (getId() == null) {
			System.out.println("Warning: Flow Id is null. By default the value of the ID is " + State.DEFAULT_FLOW_ID);
		}
		for ( StateDescriptor sd : states.values()){
			sd.validate();
		}
		
	}

	
	
	
	
	@Override
	public String toString() {
		return "FlowDescriptor [id=" + id + ", isDefault=" + isDefault + ", states=" + states + ", initialState="
				+ initialState + ", entryAction=" + entryAction + ", skipEntryExitActionsForAutoStates="
				+ skipEntryExitActionsForAutoStates + ", exitAction=" + exitAction + ", retrievalStrategy="
				+ retrievalStrategy + "]";
	}
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<flow>");
		sb.append("<id>" + id + "</id>\n");
		sb.append("<isDefault>" + isDefault + "</isDefault>\n");
		sb.append("<initialState>" + initialState + "</initialState>\n");
		sb.append("<entryAction>" + entryAction + "</entryAction>\n");
		sb.append("<exitAction>" + exitAction + "</exitAction>\n");
		sb.append("<retrievalStrategy>" + retrievalStrategy + "</retrievalStrategy>\n");
		sb.append("<skipEntryExitActionsForAutoStates>" + skipEntryExitActionsForAutoStates + "</skipEntryExitActionsForAutoStates>\n");
		sb.append("<states>\n");
		for(StateDescriptor sd: states.values()){
			sb.append(sd.toXml());
		}
		sb.append("</states>\n");
		sb.append("</flow>");
		return sb.toString();
	}
	
	public void setRetrievalStrategy(StateEntityRetrievalStrategy<? extends StateEntity> retrievalStrategy){
		this.retrievalStrategy = retrievalStrategy;
	}
	
	public FlowDescriptor retrievalStrategy(StateEntityRetrievalStrategy<? extends StateEntity> retrievalStrategy){
		this.retrievalStrategy = retrievalStrategy;
		return this;
	}

	public StateEntityRetrievalStrategy<? extends StateEntity> getRetrievalStrategy() {
		return retrievalStrategy;
	}
	
	public List<Map<String,Object>> getStatesInfo(){
		List<Map<String,Object>> statesInfo = new ArrayList<Map<String,Object>>();
		for(Entry<String, StateDescriptor> stateId : states.entrySet()){
			
			if(!stateId.getValue().isManualState()){
				continue;
			}
			
			Map<String,Object> stateInfo = new HashMap<String, Object>();
			stateInfo.put("state_id", stateId.getKey());
			List<Map<String,Object>> transitionsMeta = new ArrayList<Map<String,Object>>();
			for(Transition transition : stateId.getValue().getTransitions().values()){
				Map<String,Object> transitionDetails = new HashMap<String, Object>();
				transitionDetails.put("meta-data", transition.getMetadata());
				transitionDetails.put("eventId", transition.getEventId());
				transitionDetails.put("acls", transition.getAcls());
				transitionsMeta.add(transitionDetails);
			}
			stateInfo.put("transitions_allowed",transitionsMeta);
			stateInfo.put("meta-data",stateId.getValue().getMetadata());
			statesInfo.add(stateInfo);
		}
		return statesInfo;
	}

	public STMSecurityStrategy getStmSecurityStrategy() {
		return stmSecurityStrategy;
	}

	public void setStmSecurityStrategy(STMSecurityStrategy stmSecurityStrategy) {
		this.stmSecurityStrategy = stmSecurityStrategy;
	}
	
	public FlowDescriptor securityStrategy(STMSecurityStrategy stmSecurityStrategy) {
		this.stmSecurityStrategy = stmSecurityStrategy;
		return this;
	}
	
	public ManualStateDescriptor manualState(String id)  throws STMException{
		return manualState(id,false);
	}
	
	public ManualStateDescriptor manualState(String id, boolean initialState) throws STMException {
		ManualStateDescriptor msd = new ManualStateDescriptor();
		msd.setId(id);
		msd.setFlowId(this.id);
		msd.setFlow(this);
		msd.setManualState(true);
		if (initialState) {
			this.initialState = id;
		}
		this.addsd(msd);
		return msd;
	}
	
	public AutomaticStateDescriptor autoState(String id)  throws STMException {
		return autoState(id,false);
	}
	public AutomaticStateDescriptor autoState(String id, boolean initialState) throws STMException{
		AutomaticStateDescriptor asd = new AutomaticStateDescriptor();
		asd.setId(id);
		asd.setFlowId(this.id);
		asd.setFlow(this);
		if (initialState) {
			this.initialState = id;
		}
		this.addsd(asd);
		return asd;
	}

	public STMFlowStoreImpl getFlowStore() {
		return flowStore;
	}

	public void setFlowStore(STMFlowStoreImpl flowStore) {
		this.flowStore = flowStore;
	}

	public void setInitialState(StateDescriptor stateDescriptor) {
		this.initialState = stateDescriptor.getId();		
	}

}
