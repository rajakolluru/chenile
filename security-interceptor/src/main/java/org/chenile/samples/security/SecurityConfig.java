package org.chenile.samples.security;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.chenile.core.annotation.ChenileAnnotation;

/**
 * This provides the authorities that are required to access this service.
 * The value() of the class is the enum which specifies if the given resource is PROTECTED or UNPROTECTED.
 * If the resource is UNPROTECTED, the security interceptor does not enforce authentication checks. <br/>
 * Either the authorities or authoritiesSupplier need to be specified. If both are not specified
 * then the authorization for this resource is not checked. Only the authentication check will be
 * enforced. <br/>
 * The authoritiesSupplier must implement the {@link AuthoritiesSupplier} interface
 * If both of them don't exist users will be able to access the service without any restrictions
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
@ChenileAnnotation
public @interface SecurityConfig {
	public static enum ProtectionStatus {UNPROTECTED, PROTECTED};
	public ProtectionStatus value() default ProtectionStatus.PROTECTED;
	public String[] authorities() default {};
	public String authoritiesSupplier() default "";
}
