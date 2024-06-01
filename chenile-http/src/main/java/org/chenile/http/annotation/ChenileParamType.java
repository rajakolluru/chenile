package org.chenile.http.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The ChenileParamType is required when the method signature of a service does
 * not match the method signature of the controller. Chenile would not be able to determine
 * the method of the service to call by reflection. The @ChenileParamType helps out by
 * stating the correct param type of the actual service.
 * <pre>{ @Code
 *  public class ServiceImpl {
 *      public String foo(Object x){}
 *  } needs to be invoked by a controller which is defined here:
 *
 *  public class Controller {
 *      public String Object(@ChenileParamType(Object.class) SomeClass x){
 *      }
 *  }
 * }</pre>
 */
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ChenileParamType {
	public Class<?>  value();
}
