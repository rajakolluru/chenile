package org.chenile.foo.test.service;

import org.chenile.core.service.HealthCheckInfo;
import org.chenile.core.service.HealthChecker;

public class TestHealthChecker1 implements HealthChecker {

	@Override
	public HealthCheckInfo healthCheck() {
		HealthCheckInfo hci = new HealthCheckInfo();
		hci.healthy = true;
		hci.message = "SUCCESS1";
		return hci;
	}

}