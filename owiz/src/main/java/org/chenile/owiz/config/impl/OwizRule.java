package org.chenile.owiz.config.impl;

import org.apache.commons.digester.Rule;
import org.chenile.owiz.config.model.FlowDescriptor;
import org.xml.sax.Attributes;

public class OwizRule<InputType> extends Rule{
	public String getKey(Attributes attributes, int i) {
		 String name = attributes.getLocalName(i);
         if ("".equals(name)) {
             name = attributes.getQName(i);
         }
         return name;
	}
	
	public String getComponentNameFromAttributes(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
            String name = getKey(attributes,i);
            if (name.equals("componentName"))
            	return attributes.getValue(i);
		}
		return null;
	}
	
	public String getXmlNameAsCamelCase(String xmlElementName) {
		if (xmlElementName == null || xmlElementName.isEmpty())
			return null;
		StringBuilder sbuf = new StringBuilder();
		boolean first = true;
		for(String s:xmlElementName.split("-")) {
			char c = s.charAt(0);
			if (first) {
				first = false;				
			}else {
				c = Character.toUpperCase(s.charAt(0));
			}
			sbuf.append(c).append(s.substring(1));
		}
		if (sbuf.isEmpty()) {
			return xmlElementName;
		}
		return sbuf.toString();
	}

	@SuppressWarnings("unchecked")
	protected  FlowDescriptor<InputType> getFlow(){
		return (FlowDescriptor<InputType>) digester.peek(XmlOrchConfigurator.FLOW_STACK);
	}
}
