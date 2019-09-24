package org.chenile.scheduler.util;

import org.quartz.JobExecutionContext;

public class SchedulerUtil {
	public static Object getJobParamValue(JobExecutionContext context, String key) {
		return context.getMergedJobDataMap().get(key);		
	}
}
