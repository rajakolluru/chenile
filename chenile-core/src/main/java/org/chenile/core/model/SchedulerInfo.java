package org.chenile.core.model;

import java.util.Map;

public class SchedulerInfo {
	private String cronSchedule;
	private String triggerName;
	private String jobName;
	
	private Map<String,String> jobMetadata;
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

	private String jobDescription;
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

	private String triggerGroup;

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public Map<String,String> getJobMetadata() {
		return jobMetadata;
	}

	public void setJobMetadata(Map<String,String> jobMetadata) {
		this.jobMetadata = jobMetadata;
	}
}
