package org.chenile.stm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatesDescriptionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String entryAction;
	
	private String exitAction;
	
	private List<FlowDescriptionDTO> flowDescriptions=new ArrayList<>();

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

	public List<FlowDescriptionDTO> getFlowDescriptions() {
		return flowDescriptions;
	}

	public void setFlowDescriptions(List<FlowDescriptionDTO> flowDescriptions) {
		this.flowDescriptions = flowDescriptions;
	}

	@Override
	public String toString() {
		return "StatesDescription [entryAction=" + entryAction
				+ ", exitAction=" + exitAction + ", flowDescriptions="
				+ flowDescriptions + "]";
	}
	
}
