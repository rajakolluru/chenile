package org.chenile.owiz.impl.parallelchain;

import org.chenile.owiz.impl.CommandBase;

public class LatchWaitCommand extends CommandBase<ParallelChainContext>{
	
	public String commandToWaitFor;
	public String thisCommand;
	public String exceptionMessage;

	public LatchWaitCommand(String commandToWaitFor,  String thisCommand) {
		this.commandToWaitFor = commandToWaitFor;
		this.thisCommand = thisCommand;
	}

	@Override
	protected void doExecute(ParallelChainContext context) throws Exception {
		
		if (commandToWaitFor != null)
			context.latches.get(commandToWaitFor).await();
		if(exceptionMessage != null)
			throw new RuntimeException(exceptionMessage);
		context.list.add(thisCommand);
		context.latches.get(thisCommand).countDown();
	}

	
}
