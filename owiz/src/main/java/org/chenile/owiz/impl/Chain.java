package org.chenile.owiz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * A chain is a command that executes a chain of other commands.
 */
public class Chain<InputType> extends CommandBase<InputType> implements Command<InputType>, AttachableCommand<InputType> {

	public static final String INDEX = "index";
	protected Set<OrderedCommandDesc> commandDescSet = new TreeSet<OrderedCommandDesc>();
	protected int currentIndex = 1;
	
	protected void doExecute(InputType context) throws Exception {
		
		for (Command<InputType> cmd: obtainExecutionCommands(context)){
			cmd.execute(context);
			if (shouldStopChain(context)){
				break;
			}
		}
	}
		
	/**
	 * AN extension point to allow sub classes to stop a chain if a
	 * condition is satisfied.
	 * @param context containing the data required.
	 * @return false by default.
	 * 
	 * @throws Exception .
	 */
	protected boolean shouldStopChain(InputType context) throws Exception {
		return false;
	}

	/**
	 * Allow a command to attach itself to this chain. 
	 * If the index is not specified then the attachment order defines the 
	 * execution order. Else if index is explicitly specified then the 
	 * execution order would be the ascending order of the index.
	 */
	public void attachCommand(
			AttachmentDescriptor<InputType> attachmentDescriptor,
			CommandDescriptor<InputType> command) {
		String index = attachmentDescriptor.get(INDEX);
		int indexInt = currentIndex++;
		if (index != null) {
			indexInt = Integer.parseInt(index);
		}else {
			attachmentDescriptor.put(INDEX,indexInt + "");
		}
		commandDescSet.add(new OrderedCommandDesc(indexInt, command));		
	}
	
	
	/**
	 * Obtain the list of commands that needs to be actually executed.
	 * Considers {@link InterpolationCommand} - these commands are supposed to 
	 * emit out the actual commands that need to be executed. 
	 * @param context - the context that was passed
	 * @return - the set of commands applicable for that context
	 */
	protected List<Command<InputType>> obtainExecutionCommands(InputType context){
		List<Command<InputType>> commandsList = new ArrayList<Command<InputType>>();
		for (OrderedCommandDesc ocd: commandDescSet){
			@SuppressWarnings("unchecked")
			Command<InputType> cmd = (Command<InputType>)ocd.getCommandDescriptor().getCommand();
			if (cmd instanceof InterpolationCommand) {
				List<Command<InputType>> clist = ((InterpolationCommand<InputType>)cmd).fetchCommands(context);
				if (clist != null && clist.size() > 0)
					commandsList.addAll(clist);
			}else {
				commandsList.add(cmd);
			}
		}
		return commandsList;
	}


}
