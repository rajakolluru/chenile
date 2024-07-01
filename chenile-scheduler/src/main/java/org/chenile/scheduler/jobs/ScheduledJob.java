package org.chenile.scheduler.jobs;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.scheduler.Constants;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledJob implements Job {

	
	public ScheduledJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ChenileExchange exchange = new ChenileExchange() ;
		exchange.setHeader(HeaderUtils.ENTRY_POINT, Constants.SCHEDULER_ENTRY_POINT);
		exchange.setServiceDefinition(getServiceDefinition(context));
		exchange.setOperationDefinition(getOperationDefinition(context));
		copyHeaders(exchange,context);
		exchange.setBody(getFromMap(Constants.BODY,context)); // just in case someone wants to use it
		getChenileEntryPoint(context).execute(exchange); 
	}
	
	
	private void copyHeaders(ChenileExchange exchange, JobExecutionContext context) {
		if (context.getMergedJobDataMap() == null) return;
		context.getMergedJobDataMap().forEach(exchange::setHeader);
	}

	private ChenileServiceDefinition getServiceDefinition(JobExecutionContext context) {
		return getFromMap(Constants.SERVICE_DEFINITION,context);
	}
	
	private OperationDefinition getOperationDefinition(JobExecutionContext context) {
		return getFromMap(Constants.OPERATION_DEFINITION,context);
	}
	
	private ChenileEntryPoint getChenileEntryPoint(JobExecutionContext context) {
		return getFromMap(Constants.CHENILE_ENTRY_POINT,context);
	}
	
	@SuppressWarnings({"unchecked" })
	private <T> T getFromMap(String key, JobExecutionContext context) {
		return (T) context.getMergedJobDataMap().get(key);
	}

}
