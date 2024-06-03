package org.chenile.owiz.impl.ognl;

import java.util.Map;

import org.chenile.ognl.MapAccessor;
import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.Chain;

import ognl.Ognl;
import ognl.OgnlRuntime;

/**
 * Repeatedly, execute the attached commands till the expression specified by
 * condition evaluates to false. This command shows the power of the OWIZ XML Parser and its
 * ability to create a custom Domain Specific Language without making changes to the parser itself.
 * <p>This command has the four components of a typical 'for' loop:</p>
 * <li>Initialization - which is assumed to have happened before we get control to this command</li>
 * <li>Condition - which is an expression that is evaluated at the end of every execution </li>
 * <li>The Counter incrementation - which is performed at the end of executing all commands </li>
 * <li>The Loop body - which is all the commands contained in the for Loop</li>
 * <p>This command runs in two modes that are determined by the value of "evaluateAfterEveryCommand"</p>
 * <p>If evaluateAfterEveryCommand is true then the condition is evaluated after every command. If it is
 * condition is true then the command aborts after the command.Else it continues.</p>
 * <p>If evaluateAfterEveryCommand is false then the condition is evaluated once after all the
 * commands. If the condition is true then it repeats. </p>
 * <p>Please see the test case for a good example of this.</p>
 * @param <InputType>
 */
public class ForLoop<InputType> extends Chain<InputType> {

	private static final String CONDITION = "condition";
	private static final String INCREMENT = "increment";
	private static final String EVALUATE_AFTER_EVERY_COMMAND = "evaluateAfterEveryCommand";
	private CommandDescriptor<InputType> incrementCommand;
	static {
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}

	/**
	 * Determines if the loop should repeat. If the condition expression does not exist, it
	 * will evaluate to false.
	 * <p>The increment command (if it exists) is then executed before evaluating the condition
	 * using OGNL</p>
	 * <p>Note that if the incrementCommand does not exist, then the body of the loop
	 * must have done the incrementation (just like a normal for loop). Else there can be
	 * an infinite loop!</p>
	 * @param context the execution context
	 * @return true if loops should repeat, false otherwise.
	 * @throws Exception OGNL exception
	 */
	protected boolean shouldRepeat(InputType context) throws Exception{
		String evaluateAfterEveryCommand = getConfigValue(EVALUATE_AFTER_EVERY_COMMAND);
		if (Boolean.parseBoolean(evaluateAfterEveryCommand))
			return false;
		return isConditionTrue(context);
	}
	protected boolean isConditionTrue(InputType context) throws Exception {
		String condition = getConfigValue(CONDITION);
		if (condition == null ) return false;
		if (incrementCommand != null)
			incrementCommand.getCommand().execute(context);
		Object o = Ognl.getValue(ParseExpression.parseIt(condition), context);
		return o != null && Boolean.parseBoolean(o.toString());
	}

	@Override
	protected boolean shouldStopChain(InputType context) throws Exception {
		String evaluateAfterEveryCommand = getConfigValue(EVALUATE_AFTER_EVERY_COMMAND);
		if (!Boolean.parseBoolean(evaluateAfterEveryCommand))
			return super.shouldStopChain(context);
		boolean b = isConditionTrue(context);
		return b;
	}

	@Override
	protected void doExecute(InputType context) throws Exception {
		do{
			super.doExecute(context);
		}while(shouldRepeat(context));
	}

	@Override
	public void attachCommand(AttachmentDescriptor<InputType> attachmentDescriptor,
			CommandDescriptor<InputType> command) {
		String cc = attachmentDescriptor.get(INCREMENT) ;
		if (Boolean.parseBoolean(cc) || command.getId().startsWith(INCREMENT)){
			incrementCommand = command;
		}
		else
			super.attachCommand(attachmentDescriptor, command);
	}
}
