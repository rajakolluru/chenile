package org.chenile.query.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.chenile.query.model.QueryMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads the query definitions from a JSON file that has been passed to it.
 * There can be multiple JSON files with the same name in the project. It reads them all
 * and gathers all the definitions in one place. 
 */
public class QueryDefinitions extends BaseQueryStore{
	private final Logger logger = LoggerFactory.getLogger(QueryDefinitions.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	public QueryDefinitions(Resource[] queryDefinitionFiles) throws IOException {
		for (Resource file: queryDefinitionFiles ) {
			processFile(file);
		}
	}
	private void processFile(Resource file) throws IOException {
		String content = file.getContentAsString(Charset.defaultCharset());			
		List<QueryMetadata> queries  = objectMapper.readValue(content, new TypeReference<List<QueryMetadata>>() {} );
		for (QueryMetadata qm: queries) {
			store.put(qm.getName(), qm);
			logger.debug("Discovered name:" + qm.getName());
		}
	}
	@Override
	public QueryMetadata retrieveQueryIdFromStore(String queryId) {
		return store.get(queryId);
	}
}
