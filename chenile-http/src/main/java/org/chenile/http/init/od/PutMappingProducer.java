package org.chenile.http.init.od;

import java.lang.reflect.Method;

import org.chenile.core.model.HTTPMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PutMapping;

public class PutMappingProducer extends MappingProducerBase {

	public PutMappingProducer(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	private PutMapping extractAnnotation(Method method) {
		return method.getAnnotation(PutMapping.class);
	}

	@Override
	protected String[] url(Method method) {
		return extractAnnotation(method).value();
	}

	@Override
	protected HTTPMethod httpMethod() {
		return HTTPMethod.PUT;
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
