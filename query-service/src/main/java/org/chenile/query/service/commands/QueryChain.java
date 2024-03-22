package org.chenile.query.service.commands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.chenile.base.exception.BadRequestException;
import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.OrderedCommandDesc;
import org.chenile.query.service.commands.model.SearchContext;

public class QueryChain extends QueryPathCommand{
	
	public static final int PROPAGATING_EXCEPTION_ERRORCODE = 903;
	
	protected void validate(SearchContext context) throws Exception{
		Map<String, Object> req = obtainLastRequest(context.req);
		for(String key: req.keySet()) {
			if (!isKeyPresentInConfiguredCommands(key)){
				throw new BadRequestException(101,new Object[] {key,
						getFullyQualifiedPathAsString()});
			}
		}		
	}
	
	protected boolean isKeyPresentInConfiguredCommands(String key) {
		for(OrderedCommandDesc cmd: commandDescSet) {
			Command<?> c = cmd.getCommandDescriptor().getCommand();
			if (!(c instanceof QueryPathCommand))
				continue;
			QueryPathCommand qpc = (QueryPathCommand)c;
			String lpc = qpc.getLastPathSegment();
			if (key.equals(lpc + getRequestSuffix()))
				return true;
		}		
		return false;
	}
	
	@Override
	protected ResponseMessage handleNonErrorNumExceptions(SearchContext context, Throwable cause) {
		ResponseMessage message =  super.handleNonErrorNumExceptions(context, cause);
		message.setField(getFullyQualifiedPathAsString());
		return message;
	}

	@Override
	protected ErrorNumException generateOverallException(String message) {
		ErrorNumException e =  super.generateOverallException(message);
		e.getResponseMessage().setField(getFullyQualifiedPathAsString());
		return e;
	}

	@Override
	protected void doExecute(SearchContext context) throws Exception {
		validate(context);
		if (!isRootElement()) // for root element avoid creating a response since the context.resp already has it
			appendResponse(context.resp, new ConcurrentHashMap<String,Object>());
		super.doExecute(context); // execute commands in parallel
		doTransformation(context);
	}

	protected void doTransformation(SearchContext context) {}

}
