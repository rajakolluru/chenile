package org.chenile.owiz.impl.ognl;

import ognl.Ognl;

import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.impl.CommandBase;

public abstract class OgnlGetCommand<InputType> extends CommandBase<InputType> {

	protected static final String EXPRESSION = "expression";
	

	protected String getExpression(InputType context) throws Exception {

		String expression = getConfigValue(EXPRESSION);
		
		if (expression == null ) return null;
		Object o = Ognl.getValue(ParseExpression.parseIt(expression), context);
		return (o == null)? null: o.toString();
	}

}
