package org.chenile.owiz.config.impl;

import java.util.Map;

import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.CustomAttachmentTagDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;

public class HandleAttachment<InputType> extends OwizRule {
	protected Map<String,CustomAttachmentTagDescriptor> attachmentTagMap;
	public HandleAttachment(Map<String,CustomAttachmentTagDescriptor> attachmentTagMap) {
		this.attachmentTagMap = attachmentTagMap;
	}
	
	public boolean isRegisteredAttachmentTag(String xmlElementName) {
		return attachmentTagMap.containsKey(xmlElementName);
	}
	@Override
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
				@SuppressWarnings("unchecked")
				CommandDescriptor<InputType> cd = (CommandDescriptor<InputType>) digester.peek(1);
				
				ad.setParentId(cd.getId());
			}
			// Ensure that additional attributes of the attach tag are set in the attachment descriptor
			// as  properties. These would be available to the containing commands as attachment attributes.
			
			for (String key: catd.keySet() ){
				String value = catd.get(key);
				ad.put(key, value);
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
