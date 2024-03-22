package org.chenile.http.init.od;

import java.lang.reflect.Method;

import org.chenile.core.model.HTTPMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PatchMapping;

public class PatchMappingProducer extends MappingProducerBase {

	public PatchMappingProducer(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	private PatchMapping extractAnnotation(Method method) {
		return method.getAnnotation(PatchMapping.class);
	}

	@Override
	protected String[] url(Method method) {
		return extractAnnotation(method).value();
	}

	@Override
	protected HTTPMethod httpMethod() {
		return HTTPMethod.PATCH;
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
