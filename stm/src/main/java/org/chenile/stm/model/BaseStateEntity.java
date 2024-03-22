package org.chenile.stm.model;

import java.util.HashMap;

import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
/**
 * 
 * @author raja
 * Provides a state entity interface. Can be used for other classes whose state needs to be managed by the STM
 * 
 */
public class BaseStateEntity extends HashMap<String,Object> implements StateEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7266939204262735739L;
	private State currentState; 

	/** 
	 * @see org.chenile.stm.StateEntity#setCurrentState(org.chenile.stm.State)
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	/** 
	 * @see org.chenile.stm.StateEntity#getCurrentState()
	 */
	public State getCurrentState() {
		return currentState;
	}
	

}
