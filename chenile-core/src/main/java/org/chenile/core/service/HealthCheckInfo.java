package org.chenile.core.service;

/**
 * A base class that provides basic health check info about all services.
 * This can be extended to provide more information as needed.
 */
public class HealthCheckInfo {
	public boolean healthy;
	public String message;
	public int statusCode;
}
