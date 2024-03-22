package org.chenile.workflow.model;

import java.util.Date;

import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.utils.entity.model.Entity;

public class AbstractStateEntity extends Entity implements StateEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2976394902595668621L;
	private State state;
	private Date stateEntryTime;
	private int slaTendingLate = 0;
	private int slaLate = 0;
	
	public AbstractStateEntity() {
		
	}
	
	@Override
	public void setCurrentState(State currentState) {
		this.state = currentState;
		
	}
	@Override
	public State getCurrentState() {
		return state;
	}

	public Date getStateEntryTime() {
		return stateEntryTime;
	}

	public void setStateEntryTime(Date stateEntryTime) {
		this.stateEntryTime = stateEntryTime;
	}

	public int getSlaTendingLate() {
		return slaTendingLate;
	}

	public void setSlaTendingLate(int slaTendingLate) {
		this.slaTendingLate = slaTendingLate;
	}

	public int getSlaLate() {
		return slaLate;
	}

	public void setSlaLate(int slaLate) {
		this.slaLate = slaLate;
	}

}