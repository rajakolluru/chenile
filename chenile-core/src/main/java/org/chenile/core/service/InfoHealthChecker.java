package org.chenile.core.service;

/**
 * Health checker for the ChenileInfoService
 */
public class InfoHealthChecker implements HealthChecker{

	@Override
	public HealthCheckInfo healthCheck() {
		HealthCheckInfo hci = new HealthCheckInfo();
		hci.healthy = true;
		hci.message = "Info service is functioning normally.";
		return hci;
	}
}
