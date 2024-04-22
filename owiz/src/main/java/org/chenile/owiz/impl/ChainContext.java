package org.chenile.owiz.impl;

import java.util.List;

import org.chenile.owiz.Command;

public class ChainContext<InputType> {

	/**This class specifies a save point.
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
