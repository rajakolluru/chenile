package org.chenile.stm.action;

import org.chenile.stm.impl.ComponentPropertiesHelper;

/**
 * Actions (both actions and transition actions) implement this interface to obtain access to custom properties that can be set.
 * @author Raja Shankar Kolluru
 *
 */
public interface ComponentPropertiesAware {
	public abstract void setComponentPropertiesHelper(ComponentPropertiesHelper cph);
	/**
	 * This property is used to enable or disable inline scripts (basically constructs of type "${xxx}somestuff" where 
	 * xxx needs to be evaluated as an expression on the passed flow context. The expression is as powerful as the 
	 * scripting strategy would allow it to be. Some scripting strategies are trivial such as bean-utils. Some of them
	 * such as OGNL (which is the default scripting strategy that is supported by STM) can be more powerful.
	 * @param enable
	 */
	public abstract void setEnableInlineScriptsInProperties(boolean enable);
}
