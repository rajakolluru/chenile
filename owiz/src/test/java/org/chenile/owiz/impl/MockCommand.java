package org.chenile.owiz.impl;

import org.chenile.owiz.impl.CommandBase;

public class MockCommand extends CommandBase<BaseContext>{
	
	private int index;

	public MockCommand(int index){
		this.index = index;
	}

	@Override
	protected void doExecute(BaseContext context) throws Exception {
		
		context.addIndex(index);
		context.setCommandIdExecuted(commandDescriptor.getId());
	}

}
