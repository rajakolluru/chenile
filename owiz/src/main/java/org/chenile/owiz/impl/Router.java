package org.chenile.owiz.impl;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * A router is a command that executes one of several commands based on a routing string.
 * The computation of the routing string is left to a sub class and hence this is an abstract class.
 * @author Raja Shankar Kolluru
 *
 */
public abstract class Router<InputType> extends CommandBase<InputType> implements Command<InputType>,AttachableCommand<InputType> {

	public static final String ROUTE = "route";
	public static final String DEFAULT="default";
	
	
	public void attachCommand(
			AttachmentDescriptor<InputType> attachmentDescriptor,
			CommandDescriptor<InputType> command) {
		String route = attachmentDescriptor.get(ROUTE);
		if (route.equals(DEFAULT))
				defaultCommandDescriptor = command;
		else 
			commandsMap.put(attachmentDescriptor.get(ROUTE),command);
		
	}
	
	protected Map<String, CommandDescriptor<InputType>> commandsMap = new HashMap<String, CommandDescriptor<InputType>>();
	protected CommandDescriptor<InputType> defaultCommandDescriptor;
	
	public void doExecute(InputType context) throws Exception {
		String routingString = computeRoutingString(context);
		
		if (routingString == null || !commandsMap.containsKey(routingString)){
			if (defaultCommandDescriptor != null){
				defaultCommandDescriptor.getCommand().execute(context);
				return;
			}
			else	
				throw new Exception("The routingString " + routingString + " has not been configured for this command!");
		}
		commandsMap.get(routingString).getCommand().execute(context);
	}
	
	protected abstract String computeRoutingString(InputType context) throws Exception;

}
