package org.chenile.owiz.config.model;

import java.util.HashMap;

/**
 * Contains the meta data about the attachment of a command to another.
 * Every command must have this information unless it is a first command.
 * @author Raja Shankar Kolluru
 *
 */
public class AttachmentDescriptor<InputType> extends HashMap<String, String>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4217352231505940106L;
	private static final String PARENT_ID_NAME = "parentId";
	
	public String getParentId() {
		return get(PARENT_ID_NAME);
	}
	public void setParentId(String pid) {
		put(PARENT_ID_NAME,pid);
	}
	
	public void setParent(CommandDescriptor<InputType> parent) {
		this.parent = parent;
	}
	public CommandDescriptor<InputType> getParent() {
		return parent;
	}
	
	private CommandDescriptor<InputType> parent;
	public String toXml(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<attach-to ");
		for (String name: keySet()){
			buffer.append(name + "='" + get(name) + "' ");
		}
		buffer.append("/>\n");
		return buffer.toString();
	}
}
