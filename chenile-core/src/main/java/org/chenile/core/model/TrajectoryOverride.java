package org.chenile.core.model;

import org.chenile.core.service.HealthChecker;

public class TrajectoryOverride {
	private String trajectoryId; // the ID of the trajectory for which this override is applicable
	public String getTrajectoryId() {
		return trajectoryId;
	}
	public void setTrajectoryId(String trajectoryId) {
		this.trajectoryId = trajectoryId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getNewServiceReferenceId() {
		return newServiceReferenceId;
	}
	public void setNewServiceReferenceId(String newServiceReferenceId) {
		this.newServiceReferenceId = newServiceReferenceId;
	}
	public String getNewHealthCheckerReferenceId() {
		return newHealthCheckerReferenceId;
	}
	public void setNewHealthCheckerReferenceId(String newHealthCheckerReferenceId) {
		this.newHealthCheckerReferenceId = newHealthCheckerReferenceId;
	}
	public Object getNewServiceReference() {
		return newServiceReference;
	}
	public void setNewServiceReference(Object newServiceReference) {
		this.newServiceReference = newServiceReference;
	}
	public HealthChecker getNewHealthCheckerReference() {
		return newHealthCheckerReference;
	}
	public void setNewHealthCheckerReference(HealthChecker newHealthCheckerReference) {
		this.newHealthCheckerReference = newHealthCheckerReference;
	}
	private String serviceId; // the ID of the service for which this override is applicable
	private String newServiceReferenceId; // the new service reference for this combination of trajectory & service
	private String newHealthCheckerReferenceId; // the new health check reference for this combination of trajectory & service	
	private Object newServiceReference;
	private HealthChecker newHealthCheckerReference; 
}
