package org.chenile.query.test.service;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.query.service.commands.QueryChain;
import org.chenile.query.service.commands.model.SearchContext;
import org.chenile.query.service.impl.EntityQueryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@PropertySource("classpath:org/chenile/query/test/TestQueryService.properties")
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration", 
		"org.chenile.query.test.service"})
@ActiveProfiles("unittest")
public class SpringTestConfig extends SpringBootServletInitializer{
	@Autowired ApplicationContext applicationContext;
	@Bean public OrchExecutor<SearchContext> querySearchExecutor() throws Exception {
    	XmlOrchConfigurator<SearchContext> xmlOrchConfigurator = new XmlOrchConfigurator<>();
    	xmlOrchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {	
    		
			@Override
			public Object lookup(String componentName) {
				try {
				return applicationContext.getBean(componentName);
				}catch (Exception e) {
					return null;
				}
			}
		});
		xmlOrchConfigurator.setFilename("org/chenile/query/test/query-orch.xml");
		OrchExecutorImpl<SearchContext> executor = new OrchExecutorImpl<>();
		executor.setOrchConfigurator(xmlOrchConfigurator);
		return executor;		
    }
	
	@Bean ExecutorService queryExecutorService() {
		return Executors.newFixedThreadPool(100); // TODO change it to a configurable parameter
	}
	
	@Bean EntityQueryServiceImpl queryService(@Qualifier("querySearchExecutor") OrchExecutor<SearchContext> querySearchExecutor) {
		return new EntityQueryServiceImpl(querySearchExecutor);
	}
	
	QueryChain queryChain(ExecutorService queryExecutorService){
		QueryChain chain = new QueryChain();
		chain.setExecutorService(queryExecutorService);
		return chain;
	}
	
	@Bean QueryChain root(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean QueryChain northAmerica(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean QueryChain asia(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean QueryChain us(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean MockRTCommand china() {
		return new MockRTCommand();
	}
	
	@Bean QueryChain india(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean QueryChain karnataka(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean MockRTCommand bangalore(ExecutorService queryExecutorService) {
		return new MockRTCommand();
	}
	
	@Bean QueryChain tamilNadu(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean MockRTCommand chennai() {
		return new MockRTCommand();
	}
	
	@Bean MockRTCommand california() {
		return new MockRTCommand();
	}
	
	@Bean QueryChain newYork(ExecutorService queryExecutorService) {
		return queryChain(queryExecutorService);
	}
	
	@Bean MockRTCommand nyc() {
		return new MockRTCommand();
	}
	
	@Bean MockRTCommand syracuse() {
		return new MockRTCommand();
	}
	
	@Bean MockRTCommand canada() {
		return new MockRTCommand();
	}
	
	@Bean MockSimpleRTCommand mexico() {
		return new MockSimpleRTCommand();
	}
	
}

