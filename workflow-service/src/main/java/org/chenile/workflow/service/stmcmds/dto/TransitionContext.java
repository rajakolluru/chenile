package org.chenile.workflow.service.stmcmds.dto;

import org.chenile.stm.State;
import org.chenile.stm.model.Transition;

public class TransitionContext<T> {
		
		public T getEntity() {
			return entity;
		}
		public String getEventId() {
			return eventId;
		}
		public Object getTransitionParam() {
			return transitionParam;
		}
		public State getStartState() {
			return startState;
		}
		public State getEndState() {
			return endState;
		}
		public Transition getTransition() {
			return transition;
		}
		
		private T entity;
		private String eventId;
		private Object transitionParam;
		private State startState;
		private State endState;
		private Transition transition;
		public  TransitionContext(T entity, String eventId, Object transitionParam,
			State startState, State endState, Transition transition){
			this.entity = entity; this.eventId = eventId; this.transitionParam = transitionParam;
			this.startState = startState; this.endState = endState; this.transition = transition;
		}
		
		
}
