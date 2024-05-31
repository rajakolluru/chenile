package org.chenile.core.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation helps to replace service references with new ones. 
 * Let us say, we have a service reference in spring defined as "testService"
 * We want to replace this service reference with "testService1" but only if the trajectory id = "t1"
 * You can then use this annotation to accomplish this. 
 * <pre>{@code
 *  // original service that needs to be replaced.
 *  @Bean public TestService testService(){ return new TestServiceImpl(); }
 *  // replace with this new service only for trajectory id "t1"
 *  @Bean @ConditionalOnTrajectory(id = "t1", service = "testService")
 *  public TestService testService1(){ return new TestServiceImpl1();}
 * }<pre>
 * The code above replaces all instances of TestServiceImpl with TestServiceImpl1 but will do so only
 * for trajectory ID "t1" 
 * @author Raja Shankar Kolluru
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConditionalOnTrajectory {
	/**
	 * The trajectory ID for which the reference is replaced
	 * @return
	 */
	public String id();
	/**
	 * The service name that is being replaced with this new service
	 * @return
	 */
	public String service();
}
