package org.chenile.stm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FlowDescriptionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long id;
	
	private String flowName;

	private Boolean isDefault;
	
	private String entryAction;

	private String exitAction;

	private String retrievalStrategy;

	private Boolean skipEntryExitActionsForAutoStates;
	
	private String stmSecurityStrategy;

	private List<StateDescriptionDTO> stateDescriptions=new ArrayList<>();

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getEntryAction() {
		return entryAction;
	}

	public void setEntryAction(String entryAction) {
		this.entryAction = entryAction;
	}

	public String getExitAction() {
		return exitAction;
	}

	public void setExitAction(String exitAction) {
		this.exitAction = exitAction;
	}

	public String getRetrievalStrategy() {
		return retrievalStrategy;
	}

	public void setRetrievalStrategy(String retrievalStrategy) {
		this.retrievalStrategy = retrievalStrategy;
	}

	public Boolean getSkipEntryExitActionsForAutoStates() {
		return skipEntryExitActionsForAutoStates;
	}

	public void setSkipEntryExitActionsForAutoStates(
			Boolean skipEntryExitActionsForAutoStates) {
		this.skipEntryExitActionsForAutoStates = skipEntryExitActionsForAutoStates;
	}

	public List<StateDescriptionDTO> getStateDescriptions() {
		return stateDescriptions;
	}

	public void setStateDescriptions(List<StateDescriptionDTO> stateDescriptions) {
		this.stateDescriptions = stateDescriptions;
	}
	
	public String getStmSecurityStrategy() {
		return stmSecurityStrategy;
	}

	public void setStmSecurityStrategy(String stmSecurityStrategy) {
		this.stmSecurityStrategy = stmSecurityStrategy;
	}
	
	
	@Override
	public String toString() {
		return "FlowDescriptionDTO [id=" + id + ", isDefault=" + isDefault
				+ ", entryAction="
				+ entryAction + ", exitAction=" + exitAction
				+ ", retrievalStrategy=" + retrievalStrategy
				+ ", skipEntryExitActionsForAutoStates="
				+ skipEntryExitActionsForAutoStates + ", stateDescriptions="
				+ stateDescriptions + "]";
	}
	
}
