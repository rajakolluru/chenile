package org.chenile.stm;

/**
 * Every entity that has a state that should be tracked must implement this interface.
 * 
 * @author Raja Shankar Kolluru
 *
 */
public interface StateEntity {

	public abstract void setCurrentState(State currentState);

	public abstract State getCurrentState();

}