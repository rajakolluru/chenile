package org.chenile.core.test;

import org.chenile.base.exception.BadRequestException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Sets the body type for function s6 in {@link MockService}
 * @author Raja Shankar Kolluru
 *
 */
public class S6BodyTypeSelector implements Command<ChenileExchange>{

	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		String eventId = exchange.getHeader("eventId",String.class);
		if (eventId == null) {
			throw new BadRequestException(10001, "event id must be passed");
		}
		
		if (eventId.equals("e1")) {
			exchange.setBodyType(new TypeReference<E1>() {});
		}else if (eventId.equals("e2")) {
			exchange.setBodyType(new TypeReference<E2>() {});
		}
		
	}

}
