package org.chenile.core.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;

/**
 * This annotation will need to be used at the spring {@link Configuration} class
 * This annotation does the same thing as {@link ConditionalOnTrajectory} 
 * However it does this behaviour for health checkers rather than the services
 * You can then use this annotation to accomplish this. 
 * <pre> {@code :
 *  // original health checker that needs to be replaced for specific trajectories
 *  @Bean public HealthChecker testHealthChecker(){ return new TestHealthChecker(); }
 *  // replace with this new health checker only for trajectory id "t1"
 *  @Bean @ConditionalHealthCheckOnTrajectory(id = "t1", service = "testService")
 *  public HealthChecker testHealthChecker1(){ return new TestHealthChecker1();}
 * }</pre>
 * The code above replaces all instances of TestHealthChecker with TestHealthChecker1 but will do so only
 * for trajectory ID "t1" 
 * @author Raja Shankar Kolluru
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConditionalHealthCheckOnTrajectory {
	/**
	 * The trajectory ID for which the reference is replaced
	 * @return
	 */
	public String id();
	/**
	 * The service name that is being replaced with this new health checker
	 * @return
	 */
	public String service();
}
