package org.chenile.utils.entity.model;

import org.chenile.stm.State;

import java.util.Date;

/**
 * Non JPA version of Extended State Entity. Use it if you don't want the JPA annotations.
 */
public abstract class AbstractExtendedStateEntity extends BaseEntity implements ExtendedStateEntity {
	private State state;
	private Date stateEntryTime;
	private int slaTendingLate = 0;
	private int slaLate = 0;
	@Override
	public void setCurrentState(State currentState) {this.state = currentState;}
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