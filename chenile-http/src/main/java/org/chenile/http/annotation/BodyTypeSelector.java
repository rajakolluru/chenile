package org.chenile.http.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation specifies that this particular method needs a body type selector for
 * determining the type that the body needs to convert to. Payload bodies are usually fixed.
 * However, for some generic interfaces payload body could only be determined by knowing
 * the full context of the request. We may have to examine headers, type of object that will
 * be updated etc.
 * <p>E.g., Let us say we have two subclasses of the Employee class called ContractEmployee and
 * PayrollEmployee. Consider a method that needs to update Employee information such as
 * <pre>{@code
 *   public Employee save(Employee employee);
 * }</pre></p>
 * <p></p>The type of employee that needs to be passed depends on the context. This context can only be
 * determined by examining the request context. For example, the request may have a header which
 * specifies the type of employee who needs to be "saved". The bodyTypeSelector helps in determining
 * the specific type of Employee by examining the context of the request. It has the full context of
 * the request since it is passed a {@link org.chenile.core.context.ChenileExchange}
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface BodyTypeSelector {
	public String value();
}
