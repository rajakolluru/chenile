package org.chenile.query.service;

public interface SearchCommand<Request,Response> {
	public Response doSearch(Request request);
}
