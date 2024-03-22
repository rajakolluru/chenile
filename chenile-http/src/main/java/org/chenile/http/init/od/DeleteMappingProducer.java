package org.chenile.http.init.od;

import java.lang.reflect.Method;

import org.chenile.core.model.HTTPMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.DeleteMapping;

public class DeleteMappingProducer extends MappingProducerBase {

	public DeleteMappingProducer(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	private DeleteMapping extractAnnotation(Method method) {
		return method.getAnnotation(DeleteMapping.class);
	}

	@Override
	protected String[] url(Method method) {
		return extractAnnotation(method).value();
	}

	@Override
	protected HTTPMethod httpMethod() {
		return HTTPMethod.DELETE;
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
