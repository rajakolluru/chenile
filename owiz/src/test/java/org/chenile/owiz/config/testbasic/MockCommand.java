package org.chenile.owiz.config.testbasic;

import org.chenile.owiz.Command;

public class MockCommand implements Command<MockContext>{
	private String id;
	public MockCommand(String id){
		this.id = id;
	}

	public void execute(MockContext context) throws Exception {
		context.log(id);
	}
}
