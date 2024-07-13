package org.chenile.workflow.service.test.issues;

import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.utils.entity.model.AbstractExtendedStateEntity;
import org.chenile.utils.entity.model.BaseEntity;

public class Issue extends AbstractExtendedStateEntity {

	public String assignee;
	public String assignComment;
	public String closeComment;
	public String resolveComment;
	public String description;
	public String openedBy;
	public State state;

	@Override
	public void setCurrentState(State currentState) {
		this.state = currentState;
	}

	@Override
	public State getCurrentState() {
		return state;
	}
}
