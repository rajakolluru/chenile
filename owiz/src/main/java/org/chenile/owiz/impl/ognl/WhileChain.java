package org.chenile.owiz.impl.ognl;

import java.util.Map;

import org.chenile.ognl.MapAccessor;
import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.Chain;

import ognl.Ognl;
import ognl.OgnlRuntime;


public class WhileChain<InputType> extends Chain<InputType> {

	private static final String CONDITION = "condition";
	private static final String CONDITION_COMMAND = "conditionCommand"; 
	private CommandDescriptor<InputType> conditionCommand ;
	

	static {
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}
	
	@Override
	protected boolean shouldStopChain(InputType context) throws Exception {
		String condition = getConfigValue(CONDITION);
		if (condition == null) return false;
		
		if (conditionCommand != null) {
			conditionCommand.getCommand().execute(context);
		}
			
		Object o = Ognl.getValue(ParseExpression.parseIt(condition), context);
		return (o != null)?  Boolean.parseBoolean(o.toString()) : false;
	}

	@Override
	public void attachCommand(AttachmentDescriptor<InputType> attachmentDescriptor,
			CommandDescriptor<InputType> command) {
		String cc = attachmentDescriptor.get(CONDITION_COMMAND) ;
		if (cc != null && Boolean.parseBoolean(cc)){
			conditionCommand = command;
		}
		else
			super.attachCommand(attachmentDescriptor, command);
		
	}

	
}
