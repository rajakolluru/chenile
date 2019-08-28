package org.chenile.stm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateDescriptionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String stateName;

	private Boolean isInitialState;

	private String entryAction;

	private Boolean isFinalState;

	private String exitAction;

	private String stateType;

	private Long flowId;

	private String componentName;
	
	private List<StateAttributesDTO> stateAttributes=new ArrayList<>();
	
	private Map<String,String> metaData=new HashMap<>();

	private List<TransitionDescriptionDTO> transitionDescriptions=new ArrayList<>();

	
	public List<StateAttributesDTO> getStateAttributes() {
		return stateAttributes;
	}
	public void setStateAttributes(List<StateAttributesDTO> stateAttributes) {
		this.stateAttributes = stateAttributes;
	}
	
	public Map<String, String> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsInitialState() {
		return isInitialState;
	}
	public void setIsInitialState(Boolean isInitialState) {
		this.isInitialState = isInitialState;
	}
	public String getEntryAction() {
		return entryAction;
	}
	public void setEntryAction(String entryAction) {
		this.entryAction = entryAction;
	}
	public Boolean getIsFinalState() {
		return isFinalState;
	}
	public void setIsFinalState(Boolean isFinalState) {
		this.isFinalState = isFinalState;
	}
	public String getExitAction() {
		return exitAction;
	}
	public void setExitAction(String exitAction) {
		this.exitAction = exitAction;
	}
	public String getStateType() {
		return stateType;
	}
	public void setStateType(String stateType) {
		this.stateType = stateType;
	}

	public Long getFlowId() {
		return flowId;
	}
	public void setFlowId(Long flowId) {
		this.flowId = flowId;
	}

	public List<TransitionDescriptionDTO> getTransitionDescriptions() {
		return transitionDescriptions;
	}
	public void setTransitionDescriptions(List<TransitionDescriptionDTO> transitionDescriptions) {
		this.transitionDescriptions = transitionDescriptions;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}


	@Override
	public String toString() {
		return "StateDescription [id=" + id + ", isInitialState=" + isInitialState
				+ ", entryAction=" + entryAction + ", isFinalState=" + isFinalState
				+ ", exitAction=" + exitAction + ", stateType=" + stateType
				+ ", flowId=" + flowId + ", componentName=" + componentName
				+ ", transitionDescriptions=" + transitionDescriptions + "]";
	}
	
	
}
