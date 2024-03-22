package org.chenile.owiz.config.impl;

import java.util.Map;

import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.CustomCommandTagDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;

public class HandleRegisteredCommand<InputType> extends HandleCommand<InputType>{

	private Map<String, CustomCommandTagDescriptor> commandTagMap;

	public HandleRegisteredCommand(IDGenerator idGenerator,HandleAttachment<InputType> handleAttachment,
			Map<String,CustomCommandTagDescriptor> commandTagMap) {
		super(idGenerator,handleAttachment);
		this.commandTagMap = commandTagMap;
	}
	
	public boolean isRegisteredCommandTag(String xmlElementName) {
		return commandTagMap.containsKey(xmlElementName);
	}

	@Override
	public void begin(String namespace, String xmlElementName, Attributes attributes)
			throws OwizConfigException {
	
		CustomCommandTagDescriptor cctd = commandTagMap.get(xmlElementName);	
		CommandDescriptor<InputType> commandDescriptor = makeCommandDescriptor(xmlElementName, attributes);
		commandDescriptor.setCustomCommandTagDescriptor(cctd);
		setComponentName(commandDescriptor,attributes,xmlElementName,cctd);
		if (cctd.getDefaultAttachmentTag() != null)
			commandDescriptor.setDefaultAttachmentTag(cctd.getDefaultAttachmentTag());

		// Ensure that additional attributes of the command tag are set in the command descriptor
		// as component properties. These would be available to the commands.
		
		for (String key: cctd.keySet() ){
			String value = cctd.get(key);
			commandDescriptor.addComponentProperty(key, value);
		}
		addToFlow(commandDescriptor);
	}
	
	public void setComponentName(CommandDescriptor<InputType> cd, Attributes attributes, String xmlElementName,
			CustomCommandTagDescriptor cctd) {
		String componentName = getComponentNameFromAttributes(attributes);
		if (componentName == null) {
			componentName = cctd.getComponentName();
		}
		if (componentName == null)
			componentName = getXmlNameAsCamelCase(xmlElementName);
		cd.setComponentName(componentName);
	}
}
