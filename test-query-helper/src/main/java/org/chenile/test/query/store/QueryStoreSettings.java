package org.chenile.test.query.store;

import java.util.Map;

import org.chenile.query.model.QueryMetadata;
import org.chenile.query.service.QueryStore;
import org.chenile.query.service.impl.BaseQueryStore;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryStoreSettings {
	@Autowired private QueryStore queryStore;
	
	public void init(Map<String, QueryMetadata> store) {		
		((BaseQueryStore)queryStore).setStore(store );
	}
	
	public void addMetadata(QueryMetadata qm) {
		((BaseQueryStore)queryStore).addMetadata(qm);
	}
}
