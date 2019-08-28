package org.chenile.stm.model;

/**
 * This object stores the various tags that are supported by the XML configurator along
 * with their associated properties. 
 * @author Raja Shankar Kolluru
 *
 */
public class StateTagDescriptor {
	/**
	 * Name of the tag
	 */
	private String tag;
	/**
	 * The component name that needs to be instantiated when this tag is encountered.
	 */
	private String componentName;
	/**
	 * Is this tag representing a manual state? (or a view state) 
	 */
	private boolean manualState;
	/**
	 * The descriptor class for this tag. It should extend StateDescriptor.
	 */
	private Class<? extends StateDescriptor> descriptorClass;
	
	public Class<? extends StateDescriptor> getDescriptorClass() {
		return descriptorClass;
	}
	public void setDescriptorClass(Class<? extends StateDescriptor> descriptorClass) {
		this.descriptorClass = descriptorClass;
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
	public void setManualState(boolean manualState) {
		this.manualState = manualState;
	}
	public boolean isManualState() {
		return manualState;
	}
	
	
	
}
