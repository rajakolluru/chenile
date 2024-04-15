package org.chenile.owiz.config.impl;

import java.util.Map;

import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.CustomAttachmentTagDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;

public class HandleAttachment<InputType> extends OwizRule<InputType> {
	protected Map<String,CustomAttachmentTagDescriptor> attachmentTagMap;
	public HandleAttachment(Map<String,CustomAttachmentTagDescriptor> attachmentTagMap) {
		this.attachmentTagMap = attachmentTagMap;
	}
	
	public boolean isRegisteredAttachmentTag(String xmlElementName) {
		return attachmentTagMap.containsKey(xmlElementName);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void begin(String namespace, String xmlElementName, Attributes attributes)
			throws OwizConfigException {
			CustomAttachmentTagDescriptor catd = attachmentTagMap.get(xmlElementName);
			
			@SuppressWarnings("unchecked")
			CommandDescriptor<InputType> commandDescriptor = (CommandDescriptor<InputType>) digester.peek();
			AttachmentDescriptor<InputType> ad = new AttachmentDescriptor<InputType>();	
			
			if (null != attributes){
				for (int i = 0; i < attributes.getLength(); i++) {
		            String name = getKey(attributes,i);
		            String value = attributes.getValue(i);
		            ad.put(name, value);
				}
			}
			// parentId must not be mandatory. automatically attach it to the containing command.
			if (ad.getParentId() == null){

				CommandDescriptor<InputType> cd = null;
				Object o = digester.peek(1);
				if (o instanceof CommandDescriptor )
					cd = (CommandDescriptor<InputType>) o;

				if (cd == null){
					if (xmlElementName != null && xmlElementName.startsWith("attach-to-")){
						// extract the command name from the xml element name
						String commandId = xmlElementName.substring("attach-to-".length());
						if(getFlow().getCommandCatalog().containsKey(commandId)){
							cd = getFlow().getCommandCatalog().get(commandId);
						} else {
							throw new OwizConfigException("Attempt to attach a command to a non existent parent in "
							+ " tag = " + xmlElementName + " command = " + commandDescriptor.getId());
						}
					}
				}
				if (cd == null)
					throw new OwizConfigException("Parent ID is mandatory for attachments. XmlElementName = " +
							xmlElementName + " command = " + commandDescriptor.getId());
				
				ad.setParentId(cd.getId());
			}
			// Ensure that additional attributes of the attach tag are set in the attachment descriptor
			// as  properties. These would be available to the containing commands as attachment attributes.
			
			if (catd != null) {
				for (String key : catd.keySet()) {
					String value = catd.get(key);
					ad.put(key, value);
				}
			}
			// Ensure that the properties of the command are also available in the attachment descriptor
			// This will make the syntax much more succinct. 
			Map<String,String> props = commandDescriptor.getProperties();
			for (String key: props.keySet()){
				ad.put(key, props.get(key));
			}
			commandDescriptor.addAttachmentDescriptor(ad);
	}
}
