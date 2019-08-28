package org.chenile.owiz.impl.splitaggregate;

import java.util.concurrent.TimeUnit;

import org.chenile.owiz.Command;

public class MockSplitCommand implements Command<MockIndividualSplitContext>{

	public static final String OOOPS_SOMETHING_WENT_WRONG = "Ooops something went wrong";
	public static final String RETURN = "return";
	
	public void execute(MockIndividualSplitContext context) throws Exception {
		
		if (context.isMustWait()){
			context.getLatch().await(context.getTimeOutInMilliSeconds(),TimeUnit.MILLISECONDS);
		}
		if (context.isMustThrowException())
			throw new RuntimeException(MockSplitCommand.OOOPS_SOMETHING_WENT_WRONG);
		context.setValue(context.getKey() + MockSplitCommand.RETURN);
	}
 
}