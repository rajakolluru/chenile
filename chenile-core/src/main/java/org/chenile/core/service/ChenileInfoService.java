package org.chenile.core.service;

import org.chenile.core.model.ChenileServiceDefinition;

public interface ChenileInfoService {
	/**
	 * 
	 * @return the version of the current deployable along with service information for all 
	 * services deployed within the deployable (including this service)
	 */
	public Info info();
	/**
	 * Calls the health check information service attached to the particular service and gets them to perform a health check
	 * In case the service does not have a health check configured it throws a 404
	 * @return the value returned by the health check service
	 */
	public HealthCheckInfo healthCheck(String currTrajectory,String service);
	/**
	 * 
	 * @param service
	 * @return the full information for a particular service. 
	 */
	public ChenileServiceDefinition serviceInfo(String service);
}
