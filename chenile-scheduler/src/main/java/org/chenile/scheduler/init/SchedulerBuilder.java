package org.chenile.scheduler.init;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.chenile.base.exception.ServerException;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.SchedulerInfo;
import org.chenile.scheduler.jobs.ScheduledJob;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


/**
 * Set up the scheduler for the relevant services if applicable
 * @author Raja Shankar Kolluru
 *
 */
public class SchedulerBuilder implements DisposableBean{
	
	public static final String SERVICE_DEFINITION = "service.definition";
	public static final String OPERATION_DEFINITION = "operation.definition";
	public static final String CHENILE_ENTRY_POINT = "chenile.entry.point";
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired
	private Scheduler quartzScheduler;
	@Autowired
	private ChenileEntryPoint chenileEntryPoint;
	
	public SchedulerBuilder() {
		super();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void build() throws Exception {
		chenileConfiguration.getServices().forEach((name,sd)->{
			sd.getOperations().forEach((od)->{
					try {
						scheduleJob(sd,od);
					} catch (SchedulerException e) {
						throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), 
								"Cannot schedule job for " + sd.getId() + "." + od.getName(),e);
					}
			});
		});
		quartzScheduler.start();
	}
	
	private void scheduleJob(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition)
					throws SchedulerException{
		if (operationDefinition.getSchedulerInfo() == null) return;
		SchedulerInfo sinfo = operationDefinition.getSchedulerInfo();
		
		JobDataMap jdm = new JobDataMap();
		jdm.put(SERVICE_DEFINITION, serviceDefinition);
		jdm.put(OPERATION_DEFINITION, operationDefinition);
		jdm.put(CHENILE_ENTRY_POINT, chenileEntryPoint);
		
		JobDetail job = newJob(ScheduledJob.class)
	             .withIdentity(sinfo.getJobName())
	             .setJobData(jdm)
	             .withDescription(sinfo.getJobDescription())
	             .build();
		CronTrigger trigger = newTrigger()
			    .withIdentity(sinfo.getTriggerName(), sinfo.getTriggerGroup())
			    .withSchedule(cronSchedule(sinfo.getCronSchedule()))
			    .build();
		quartzScheduler.scheduleJob(
				job,
				trigger);
	}

	@Override
	public void destroy() throws Exception {
		if(quartzScheduler.isStarted())
			quartzScheduler.shutdown(true); // wait for jobs to complete		
	}

}
