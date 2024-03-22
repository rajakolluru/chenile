/**
 * Copyright (c) 2007 - 2010 GlobalLogic Inc. All rights reserved.
 */
package org.chenile.query.service;

import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;

public interface SearchService<T> {
	public SearchResponse search(SearchRequest<T> searchInput);
}
