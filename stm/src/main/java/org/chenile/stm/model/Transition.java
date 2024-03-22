package org.chenile.stm.model;

import org.chenile.stm.action.STMTransitionAction;

public class Transition extends EventInformation {
	
	public Transition(EventInformation eventInformation) {
		fromEventInformation(eventInformation);
	}
	
	public Transition fromEventInformation(EventInformation eventInformation) {
		this.eventId = eventInformation.eventId;
		this.metadata = eventInformation.metadata;
		this.transitionAction = eventInformation.transitionAction;
		return this;
	}
	
	public Transition() {}
	
	public String getNewStateId() {
		return newStateId;
	}
	public void setNewStateId(String newStateId) {
		this.newStateId = newStateId;
	}
	
	public Transition transitionAction(STMTransitionAction<?> transitionAction) {
		this.transitionAction = transitionAction;
		return this;
	}
	
	public Transition newStateId(String newStateId) {
		this.newStateId = newStateId;
		return this;
	}
	public void setNewFlowId(String newFlowId) {
		this.newFlowId = newFlowId;
	}
	
	public Transition newFlowId(String newFlowId) {
		this.newFlowId = newFlowId;
		return this;
	}
	public String getNewFlowId() {
		return newFlowId;
	}
	public boolean isRetrievalTransition() {
		return retrievalTransition;
	}
	public void setRetrievalTransition(boolean retrievalTransition) {
		this.retrievalTransition = retrievalTransition;
	}

	public String getStateId() {
		return stateId;
	}
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	
	public Transition id(String stateId) {
		this.stateId = stateId;
		return this;
	}
	
	private String[] acls;
	private boolean isInvokableOnlyFromStm = false;
	private String newStateId;
	private String newFlowId;
	private boolean retrievalTransition;
	private String stateId;
	private String flowId;
	private StateDescriptor parentState;


	public StateDescriptor getParentState() {
		return parentState;
	}

	public void setParentState(StateDescriptor parentState) {
		this.parentState = parentState;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	@Override
	public String toString() {
		return "Transition [eventId=" + eventId + ", newStateId=" + newStateId
				+ ", newFlowId=" + newFlowId + ", retrievalTransition="
				+ retrievalTransition + ", transitionAction="
				+ transitionAction + ", metadata=" + metadata + "]";
	}
	
	public String[] getAcls() {
		return acls;
	}
	
	public void setAclString(String acl){
		if (acl == null) return;
		setAcls(acl.split(","));
	}
	
	public Transition acl(String acl){
		if (acl == null) return this;
		setAcls(acl.split(","));
		return this;
	}
	
	
	public void setAcls(String[] acls) {
		this.acls = acls;
	}
	
	public boolean isInvokableOnlyFromStm() {
		return isInvokableOnlyFromStm;
	}
	public void setInvokableOnlyFromStm(boolean isInvokableOnlyFromStm) {
		this.isInvokableOnlyFromStm = isInvokableOnlyFromStm;
	}
	
	public Transition makeInvokableOnlyFromStm() {
		this.isInvokableOnlyFromStm = true;
		return this;
	}
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<transition eventId='" + eventId + "' >\n");
		sb.append("<newStateId>" + newStateId + "</newStateId>\n");
		sb.append("<newFlowId>" + newFlowId + "</newFlowId>\n");
		sb.append("<retrievalTransition>" + retrievalTransition + "</retrievalTransition>\n");
		sb.append("<transitionAction>" + transitionAction + "</transitionAction>\n");
		sb.append("</transition>\n");
		return sb.toString();
	}

	public Transition transitionTo(String stateId, String... flowId) {
		String newFlowId = this.flowId;
		if (flowId != null && flowId.length > 0 && flowId[0] != null) {
			newFlowId = flowId[0];
		}
		this.newFlowId = newFlowId;
		this.newStateId = stateId;	
		return this;
	}
	
	public StateDescriptor state() {
		return parentState;
	}
	
	
}
