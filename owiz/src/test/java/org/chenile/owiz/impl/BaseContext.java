package org.chenile.owiz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseContext extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3736375616033384338L;
	private List<Integer> listOfIndexes = new ArrayList<Integer>();
	private String module;
	private String commandIdExecuted;
	
	public void addIndex(int index) {
		listOfIndexes.add(index);
	}
	
	public List<Integer> getListOfIndexes(){
		return listOfIndexes;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return module;
	}


	public void setCommandIdExecuted(String commandIdExecuted) {
		this.commandIdExecuted = commandIdExecuted;
	}

	public String getCommandIdExecuted() {
		return commandIdExecuted;
	}
	
	
	
}
