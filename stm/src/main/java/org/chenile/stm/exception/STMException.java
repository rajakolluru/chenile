package org.chenile.stm.exception;

import org.chenile.stm.State;

/**
 * Custom exception for the STM framework.
 * @author Raja Shankar Kolluru
 *
 */
public class STMException extends Exception {

	public static final int INVALID_STD = 500;
	public static final int INVALID_STATE = 501;
	public static final int UNAVAILABLE_TRANSITION = 550;
	public static final int INVALID_TRANSITION = 551;
	public static final int INVALID_EVENTID = 580;
	public static final int UNDEFINED_INITIAL_STATE_OR_FLOW = 600;
	public static final int UNABLE_TO_CREATE_COMPONENT = 650;
	
	
	private static final long serialVersionUID = 8741909778071370857L;
	
	
	private State state;
	private int messageId;
	
	public STMException(String message, int messageId, Throwable cause) {
		super(message, cause);
		this.messageId = messageId;
	}

	public STMException(String message,int messageId) {
		super(message);
		this.messageId = messageId;
	}
	
	public STMException(String message, int messageId,State state) {
		super(message);
		this.state = state;
		this.messageId = messageId;
	}
	
	public int getMessageId(){
		return this.messageId;
	}
	
	public State getSate(){
		return this.state;
	}
	
	public String toString(){
		return super.toString() + "\nstate = " + state + "\nMessageId = " + messageId;
	}

}
