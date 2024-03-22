package org.chenile.http.init.od;

import java.lang.reflect.Method;

import org.chenile.core.model.HTTPMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;

public class GetMappingProducer extends MappingProducerBase {

	public GetMappingProducer(ApplicationContext applicationContext) {
		super(applicationContext);
	}
	
	private GetMapping extractAnnotation(Method method) {
		return method.getAnnotation(GetMapping.class);
	}

	@Override
	protected String[] url(Method method) {
		return extractAnnotation(method).value();
	}

	@Override
	protected HTTPMethod httpMethod() {
		return HTTPMethod.GET;
	}

	@Override
	protected String[] consumes(Method method) {
		return extractAnnotation(method).consumes();
	}

	@Override
	protected String[] produces(Method method) {
		return extractAnnotation(method).produces();
	}
}
