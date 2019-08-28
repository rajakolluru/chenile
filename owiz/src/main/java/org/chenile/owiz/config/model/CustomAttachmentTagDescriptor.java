package org.chenile.owiz.config.model;

import java.util.HashMap;

/**
 * This object stores the various tags that are supported by the XML configurator along
 * with their associated properties. 
 * Facilitate the usage of custom tags.
 * @author Raja Shankar Kolluru
 *
 */
public class CustomAttachmentTagDescriptor extends HashMap<String,String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6234201363373868501L;
	private final static String TAGATTRIBUTE = "tag";
	
	/**
	 * Name of the tag
	 */
	private String tag;
	
	public CustomAttachmentTagDescriptor(String tag) {
		super();
		this.tag = tag;
	}
	public CustomAttachmentTagDescriptor() {
		super();
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
	@Override
	public String put(String key, String value) {
		if (key != null && TAGATTRIBUTE.equalsIgnoreCase(key)){
			setTag(value);
			return value;
		}
		return super.put(key, value);
	}
	@Override
	public String toString() {
		return "CustomAttachmentTagDescriptor [tag=" + tag + "]";
	}

	
	
}
