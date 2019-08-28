package org.chenile.owiz.impl.ognl;

import java.util.Map;

import org.chenile.ognl.MapAccessor;
import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.DoNothing;
import org.chenile.owiz.impl.Router;

import ognl.Ognl;
import ognl.OgnlRuntime;


public class EvaluateRouter<InputType> extends Router<InputType> {

	private static final String EXPRESSION = "expression";
	
	static {
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}
	
	
	public EvaluateRouter() {
		defaultCommandDescriptor = new CommandDescriptor<InputType>();
		defaultCommandDescriptor.setCommand(new DoNothing<InputType>());
	}

	@Override
	protected String computeRoutingString(InputType context) throws Exception {
		String condition = getConfigValue(EXPRESSION);
		if (condition == null) return "false";
		Object o = Ognl.getValue(ParseExpression.parseIt(condition), context);
		if (o == null) return "false";
		return  o.toString();
	}

}
