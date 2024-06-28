package org.chenile.scheduler.jobs;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.scheduler.init.SchedulerBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledJob implements Job {

	
	public ScheduledJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ChenileExchange exchange = new ChenileExchange() ;
		exchange.setServiceDefinition(getServiceDefinition(context));
		exchange.setOperationDefinition(getOperationDefinition(context));
		// This enables methods to accept job map keys as method params
		// See the test case sch method for an example
		copyHeaders(exchange,context);
		exchange.setBody(getFromMap(SchedulerBuilder.BODY,context)); // just in case someone wants to use it
		getChenileEntryPoint(context).execute(exchange); 
	}
	
	
	private void copyHeaders(ChenileExchange exchange, JobExecutionContext context) {
		if (context.getMergedJobDataMap() == null) return;
		context.getMergedJobDataMap().forEach((k,v)-> {
			exchange.setHeader(k, v);
		});
		
	}

	private ChenileServiceDefinition getServiceDefinition(JobExecutionContext context) {
		return getFromMap(SchedulerBuilder.SERVICE_DEFINITION,context);
	}
	
	private OperationDefinition getOperationDefinition(JobExecutionContext context) {
		return getFromMap(SchedulerBuilder.OPERATION_DEFINITION,context);
	}
	
	private ChenileEntryPoint getChenileEntryPoint(JobExecutionContext context) {
		return getFromMap(SchedulerBuilder.CHENILE_ENTRY_POINT,context);
	}
	
	@SuppressWarnings({"unchecked" })
	private <T> T getFromMap(String key, JobExecutionContext context) {
		return (T) context.getMergedJobDataMap().get(key);
	}

}
