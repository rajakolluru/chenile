package org.chenile.configuration.query.service.test;

import java.util.List;

import org.chenile.query.model.CannedReport;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.service.CannedReportStore;
import org.chenile.query.service.QueryStore;
import org.chenile.query.service.impl.BaseQueryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("unittest")
public class QueryEsDatasourceUnittest {
	@Bean
	public QueryStore queryStore() {
		return new BaseQueryStore() {
			
			@Override
			public QueryMetadata retrieveQueryIdFromStore(String queryId) {
				return store.get(queryId);
			}
		};
	}
	
	@Bean
	public CannedReportStore cannedReportStore() {
		return new CannedReportStore() {
			
			@Override
			public void store(CannedReport cannedReport) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public CannedReport retrieve(String cannedReportName, String queryName) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<CannedReport> getAllCannedReportsForUserTenant(String queryName) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
}
