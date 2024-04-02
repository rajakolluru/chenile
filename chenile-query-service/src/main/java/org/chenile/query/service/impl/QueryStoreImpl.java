package org.chenile.query.service.impl;

// import org.chenile.es.ElasticsearchStore;
import org.chenile.query.model.QueryMetadata;

import jakarta.annotation.PostConstruct;


public class QueryStoreImpl extends BaseQueryStore {

	// @Autowired
	// private ElasticsearchStore<QueryMetadata> queryEsStore;

	@PostConstruct
	public void init() {
//		List<ResponseRow> queryMetadataList = (List<ResponseRow>) queryEsStore.retrieveAll(null, true);
//		for (ResponseRow queryMetadata : queryMetadataList) {
//			super.addMetadata((QueryMetadata) queryMetadata.getRow());
//		}
	}

	@Override
	public QueryMetadata retrieveQueryIdFromStore(String queryId) {
		// QueryMetadata queryMetadata = queryEsStore.retrieve(queryId);
		//return queryMetadata;
		return null;
	}
}
