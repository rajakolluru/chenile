package org.chenile.query.service.commands;

import org.chenile.query.model.SearchRequest;
import org.chenile.query.service.SearchService;

public abstract class RTCommand<Filter> extends GenericRTCommand<SearchRequest<Filter>, Object>{
	private SearchService<Filter> searchService;
	public RTCommand() {}
	public RTCommand(SearchService<Filter> searchService) {
		this.searchService = searchService;
	}
	@Override
	protected Object executeSearch(SearchRequest<Filter> searchRequest) {
		if (searchService != null) {
			return searchService.search(searchRequest);
		}
		return super.executeSearch(searchRequest);
	}	
}
