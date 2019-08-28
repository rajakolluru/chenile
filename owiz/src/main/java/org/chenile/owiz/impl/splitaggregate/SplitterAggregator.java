package org.chenile.owiz.impl.splitaggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.CommandBase;


public class SplitterAggregator extends CommandBase<SplitterContext> implements  AttachableCommand<SplitterContext>{
	   
	private CommandDescriptor<?> command;
	private ExecutorService executorService;
	private int timeoutInMilliSeconds = -1;
	public static final String TIMEOUT_ERR = "TIMEOUTERR";
	
	public SplitterAggregator(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public SplitterAggregator(ExecutorService executorService, int timeoutInMilliSeconds) {
		this.executorService = executorService;
		this.timeoutInMilliSeconds = timeoutInMilliSeconds;
	}
	
	public void attachCommand(
			AttachmentDescriptor<SplitterContext> attachmentDescriptor,
			CommandDescriptor<SplitterContext> command) {
		if (this.command != null)
			throw new RuntimeException("Command is duplicated for splitter aggregator id = " + commandDescriptor.getId());
		this.command = command;	
	}
	
	protected void doExecute(SplitterContext splitContext) throws Exception {	
		List<Callable<IndividualSplitContext>> tasks = new ArrayList<Callable<IndividualSplitContext>>();
		for ( IndividualSplitContext item: splitContext.obtainList()){
			tasks.add(new CallableAdapter(item));
		}
		List<Future<IndividualSplitContext>> futures;
		
		//System.out.println("Current time pre  : " + System.currentTimeMillis());
		if (timeoutInMilliSeconds == -1)
			futures = executorService.invokeAll(tasks);
		else
			futures = executorService.invokeAll(tasks,timeoutInMilliSeconds, TimeUnit.MILLISECONDS);
		
		//System.out.println("Current time post : " + System.currentTimeMillis());
		for (int i = 0; i < futures.size();i++) {
			Future<IndividualSplitContext> f = futures.get(i);
			CallableAdapter task = (CallableAdapter)tasks.get(i);
			if (f.isCancelled()){
				splitContext.addErrorToAggregate(task.getRequest(), TIMEOUT_ERR);
			}else{
				try{
					splitContext.addToAggregate(f.get());
				}catch(Throwable t){
					splitContext.addErrorToAggregate(task.getRequest(),t);
				}
			}
		}
	}
		
	/**
	 * An adapter between {@link Command} and the {@link Callable} interface mandated by
	 * the methods of java.util.concurrent.
	 * 
	 * @author raja.kolluru
	 * 
	 * @param <Request>
	 * @param <Response>
	 */
	private class CallableAdapter implements Callable<IndividualSplitContext> {
		private IndividualSplitContext req;

		public CallableAdapter(IndividualSplitContext req) {
			this.req = req;
		}
		
		public IndividualSplitContext getRequest(){
			return req;
		}

		public IndividualSplitContext call() throws Exception {
			@SuppressWarnings("unchecked")
			Command<Object> cmd = (Command<Object>) command.getCommand();
			cmd.execute(req);
			return req;
		}
	}	
}