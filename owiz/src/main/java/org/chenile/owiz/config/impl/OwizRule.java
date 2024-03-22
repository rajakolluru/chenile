package org.chenile.owiz.config.impl;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class OwizRule extends Rule{
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
		if (xmlElementName == null || xmlElementName.length() == 0)
			return null;
		StringBuffer sbuf = new StringBuffer();
		boolean first = true;
		for(String s:xmlElementName.split("-")) {
			char c = s.charAt(0);
			if (first) {
				first = false;				
			}else {
				c = Character.toUpperCase(s.charAt(0));
			}
			sbuf.append(c + s.substring(1));
		}
		if (sbuf.length() == 0) {
			return xmlElementName;
		}
		return sbuf.toString();
	}
}
