package org.chenile.query.test.service;

import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.service.commands.RTCommand;

import com.fasterxml.jackson.core.type.TypeReference;

public class MockRTCommand extends RTCommand<MockFilter>{
	@Override
	protected SearchResponse executeSearch(SearchRequest<MockFilter> searchRequest) {
		MockFilter filter = searchRequest.getFilters();
		if (filter != null && filter.exceptionMessage != null)
			throw generateException(500,filter.errorCode,filter.exceptionMessage);
		SearchResponse response = new SearchResponse();
		ResponseRow rr = new ResponseRow();
		rr.setRow(getLastPathSegment());
		response.setData(rr);
		return response;
	}

	@Override
	protected TypeReference<SearchRequest<MockFilter>> searchRequestType() {
		return new TypeReference<SearchRequest<MockFilter>>() {};
	}
}
