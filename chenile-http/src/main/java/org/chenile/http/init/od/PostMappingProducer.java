package org.chenile.http.init.od;

import java.lang.reflect.Method;

import org.chenile.core.model.HTTPMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;

public class PostMappingProducer extends MappingProducerBase {

	public PostMappingProducer(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	private PostMapping extractAnnotation(Method method) {
		return method.getAnnotation(PostMapping.class);
	}

	@Override
	protected String[] url(Method method) {
		return extractAnnotation(method).value();
	}

	@Override
	protected HTTPMethod httpMethod() {
		return HTTPMethod.POST;
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
