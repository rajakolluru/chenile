package org.chenile.owiz.impl;

import java.util.Set;
import java.util.TreeSet;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * A chain is a command that executes a chain of other commands.
 * @author Raja Shankar Kolluru
 *
 */
public class Chain<InputType> extends CommandBase<InputType> implements Command<InputType>, AttachableCommand<InputType> {

	public static final String INDEX = "index";
	protected Set<OrderedCommandDesc> commandDescSet = new TreeSet<OrderedCommandDesc>();
	protected int currentIndex = 1;
	
	protected void doExecute(InputType context) throws Exception {
		for (OrderedCommandDesc ocd: commandDescSet){
			@SuppressWarnings("unchecked")
			Command<InputType> cmd = (Command<InputType>)ocd.getCommandDescriptor().getCommand();
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


}
