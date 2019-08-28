package org.chenile.owiz.config.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.chenile.owiz.Command;

public class FlowDescriptor<InputType> {
	private String id;
	private Map<String, CommandDescriptor<InputType>> commandCatalog = new LinkedHashMap<String, CommandDescriptor<InputType>>();
	private boolean defaultFlow = false;
	
	public Map<String, CommandDescriptor<InputType>> getCommandCatalog() {
		return commandCatalog;
	}

	public void setCommandCatalog(
			Map<String, CommandDescriptor<InputType>> commandCatalog) {
		this.commandCatalog = commandCatalog;
	}
	private CommandDescriptor<InputType> firstCommand;
	
	public void addCommand(CommandDescriptor<InputType> commandDescriptor){
		commandCatalog.put(commandDescriptor.getId(), commandDescriptor);
		commandDescriptor.setFlowDescriptor(this);
		// default the first command.
		if (firstCommand == null) {
			firstCommand = commandDescriptor;
			firstCommand.setFirst(true);
			return;
		}
		if (commandDescriptor.isFirst()){
			firstCommand.setFirst(false); // make the existing first command a non first command.
			// switch the first command from the existing one to the new one. 
			firstCommand = commandDescriptor;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public Command<InputType> obtainFirstCommand() {
		return (firstCommand == null)? null : firstCommand.getCommand();
	}
	public CommandDescriptor<InputType> obtainCommandInfo(String id) {
			return commandCatalog.get(id);
	}
	public CommandDescriptor<InputType> obtainFirstCommandInfo() {
		return firstCommand;
	}

	public void setDefaultFlow(boolean defaultFlow) {
		this.defaultFlow = defaultFlow;
	}

	public boolean isDefaultFlow() {
		return defaultFlow;
	}
	
	public String toXml(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<flow id='" + id + "' defaultFlow='" + defaultFlow + "'>\n");
		for(CommandDescriptor<InputType> cd: commandCatalog.values()){
			buffer.append(cd.toXml());
		}
		buffer.append("</flow>\n");
		return buffer.toString();
	}
}
