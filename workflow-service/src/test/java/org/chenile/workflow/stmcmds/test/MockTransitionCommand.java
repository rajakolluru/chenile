package org.chenile.workflow.stmcmds.test;

import org.chenile.owiz.Command;
import org.chenile.workflow.service.stmcmds.dto.TransitionContext;

public class MockTransitionCommand implements Command<TransitionContext<SomeEntity>>{
	
	private String stringToAdd;
	

	public MockTransitionCommand(String stringToAdd) {
		this.stringToAdd = stringToAdd;
	}


	@Override
	public void execute(TransitionContext<SomeEntity> context) throws Exception {
		SomeEntity someEntity = context.getEntity();
		someEntity.getListOfStrings().add(stringToAdd);		
	}

}
