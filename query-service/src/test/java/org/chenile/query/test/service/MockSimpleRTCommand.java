package org.chenile.query.test.service;

import java.util.Map;

import org.chenile.query.service.commands.GenericRTCommand;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * This command returns just one attribute for the parent
 * @author Raja Shankar Kolluru
 *
 */
public class MockSimpleRTCommand extends GenericRTCommand<MockFilter,Boolean> {

	@Override
	protected TypeReference<MockFilter> searchRequestType() {
		return new TypeReference<MockFilter>(){};
	}

	@Override
	protected Boolean executeSearch(Map<String, Object> headers, MockFilter searchRequest) {
		if (searchRequest.exceptionMessage != null)
			throw generateException(500,searchRequest.errorCode,searchRequest.exceptionMessage);
		return true;
	}

}
