package org.chenile.owiz.impl;

import java.util.List;

import org.chenile.owiz.Command;

public class ChainContext {
	private List<Command<?>> commands ;
	private Object context ;
	private int index = 0;
	public ChainContext(List<Command<?>> commands,Object context){
		this.commands = commands;
		this.context = context;
	}
	
	/**
	 * Designed to be called again and again till the chain stops
	 * @throws Exception .
	 */
	@SuppressWarnings("unchecked")
	public void doContinue() throws Exception {
		if (index == commands.size()) return;
		Command<Object> currCommand = (Command<Object>)commands.get(index++);
		currCommand.execute(context);
	}
}
