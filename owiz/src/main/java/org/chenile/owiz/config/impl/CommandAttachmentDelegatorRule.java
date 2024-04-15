package org.chenile.owiz.config.impl;

import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.chenile.owiz.config.model.CustomAttachmentTagDescriptor;
import org.chenile.owiz.config.model.CustomCommandTagDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;



/**
 * Custom digester rule to process either the command or attach Descriptor.
 * We don't know if the tag is a command or an attachment. This rule finds that out
 * and delegates it to the respective rules for handling commands or attachments.
 * @author raja
 *
 */

public class CommandAttachmentDelegatorRule<InputType> extends Rule {
	private  HandleAttachment<InputType> handleAttachment;
	private  HandleCommand<InputType> handleCommand;
	private  HandleRegisteredCommand<InputType> handleRegisteredCommand;
	
	public CommandAttachmentDelegatorRule(IDGenerator idGenerator, Map<String,CustomCommandTagDescriptor> commandTagMap,
			Map<String,CustomAttachmentTagDescriptor> attachmentTagMap ){		
		handleAttachment = new HandleAttachment<InputType>(attachmentTagMap);
		handleCommand = new HandleCommand<InputType>(idGenerator,handleAttachment);
		handleRegisteredCommand = new HandleRegisteredCommand<>(idGenerator,handleAttachment,commandTagMap);
	}
	@Override
	public void setDigester(Digester digester) {
		super.setDigester(digester);
		handleCommand.setDigester(digester);
		handleAttachment.setDigester(digester);
		handleRegisteredCommand.setDigester(digester);
	}
	@Override
	public void end(String namespace, String xmlElementName) throws Exception {
		super.end(namespace, xmlElementName);
		if (handleRegisteredCommand.isRegisteredCommandTag(xmlElementName))
			handleRegisteredCommand.end(namespace,xmlElementName);
		else if (handleAttachment.isRegisteredAttachmentTag(xmlElementName))
			handleAttachment.end(namespace,xmlElementName);
		else if (xmlElementName != null  && xmlElementName.startsWith("attach-to"))
			handleAttachment.end(namespace,xmlElementName);
		else
			handleCommand.end(namespace,xmlElementName);
	}
	@Override
	public void begin(String namespace, String xmlElementName, Attributes attributes)
			throws OwizConfigException {
		if (handleRegisteredCommand.isRegisteredCommandTag(xmlElementName))
			handleRegisteredCommand.begin(namespace,xmlElementName,attributes);
		else if (handleAttachment.isRegisteredAttachmentTag(xmlElementName))
			handleAttachment.begin(namespace,xmlElementName,attributes);
		else if (xmlElementName != null  && xmlElementName.startsWith("attach-to-"))
			handleAttachment.begin(namespace,xmlElementName,attributes);
		else
			handleCommand.begin(namespace,xmlElementName,attributes);		
	}		
}