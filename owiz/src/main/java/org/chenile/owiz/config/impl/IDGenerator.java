package org.chenile.owiz.config.impl;

public class IDGenerator {
	// supports automatic ID generation for commands.
	private int generatedIdSequence = 1;
		
	public  String generateId(){
		return "generatedID" + generatedIdSequence++;
	}
}
