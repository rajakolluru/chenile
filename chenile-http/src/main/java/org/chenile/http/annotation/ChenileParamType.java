package org.chenile.http.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The ChenileParamType is required when the method signature of a service does
 * not match the method signature of the controller. Chenile would not be able to determine
 * the method of the service to call by reflection. The @ChenileParamType helps out by
 * stating the correct param type of the actual service.<br/>
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
 * <p>This situation can happen when the actual type in the Controller is a String whereas the param type
 * expected is an Object (or some other base class). The actual type is determined by the
 * {@link BodyTypeSelector} during runtime</p>
 * <p>Alternatively, there may be a situation when the same service class is reused multiple times. See chenile-workflow
 * for an example. In this situation as well, the class will be of a more specific type in the
 * Controller whilst it will be more generic in the Service class</p>
 */
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ChenileParamType {
	public Class<?>  value();
}
