package org.chenile.workflow.service.test.issues;

import org.chenile.workflow.param.MinimalPayload;

import java.io.Serial;

public class AssignIssuePayload extends MinimalPayload{
	@Serial
	private static final long serialVersionUID = 7166835437051551936L;
	public String assignee;	
}
