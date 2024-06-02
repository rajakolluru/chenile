package org.chenile.owiz.impl;

import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * Internal class used to preserve the order of execution of commands.
 */
public class OrderedCommandDesc implements Comparable<OrderedCommandDesc> {
	/**
	 * This class is needed to ensure that the commands are retrieved in the correct order
	 */
	private int index;
	private CommandDescriptor<?> command;
	public OrderedCommandDesc(int index,CommandDescriptor<?> command){
		this.index = index;
		this.command = command;
	}
	public OrderedCommandDesc(String indx,CommandDescriptor<?> command){
		this.index = Integer.parseInt(indx);
		this.command = command;
	}
	public CommandDescriptor<?> getCommandDescriptor(){
		return command;
	}
	public int compareTo(OrderedCommandDesc o) {
		return (index - o.index );
	}
	
	public String toString() {
		return command.getId();
	}
		
}

