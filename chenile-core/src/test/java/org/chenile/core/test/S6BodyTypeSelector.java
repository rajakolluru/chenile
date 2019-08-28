package org.chenile.core.test;

import org.chenile.base.exception.BadRequestException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;

/**
 * Sets the body type for function s6 in {@link MockService}
 * @author meratransport
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
			exchange.setBodyType(E1.class);
		}else if (eventId.equals("e2")) {
			exchange.setBodyType(E2.class);
		}
		
	}

}
