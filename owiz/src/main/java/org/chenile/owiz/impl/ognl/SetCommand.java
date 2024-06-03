package org.chenile.owiz.impl.ognl;

import ognl.Ognl;

import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.impl.CommandBase;

/**
 * Sets specific parts of the context to a value.
 * The value can be a constant, or it can be computed from the context as well
 * @param <InputType>
 */
public class SetCommand<InputType> extends CommandBase<InputType> {

	private static final String NAME = "name";
	private static final String VALUE = "value";
	

	@Override
	protected void doExecute(InputType context) throws Exception {
		String name = getConfigValue(NAME);
		String value = getConfigValue(VALUE);
		Object oval = Ognl.getValue(ParseExpression.parseIt(value),context);
		if (name == null ) return ;
		Ognl.setValue(ParseExpression.parseIt(name), context,oval);
	}
}
