package org.chenile.owiz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.owiz.BypassableCommand;
import org.chenile.owiz.Command;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author Raja Shankar Kolluru
 *
 * @param <InputType>
 * <br/>
 * Extends normal chain functionality and handles parallel processing.
 * Individual commands are assumed to be thread safe i.e. they mutate context (the InputType)
 * in different threads in a concurrent way.
 * It is also assumed that the context is WarningAware so that all the errors are captured 
 * as warnings.
 * Exception processing:<br/><ol>
 * <li>Commands are expected to throw ErrorNumException in case of errors. These errors are
 * logged in the context as warnings</li>
 * <li>Framework throws ErrorNumException (error code 903). This error will not be logged 
 * as a warning (because the sub command has already been logged and this will be redundant)</li>
 * </ol>
 */
public class ParallelChain<InputType> extends Chain<InputType> {

	private ExecutorService executorService;
	private long timeoutInMillis;
	
	public void setTimeoutInMillis(long timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis;
	}

	public ParallelChain() {
		this.timeoutInMillis = 20000L;
	}
	
	public ParallelChain(ExecutorService service, int timeoutInMillis) {
		this.executorService = service;
		this.timeoutInMillis = timeoutInMillis;
	}
	
	public void setExecutorService(ExecutorService service) {
		this.executorService = service;
	}
	@Override
	protected void doExecute(InputType context) throws Exception {
		if (executorService == null ) {
			throw new ServerException(902,new Object[] {getId()});
		}
		List<ChainExecutor<InputType>> tasks = new ArrayList<>();
		for (Command<InputType> cmd: obtainExecutionCommands(context)){
			if (!commandCanBeBypassed(cmd,context)) {
				tasks.add(new ChainExecutor<InputType>(cmd,context));
			}
		}
		List<Future<InputType>> futs = null;
		try {
			futs = executorService.invokeAll(tasks, timeoutInMillis, TimeUnit.MILLISECONDS);
		}catch(InterruptedException ee) {
			throw new ErrorNumException(500,905,"Timed out waiting for downstream systems.");
		}
		int errCount = 0;
		for (Future<InputType> f: futs) {
			try{
				f.get(); // check for error
			}catch(ExecutionException | InterruptedException ee) {	
				errCount++;				
				Throwable cause = ee.getCause();
				handleErrors(context,cause);
			}
		}
		if (errCount == futs.size()) {
			throw generateOverallException("All commands have failed");
		}
	}
	

	@SuppressWarnings("unchecked")
	protected boolean commandCanBeBypassed(Command<InputType> cmd, InputType context) {
		return (cmd instanceof BypassableCommand) &&
				((BypassableCommand<InputType>) cmd).bypass(context);
	}

	protected ErrorNumException generateOverallException(String message) {
		return new ErrorNumException(500,903,message);
	}
	
	protected void handleErrors(InputType context, Throwable cause) {
		ResponseMessage mesg;
		if (cause instanceof ErrorNumException) {
			mesg = ((ErrorNumException)cause).getResponseMessage();
		}else {
			mesg = handleNonErrorNumExceptions(context,cause);
		}
		// Ignore 903 sub error code since these messages were generated 
		// when the framework realizes that all its sub commands have errored out
		// Since sub commands have already logged warning it is redundant to re-log them again
		if (mesg.getSubErrorCode() != 903)
			WarningAware.addWarningMessage(context,mesg);		
	}
	
	protected ResponseMessage handleNonErrorNumExceptions(InputType context, Throwable cause) {
		ResponseMessage mesg = new ResponseMessage();	
		mesg.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
		mesg.setSubErrorCode(904);
		mesg.setSeverity(ErrorType.ERROR);
		mesg.setDescription(cause.getMessage());	
		return mesg;
	}
	
	protected static class ChainExecutor<InputType> implements Callable<InputType>{
		private Command<InputType> command;
		private InputType context;
		public ChainExecutor(Command<InputType> command, InputType context) {
			this.command = command;
			this.context = context;
		}
		
		@Override
		public InputType call() throws Exception {			
			command.execute(context);			
			return context;
		}		
	}

}
