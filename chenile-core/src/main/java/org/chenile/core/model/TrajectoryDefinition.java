package org.chenile.core.model;

import java.util.HashMap;
import java.util.Map;

import org.chenile.core.service.HealthChecker;

/**
 * The trajectory definition details. 
 * Trajectories can dictate that the user be directed to new services or health checks.
 * @author r0k02sw
 *
 */
public class TrajectoryDefinition {

	private String id;
	/** 
	 * a map between the service name and the service reference to use for this trajectory.
	 * This effects only those services that are configured. Other services will stay unaffected.
	 */
	//private Map<String,String> serviceToServiceReferenceId = new HashMap<>();
	//private Map<String,Object> serviceToServiceReference = new HashMap<>();
	private Map<String,TrajectoryOverride> trajectoryOverrides = new HashMap<>();

	public void setTrajectoryOverrides(Map<String, TrajectoryOverride> trajectoryOverrides) {
		this.trajectoryOverrides = trajectoryOverrides;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}
	
	public void merge(TrajectoryDefinition trajectoryDefinition) {
		trajectoryOverrides.putAll(trajectoryDefinition.getTrajectoryOverrides());	
	}
	
	public Object getServiceReference(String serviceId) {
		TrajectoryOverride to = trajectoryOverrides.get(serviceId);
		if (to == null) return null;
		return to.getNewServiceReference();
	}
	
	public HealthChecker getHealthCheckerReference(String serviceId) {
		TrajectoryOverride to = trajectoryOverrides.get(serviceId);
		if (to == null) return null;
		return to.getNewHealthCheckerReference();
	}
	
	public String getServiceReferenceId(String serviceId) {
		TrajectoryOverride to = trajectoryOverrides.get(serviceId);
		if (to == null) return null;
		return to.getNewServiceReferenceId();
	}
	
	public String getHealthCheckerReferenceId(String serviceId) {
		TrajectoryOverride to = trajectoryOverrides.get(serviceId);
		if (to == null) return null;
		return to.getNewHealthCheckerReferenceId();
	}

	public Map<String,TrajectoryOverride> getTrajectoryOverrides() {
		return trajectoryOverrides;
	}

}
