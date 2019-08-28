package org.chenile.stm.model;

public class Transition extends EventInformation {
	
	public Transition(EventInformation eventInformation) {
		this.eventId = eventInformation.eventId;
		this.metadata = eventInformation.metadata;
		this.transitionAction = eventInformation.transitionAction;
	}
	
	public Transition() {}
	
	public String getNewStateId() {
		return newStateId;
	}
	public void setNewStateId(String newStateId) {
		this.newStateId = newStateId;
	}
	public void setNewFlowId(String newFlowId) {
		this.newFlowId = newFlowId;
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
	
	private String[] acls;
	private boolean isInvokableOnlyFromStm = false;
	private String newStateId;
	private String newFlowId;
	private boolean retrievalTransition;
	private String stateId;


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
	
	
	public void setAcls(String[] acls) {
		this.acls = acls;
	}
	
	public boolean isInvokableOnlyFromStm() {
		return isInvokableOnlyFromStm;
	}
	public void setInvokableOnlyFromStm(boolean isInvokableOnlyFromStm) {
		this.isInvokableOnlyFromStm = isInvokableOnlyFromStm;
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
	
	
}
