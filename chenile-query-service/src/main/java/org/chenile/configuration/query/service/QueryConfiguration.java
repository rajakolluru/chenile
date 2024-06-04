package org.chenile.configuration.query.service;

import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.chenile.query.service.QueryStore;
import org.chenile.query.service.SearchService;
import org.chenile.query.service.impl.NamedQueryServiceSpringMybatisImpl;
import org.chenile.query.service.impl.QueryDefinitions;
import org.chenile.query.service.interceptor.QuerySAASInterceptor;
import org.chenile.query.service.interceptor.QueryUserFilterInterceptor;

/**
 * Registers query beans in Spring
 */
@Configuration
public class QueryConfiguration {

	@Value("${query.mapperFiles}")
	private Resource[] mapperFiles;
	
	@Value("${query.definitionFiles}")
	private Resource[] queryDefinitionFiles;
	
	@Bean("queryDefinitions") QueryDefinitions queryDefinitions() throws IOException{
		return new QueryDefinitions(queryDefinitionFiles);
	}

    @Bean("queryDatasource")
    @ConfigurationProperties(prefix = "query.datasource")
    DataSource queryDataSource() {
		return DataSourceBuilder.create().build();
	}

    @Bean
    SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("queryDatasource") DataSource queryDataSource)
            throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(queryDataSource);
		factoryBean.setMapperLocations(mapperFiles);
		return factoryBean.getObject();
	}

    @Bean
    SearchService<Map<String, Object>> searchService(@Autowired @Qualifier("queryDefinitions") 
       QueryStore queryStore) {
		return new NamedQueryServiceSpringMybatisImpl(queryStore);
	}

    @Bean
    SqlSessionTemplate sqlSessionTemplate(@Autowired SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

    @Bean
    QuerySAASInterceptor querySAASInterceptor() {
		return new QuerySAASInterceptor();
	}

    @Bean
    QueryUserFilterInterceptor queryUserFilterInterceptor() {
		return new QueryUserFilterInterceptor();
	}
}
