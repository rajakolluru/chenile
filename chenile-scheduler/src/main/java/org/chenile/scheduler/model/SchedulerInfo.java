package org.chenile.scheduler.model;

import java.util.Map;

/**
 * Schedule definition.
 *
 */
public class SchedulerInfo {
	public static final String EXTENSION = "chenile.scheduler";
	public String serviceName;
	public String operationName;
	public String cronSchedule;
	public String jobName;
	public String jobDescription;
	public String triggerName;
	private String triggerGroup;
	
	public Map<String,Object> headers;
	public String payload;
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}



	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public Map<String,Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String,Object> headers) {
		this.headers = headers;
	}
}
