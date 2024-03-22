package org.chenile.stm.action;

import org.chenile.stm.impl.ScriptingStrategy;

/**
 * Actions implement this interface to get hold of a scripting strategy that is set by the STM framework.
 * @author Raja Shankar Kolluru
 *
 */
public interface ScriptingStrategyAware {
	public abstract void setScriptingStrategy(ScriptingStrategy ss);
}
