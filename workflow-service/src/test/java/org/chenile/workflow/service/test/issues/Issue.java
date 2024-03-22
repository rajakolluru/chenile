package org.chenile.workflow.service.test.issues;

import org.chenile.workflow.model.AbstractStateEntity;

public class Issue extends AbstractStateEntity{
	private static final long serialVersionUID = 5943127292911636088L;	
	public String assignee;
	public String assignComment;
	public String closeComment;
	public String resolveComment;
	public String description;
	public String openedBy;
}
