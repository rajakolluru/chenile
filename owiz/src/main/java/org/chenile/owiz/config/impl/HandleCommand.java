package org.chenile.owiz.config.impl;

import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.FlowDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;

public class HandleCommand<InputType> extends OwizRule {
	public final String DEFAULT_ATTACHMENT_TAG = "attach-to";
	protected IDGenerator idGenerator;
	protected HandleAttachment<InputType> handleAttachment;
	public HandleCommand(IDGenerator idGenerator, HandleAttachment<InputType> handleAttachment){
		this.idGenerator = idGenerator;
		this.handleAttachment = handleAttachment;
	}
	
	protected CommandDescriptor<InputType> makeCommandDescriptor(String xmlElementName,  Attributes attributes){
		CommandDescriptor<InputType> commandDescriptor = new CommandDescriptor<InputType>();
		commandDescriptor.setTagName(xmlElementName);
		commandDescriptor.setDefaultAttachmentTag(DEFAULT_ATTACHMENT_TAG);
		String componentName = getXmlNameAsCamelCase(xmlElementName);
		digester.push(commandDescriptor);
		// add the  attributes to the componentProperties
		for (int i = 0; i < attributes.getLength(); i++) {
            String name = getKey(attributes,i);
            String value = attributes.getValue(i);
            commandDescriptor.addComponentProperty(name, value);
		}
		if (commandDescriptor.getComponentName() != null) {
			componentName = commandDescriptor.getComponentName();
		}
		// default the component name and ID if not supplied.  
		if(componentName != null && commandDescriptor.getComponentName() == null){
			commandDescriptor.setComponentName(componentName);
		}
		if (commandDescriptor.getId() == null) {
			// try to use the xml element name as the ID.
			// If it is already used, try to use the componentName as the ID.
			// else use the ID generator to generate a new ID.
			FlowDescriptor<InputType> flow = getFlow();
			String id = xmlElementName;
			if (flow.getCommandCatalog().containsKey(id)) id = componentName;
			if (flow.getCommandCatalog().containsKey(id)) id = idGenerator.generateId();
			commandDescriptor.setId(id);
		}
		return commandDescriptor;
	}
	
	@SuppressWarnings("unchecked")
	private FlowDescriptor<InputType> getFlow(){
		return (FlowDescriptor<InputType>) digester.peek(XmlOrchConfigurator.FLOW_STACK);
	}
	
	protected void addToFlow(CommandDescriptor<InputType> commandDescriptor) {
		getFlow().addCommand(commandDescriptor);
	}
	@Override
	public void begin(String namespace, String xmlElementName, Attributes attributes)
			throws OwizConfigException {
		CommandDescriptor<InputType> commandDescriptor = makeCommandDescriptor(xmlElementName, attributes);
		addToFlow(commandDescriptor);
	}
	@Override
	public void end(String namespace, String xmlElementName) throws Exception {
		super.end(namespace, xmlElementName);
		@SuppressWarnings("unchecked")
		CommandDescriptor<InputType> commandDescriptor = (CommandDescriptor<InputType>) digester.peek();
			
		// Before popping the command out of the stack, we should ensure that every command (other than the first command)
		// is attached to another command. Otherwise the command will never be accessed by the framework during runtime!!
		// This is only possible if the commands are nested or if there is an attach-to clause under the command.
		// Otherwise we should throw an OwizConfigException. 
		// If the containing command has the notion of a default attachment descriptor, then that should be picked up
		// and the command must be attached to its parent using the default attachment descriptor.
		// If there is no default attachment descriptor then the "attach-to" tag is used by default.
		
		if (!commandDescriptor.isFirst() && commandDescriptor.getAttachmentDescriptors().size() == 0){
			Object o = digester.peek(1);
			// if it is not a top level command attach it to the command above it. 
			// if there is a default attachment type to the parent command.
			if (null == o || !(o instanceof CommandDescriptor))
				throw new OwizConfigException("Command " + commandDescriptor.getComponentName() + " and Id = " + commandDescriptor.getId()
						+ " will not be accessible by the framework since it is not attached to any other command. Did you " +
						"forget he attach-to tag or did you not nest it under its parent?");
			
			@SuppressWarnings("unchecked")
			CommandDescriptor<InputType> parent = (CommandDescriptor<InputType>)o;
			//CustomCommandTagDescriptor parentCctd = parent.getCustomCommandTagDescriptor();
			//if (parentCctd == null) {
				//System.err.println("Parent cctd is null for " + parent.getId() + 
					//	" component name = " + parent.getComponentName());
			//}
			//String attachTag = parentCctd.getDefaultAttachmentTag();
			//attachTag = (attachTag == null)? "attach-to" : attachTag;

			handleAttachment.begin(namespace,parent.getDefaultAttachmentTag(),null);
			
		}
		digester.pop();
	}		
}
