package org.chenile.owiz.config.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.chenile.owiz.Command;

/**
 * Stores the meta data about all the commands that have been configured in the system.
 * @author Raja Shankar Kolluru
 *
 */
public class CommandDescriptor<InputType> {
	public Command<InputType> getCommand(){
		return command;
	}
	public void setCommand(Command<InputType> command){
		this.command = command;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public boolean isFirst() {
		return first;
	}
	public Set<AttachmentDescriptor<InputType>> getAttachmentDescriptors() {
		return attachmentDescriptors;
	}
	
	public void addAttachmentDescriptor(AttachmentDescriptor<InputType> attachmentDescriptor) {
		this.attachmentDescriptors.add(attachmentDescriptor);
	}
	
	//public void setAttachmentDescriptor(AttachmentDescriptor<InputType> attachmentDescriptor) {
		//this.attachmentDescriptor = attachmentDescriptor;
	//}
	private String id;
	private String componentName;
	private String defaultAttachmentTag;
	private String tagName;
	private boolean first = false;
	private Set<AttachmentDescriptor<InputType>> attachmentDescriptors = new HashSet<AttachmentDescriptor<InputType>>();
	private Command<InputType> command;
	private Map<String, String> properties = new HashMap<String, String>();
	private CustomCommandTagDescriptor customCommandTagDescriptor;
	private FlowDescriptor<InputType> flowDescriptor;
	
	public String toXml(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<command id='" + id + "' componentName='" + componentName + "' first='" + first + "' >\n");
		buffer.append("<attachmentDescriptors>");
		for(AttachmentDescriptor<InputType> attachmentDescriptor: attachmentDescriptors){
			buffer.append( attachmentDescriptor.toXml());
		}
		buffer.append("</attachmentDescriptors>");
		buffer.append("</command>\n");
		return buffer.toString();
	}
	public void addComponentProperty(String name, String value) {
		
		int ind = name.lastIndexOf("/");
		if (ind != -1 )
			name = name.substring(ind + 1);
		
		if (name.equals("componentName") || name.equals("id") || name.equals("first")){
        	if ("id".equals(name))
        		setId(value);
        	else if ("componentName".equals(name))
        		setComponentName(value);
        	else 
        		setFirst(Boolean.parseBoolean(value));
        }

		properties.put(name,value);
	}
	
	public Map<String,String> getProperties(){
		return properties;
	}
	
	public String getPropertyValue(String name){
		return properties.get(name);
	}
	public void setCustomCommandTagDescriptor(CustomCommandTagDescriptor customCommandTagDescriptor) {
		this.customCommandTagDescriptor = customCommandTagDescriptor;
	}
	public CustomCommandTagDescriptor getCustomCommandTagDescriptor() {
		return customCommandTagDescriptor;
	}
	public void setFlowDescriptor(FlowDescriptor<InputType> flowDescriptor) {
		this.flowDescriptor = flowDescriptor;
	}
	public FlowDescriptor<InputType> getFlowDescriptor() {
		return flowDescriptor;
	}
	public String getDefaultAttachmentTag() {
		return defaultAttachmentTag;
	}
	public void setDefaultAttachmentTag(String defaultAttachmentTag) {
		this.defaultAttachmentTag = defaultAttachmentTag;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
