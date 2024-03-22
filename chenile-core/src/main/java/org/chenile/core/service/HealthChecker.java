package org.chenile.core.service;

/**
 * All Health Checkers must implement this interface 
 * Health Checker can be attached to any service
 * Health checkers will be exposed via a GET method
 * @author r0k02sw
 *
 */
public interface HealthChecker {
	public HealthCheckInfo healthCheck();
}
