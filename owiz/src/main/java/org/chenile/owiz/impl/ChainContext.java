package org.chenile.owiz.impl;

import java.util.List;

import org.chenile.owiz.Command;

/**
 * This class is used by {@link FilterChain}. This is passed as part of the
 * context. (by classes implementing {@link ChainContextContainer}) The class allows
 * a Command which is called by the FilterChain to pass control to the commands that
 * are downstream from it. The {@link #doContinue()} executes the downstream commands.
 * This method will be recursively called till the last command is executed in the Chain.
 * <p>This context also can roll back the Commands that have already been called if
 * required by using the {@link #savePoint()}  and {@link #resumeFromSavedPoint(SavePoint)}
 * methods. </p>
 * @param <InputType>
 */
public class ChainContext<InputType> {

	/**
	 * This class specifies a save point.
	 * You can restart the chain from saved points.
	 */
	public static class SavePoint{
		int savedIndex;
		public SavePoint(int index){
			this.savedIndex = index;
		}
	}
	private List<Command<InputType>> commands ;
	private Object context ;
	private int index = 0;
	public ChainContext(List<Command<InputType>> commands,Object context){
		this.commands = commands;
		this.context = context;
	}

	public SavePoint savePoint(){
		return new SavePoint(index);
	}

	public void resumeFromSavedPoint(SavePoint savedPoint) throws Exception{
		this.index = savedPoint.savedIndex;
		doContinue();
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
