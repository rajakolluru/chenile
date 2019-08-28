package org.chenile.owiz.impl;

import org.chenile.owiz.Command;
import org.chenile.owiz.OrchestrationAware;
import org.chenile.owiz.config.OrchConfigurator;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * Base class to implement command functionality. Typically should get customized for 
 * individual implementations. Inherit this command and over-ride the pre and post process methods.
 * @author Raja Shankar Kolluru
 *
 */
public abstract class CommandBase<InputType> implements Command<InputType>,
			OrchestrationAware<InputType>{


	protected OrchConfigurator<InputType> orchConfigurator;
	protected CommandDescriptor<InputType> commandDescriptor;
	
	public void setOrchConfigurator(OrchConfigurator<InputType> orchConfigurator) {
		this.orchConfigurator = orchConfigurator;
	}

	public void setCommandDescriptor(
			CommandDescriptor<InputType> commandDescriptor) {
		this.commandDescriptor = commandDescriptor;
	}

	public final void execute(InputType context) throws Exception{
		preprocess(context);
		doExecute(context);
		postprocess(context);
	}
	
	protected abstract void doExecute(InputType context) throws Exception ;

	public void preprocess(InputType context) throws Exception{
		
	}
	
	public void postprocess(InputType context) throws Exception{
		
	}
	
	protected String getConfigValue(String name){
		// see if the name of the attribute has been changed to something else.
		String attributeName = name + "AttributeName";
		String configuredAttributeName = commandDescriptor.getPropertyValue(attributeName);
		if (configuredAttributeName != null)name = configuredAttributeName;
		
		return commandDescriptor.getPropertyValue(name);
	}

	protected CommandDescriptor<InputType> obtainCommand(String key) {
		return commandDescriptor.getFlowDescriptor().getCommandCatalog().get(key);
	}

}
