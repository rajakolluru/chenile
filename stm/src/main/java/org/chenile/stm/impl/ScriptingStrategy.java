package org.chenile.stm.impl;

public interface ScriptingStrategy {
	public abstract Object executeGenericScript(String script, Object context) throws Exception;
	public abstract boolean executeBooleanExpression(String script, Object context) throws Exception;
}
