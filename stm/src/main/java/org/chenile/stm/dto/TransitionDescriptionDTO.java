package org.chenile.stm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TransitionDescriptionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String eventName;

	private String newStateId;

	private String newFlowId;

	private Long stateId;

	private Boolean isRetrievalTransition=false;

	private String transitionAction;
	
	private String acls;
	
	private Boolean isInvokableOnlyFromStm=false;
	
	private Map<String,String> metaData=new HashMap<>();
	
	public Map<String, String> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getStateId() {
		return stateId;
	}
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
	public String getNewStateId() {
		return newStateId;
	}
	public void setNewStateId(String newStateId) {
		this.newStateId = newStateId;
	}
	public String getNewFlowId() {
		return newFlowId;
	}
	public void setNewFlowId(String newFlowId) {
		this.newFlowId = newFlowId;
	}
	public Boolean getIsRetrievalTransition() {
		return isRetrievalTransition;
	}
	public void setIsRetrievalTransition(Boolean isRetrievalTransition) {
		this.isRetrievalTransition = isRetrievalTransition;
	}
	public String getTransitionAction() {
		return transitionAction;
	}
	public void setTransitionAction(String transitionAction) {
		this.transitionAction = transitionAction;
	}

	public Boolean getIsInvokableOnlyFromStm() {
		return isInvokableOnlyFromStm;
	}
	public void setIsInvokableOnlyFromStm(Boolean isInvokableOnlyFromStm) {
		this.isInvokableOnlyFromStm = isInvokableOnlyFromStm;
	}
	
	public String getAcls() {
		return acls;
	}
	public void setAcls(String acls) {
		this.acls = acls;
	}

	@Override
	public String toString() {
		return "TransitionDescription [eventId=" + eventName + ", newStateId="
				+ newStateId + ", newFlowId=" + newFlowId
				+ ", isRetrievalTransition=" + isRetrievalTransition
				+ ", transitionAction=" + transitionAction + "]";
	}

}
