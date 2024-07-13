package org.chenile.jpautils.entity;

import java.util.Date;

import jakarta.persistence.*;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.utils.entity.model.ExtendedStateEntity;

@MappedSuperclass
public abstract class AbstractJpaStateEntity extends BaseJpaEntity implements ExtendedStateEntity {
	private State state;
	private Date stateEntryTime;
	private int slaTendingLate = 0;
	private int slaLate = 0;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="flowId", column=@Column(name = "flowId")),
			@AttributeOverride(name="stateId", column=@Column(name = "stateId"))
	})
	public State currentState;
	public AbstractJpaStateEntity() {
		
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