package org.chenile.owiz.impl.ognl;

import ognl.Ognl;

import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.impl.CommandBase;

public class SetCommand<InputType> extends CommandBase<InputType> {

	private static final String NAME = "name";
	private static final String VALUE = "value";
	

	@Override
	protected void doExecute(InputType context) throws Exception {

		String name = getConfigValue(NAME);
		String value = getConfigValue(VALUE);
		
		if (name == null ) return ;
		Ognl.setValue(ParseExpression.parseIt(name), context,value);

	}

}
