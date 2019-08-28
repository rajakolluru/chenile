package org.chenile.owiz.impl;

import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.OrchConfigurator;

public class OrchExecutorImpl<InputType> implements OrchExecutor<InputType> {
	private OrchConfigurator<InputType> orchConfigurator;
	
	
	public void execute(String flowId, InputType context) throws Exception {
		Command<InputType> command = orchConfigurator.obtainFirstCommand(flowId);
		doExecute(command,context);
	}

	public void execute(InputType context) throws Exception {
		Command<InputType> command = orchConfigurator.obtainFirstCommand();
		doExecute(command,context);
	}

	protected void doExecute(Command<InputType> command, InputType context) throws Exception {
		command.execute(context);
	}

	public void setOrchConfigurator(OrchConfigurator<InputType> orchConfigurator) {
		this.orchConfigurator = orchConfigurator;
	}


}
