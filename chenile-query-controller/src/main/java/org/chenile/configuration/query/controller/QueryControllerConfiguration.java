/**
 * 
 */
package org.chenile.configuration.query.controller;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.Router;
import org.chenile.owiz.impl.ognl.EvaluateRouter;
import org.chenile.query.controller.QueryController;
import org.chenile.query.controller.commands.AuthReporteesCommand;
import org.chenile.query.controller.commands.EnhanceSystemFiltersCommand;
import org.chenile.query.controller.commands.IOTSortingCommand;
import org.chenile.query.controller.commands.SearchRequestCommand;
import org.chenile.query.controller.dto.QueryExchange;
import org.chenile.query.controller.impl.QueryControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryControllerConfiguration {
	
	@Autowired private ApplicationContext applicationContext;

	@Bean
	public QueryController queryController() {
		return new QueryControllerImpl();
	}
	
	@Bean
	public Command<QueryExchange> searchRequestCommand() {
		return new SearchRequestCommand();
	}
	
	@Bean
	public OrchExecutor<QueryExchange> queryControllerOrchExecutor() {
		XmlOrchConfigurator<QueryExchange> orchConfigurator = new XmlOrchConfigurator<>();
		orchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {
			@Override
			public Object lookup(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		orchConfigurator.setFilename("org/chenile/owiz/query/query-controller-orch.xml");
		OrchExecutorImpl<QueryExchange> orchExecutor = new OrchExecutorImpl<>();
		orchExecutor.setOrchConfigurator(orchConfigurator);
		return orchExecutor;
	}
	
	/*@Bean
	public Command<QueryExchange> queryControllerEmptyCommand() {
		return (Command<QueryExchange>) OrchExecutor.emptyCommand();
	}*/
	
	@Bean
	public Command<QueryExchange> authReporteesCommand() {
		return new AuthReporteesCommand();
	}
	
	@Bean
	public Command<QueryExchange> iotSortingCommand() {
		return new IOTSortingCommand();
	}
	
	@Bean
	public Command<QueryExchange> enhanceSystemFiltersCommand() {
		return new EnhanceSystemFiltersCommand();
	}
	
	@Bean
	public Chain<QueryExchange> queryControllerChain() {
		return new Chain<>();
	}
	
	@Bean
	public Chain<QueryExchange> iotQueryChain() {
		return new Chain<>();
	}
	
	@Bean
	public Router<QueryExchange> evaluateAuthReporteesRouter() {
		return new EvaluateRouter<>();
	}
}
