package org.chenile.stm;

import java.io.Serializable;
/**
 * An encapsulation of what constitutes a state. In this framework, a state is a combination of flowId and stateId.
 * The flowId is used so that the same STM can be used in different flows.The state Id is namespaced by the flow.
 * @author Raja Shankar Kolluru
 *
 */
public class State implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7898900078541L;

	private String stateId;
	private String flowId;
	/** 
	 * If flow Id is not specified, it is assumed to be the default flow.
	 */
	public static final String DEFAULT_FLOW_ID = "__DEFAULT_FLOW__";
	
	public State(String stateId, String flowId) {
		super();
		this.stateId = stateId;
		this.flowId = flowId;
	}
	
	public State(){
		
	}
	
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	public String getStateId() {
		return stateId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getFlowId() {
		return flowId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowId == null) ? 0 : flowId.hashCode());
		result = prime * result + ((stateId == null) ? 0 : stateId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (flowId == null) {
			if (other.flowId != null)
				return false;
		} else if (!flowId.equals(other.flowId))
			return false;
		if (stateId == null) {
			if (other.stateId != null)
				return false;
		} else if (!stateId.equals(other.stateId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "State [flowId=" + flowId + ", stateId=" + stateId + "]";
	}
}
