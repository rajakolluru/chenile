package org.chenile.query.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.chenile.query.model.QueryMetadata;
import org.chenile.query.service.QueryStore;

/**
 * Implementation of query store. This stores a MYbatis query name along with query meta data
 * Currently this implementation is done in a map. Map gets initialized during start up.
 * See {@link QueryDefinitions} for a concrete implementation.
 */
public abstract class BaseQueryStore implements QueryStore{

	protected Map<String, QueryMetadata> store = new HashMap<>();

	public BaseQueryStore() {
		super();
	}
	
	public QueryMetadata retrieve(String queryId) {
		QueryMetadata queryMetadata = store.get(queryId);
		if (null == queryMetadata) {
			queryMetadata = retrieveQueryIdFromStore(queryId);
			addMetadata(queryMetadata);
		}
		return queryMetadata;
	}

	public void addMetadata(QueryMetadata queryMetadata) {
		if (null != queryMetadata) {
			store.put(queryMetadata.getId(), queryMetadata);
		}
	}

	public void setStore(Map<String,QueryMetadata> store) {
		this.store = store;
	}
	
	public abstract QueryMetadata retrieveQueryIdFromStore(String queryId);

}