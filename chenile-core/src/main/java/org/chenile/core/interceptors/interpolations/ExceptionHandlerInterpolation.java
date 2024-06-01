package org.chenile.core.interceptors.interpolations;

import java.util.ArrayList;
import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.InterpolationCommand;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Calls the exception handler that is defined in Chenile properties.
 */
public class ExceptionHandlerInterpolation extends InterpolationCommand<ChenileExchange> {

	@Autowired ChenileConfiguration chenileConfiguration;
	@Override
	protected List<Command<ChenileExchange>> fetchCommands(ChenileExchange context) {
		List<Command<ChenileExchange>>  list = new ArrayList<>();
		list.add(chenileConfiguration.getChenileExceptionHandler());
		return list;
	}

}
