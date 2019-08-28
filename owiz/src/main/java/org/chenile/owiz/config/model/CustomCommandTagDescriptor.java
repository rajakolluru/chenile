package org.chenile.owiz.config.model;

import java.util.HashMap;

/**
 * This object stores the various tags that are supported by the XML configurator along
 * with their associated properties. 
 * Facilitate the usage of custom tags.
 * @author Raja Shankar Kolluru
 *
 */
public class CustomCommandTagDescriptor extends HashMap<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2799520320402297127L;
	
	private static final String TAGATTRIBUTE = "tag";
	private static final String COMPONENT_NAME_ATTRIBUTE = "componentName";
	private static final String DEFAULT_ATTACHMENT_TAG = "defaultAttachmentTag";
	/**
	 * Name of the tag
	 */
	private String tag;
	
	public CustomCommandTagDescriptor(String tag) {
		super();
		this.tag = tag;
	}
	public CustomCommandTagDescriptor() {
		super();
	}
	/**
	 * The component name that needs to be instantiated when this tag is encountered.
	 */
	private String componentName;
	private String defaultAttachmentTag;
	
	
	public String getDefaultAttachmentTag() {
		return defaultAttachmentTag;
	}
	public void setDefaultAttachmentTag(String defaultAttachmentTag) {
		this.defaultAttachmentTag = defaultAttachmentTag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getComponentName() {
		return componentName;
	}
	
	@Override
	public String put(String key, String value) {
		
		if (TAGATTRIBUTE.equalsIgnoreCase(key)){
			setTag(value);
			return value;
		}	
		else if (COMPONENT_NAME_ATTRIBUTE.equalsIgnoreCase(key)){
			setComponentName(value);
			return value;
		}
		else if (DEFAULT_ATTACHMENT_TAG.equalsIgnoreCase(key)){
			setDefaultAttachmentTag(value);
			return value;
		}
		return super.put(key, value);
	}
	@Override
	public String toString() {
		return "CustomCommandTagDescriptor [tag=" + tag + ", componentName="
				+ componentName + "]";
	}
	
	
	
	
}
