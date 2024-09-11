package org.chenile.stm.dummy;

import org.chenile.stm.impl.ScriptingStrategy;

public class DummyScriptingStrategy implements ScriptingStrategy {

    @Override
    public Object executeGenericScript(String script, Object context) throws Exception {
        return null;
    }

    @Override
    public boolean executeBooleanExpression(String script, Object context) throws Exception {
        return false;
    }
}