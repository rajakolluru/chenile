package org.chenile.query.service.commands;

import java.util.Map;

import org.chenile.base.exception.BadRequestException;
import org.chenile.query.service.SearchCommand;
import org.chenile.query.service.commands.model.SearchContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class GenericRTCommand<Request,Response> extends QueryPathCommand{
	private static ObjectMapper objectMapper = new ObjectMapper();
	protected SearchCommand<Request,Response> searchCommand;
	public GenericRTCommand() {}
	public GenericRTCommand(SearchCommand<Request, Response> searchCommand) {
		this.searchCommand = searchCommand;
	}

	@Override
	public void doExecute(SearchContext context) throws Exception {	
		Request s = obtainSearchRequest(context.req);
		Response sr = executeSearch(context.headers,s);	
		copyToParent(context,sr);
	}
	
	/**
	 * Override this method to implement custom transformation logic
	 * @param context
	 * @param sr
	 * @throws Exception
	 */
	protected void copyToParent(SearchContext context,Response sr)throws Exception {
		appendResponse(context.resp,sr);
	}
		
	protected Response executeSearch(Request searchRequest) {
		if (searchCommand != null) {
			return searchCommand.doSearch(searchRequest);
		}
		return null;
	}
	protected Response executeSearch(Map<String,Object> headers, Request searchRequest) {
		return executeSearch(searchRequest);
	}
	protected abstract TypeReference<Request> searchRequestType();
		
	private Request obtainSearchRequest(Map<String,Object> element) throws Exception{
		element = obtainLastRequest(element);
		if (element == null) return null;
		try {
			return objectMapper.convertValue(element, searchRequestType());
		}catch(Exception e) {
			throw new BadRequestException(102,
					new Object[] {getFullyQualifiedPathAsString()});
		}
	}

}