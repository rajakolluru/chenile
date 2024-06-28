package org.chenile.scheduler.init;

import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.SubscriberVO;
import org.chenile.scheduler.jobs.ScheduledJob;
import org.chenile.scheduler.model.SchedulerInfo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * Set up the scheduler for the relevant services if applicable
 * @author Raja Shankar Kolluru
 *
 */
public class SchedulerBuilder implements DisposableBean{
	
	public static final String SERVICE_DEFINITION = "service.definition";
	public static final String OPERATION_DEFINITION = "operation.definition";
	public static final String CHENILE_ENTRY_POINT = "chenile.entry.point";
	public static final String BODY = "body";
	public static Logger logger = LoggerFactory.getLogger(SchedulerBuilder.class);
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired
	private Scheduler quartzScheduler;
	@Autowired
	private ChenileEntryPoint chenileEntryPoint;
	@Autowired
	private ChenileExchangeBuilder chenileExchangeBuilder;
	
	public SchedulerBuilder() {
		super();
	}

	@SuppressWarnings("unchecked")
	@EventListener(ApplicationReadyEvent.class)
	public void build() throws Exception {
		Map<String, SchedulerInfo> map = (Map<String,SchedulerInfo>)
				chenileConfiguration.getOtherExtensions().get(SchedulerInfo.EXTENSION);

		map.forEach((name,schedulerInfo)->{
					try{
						scheduleJob(schedulerInfo);
					} catch (SchedulerException e) {
						logger.error("Cannot schedule job {}. Error = {}", schedulerInfo.jobName,
								e.getMessage());
					}
			});
		quartzScheduler.start();
	}
	
	public void scheduleJob(SchedulerInfo schedulerInfo) throws
	  SchedulerException {
		SubscriberVO subscriberVo = chenileExchangeBuilder.makeSubscriberVO(schedulerInfo.serviceName,
				schedulerInfo.operationName);
		scheduleJob(subscriberVo.serviceDefinition, subscriberVo.operationDefinition,schedulerInfo);
	}
	

	public void scheduleJob(ChenileServiceDefinition serviceDefinition, 
			OperationDefinition operationDefinition,SchedulerInfo schedulerInfo) throws SchedulerException {

		JobDataMap jdm = new JobDataMap();
		jdm.put(SERVICE_DEFINITION, serviceDefinition);
		jdm.put(OPERATION_DEFINITION, operationDefinition);
		jdm.put(CHENILE_ENTRY_POINT, chenileEntryPoint);
		if (schedulerInfo.getHeaders() != null) {
			schedulerInfo.getHeaders().forEach((k, v)->{
				jdm.put(k,v);
			});
		}
		jdm.put(BODY,schedulerInfo.payload);
		JobDetail job = newJob(ScheduledJob.class)
	             .withIdentity(schedulerInfo.getJobName())
	             .setJobData(jdm)
	             .withDescription(schedulerInfo.getJobDescription())
	             .build();
		CronTrigger trigger = newTrigger()
			    .withIdentity(schedulerInfo.getTriggerName(), schedulerInfo.getTriggerGroup())
			    .withSchedule(cronSchedule(schedulerInfo.getCronSchedule()))
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
