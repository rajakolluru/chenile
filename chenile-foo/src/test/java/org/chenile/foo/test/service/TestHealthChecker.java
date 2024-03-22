package org.chenile.foo.test.service;

import org.chenile.core.service.HealthCheckInfo;
import org.chenile.core.service.HealthChecker;

public class TestHealthChecker implements HealthChecker {

	@Override
	public HealthCheckInfo healthCheck() {
		HealthCheckInfo hci = new HealthCheckInfo();
		hci.healthy = true;
		hci.message = "SUCCESS";
		return hci;
	}

}
