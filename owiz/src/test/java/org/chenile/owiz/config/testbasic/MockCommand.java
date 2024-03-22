package org.chenile.owiz.config.testbasic;

import org.chenile.owiz.impl.CommandBase;

public class MockCommand  extends CommandBase<MockContext>{
	private String id;
	public MockCommand(String id){
		this.id = id;
	}

	public void doExecute(MockContext context) throws Exception {
		context.log(id);
	}
}
