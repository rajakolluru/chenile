package org.chenile.workflow.stmcmds.test;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.Router;
import org.chenile.owiz.impl.ognl.OgnlRouter;
import org.chenile.workflow.service.stmcmds.BaseTransitionAction;
import org.chenile.workflow.service.stmcmds.dto.TransitionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;


@Configuration
@PropertySource("classpath:org/chenile/workflow/service/test/TestWorkflowService.properties")
// @ComponentScan(basePackages = { "org.chenile.configuration"})
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" , "org.chenile.workflow.service.test"})
@ActiveProfiles("unittest")
public class SpringTestConfig {
	@Autowired ApplicationContext applicationContext;
	
	@Bean public BaseTransitionAction<SomeEntity> baseTransitionAction(){
		return new BaseTransitionAction<SomeEntity>();
	}
	
	@Bean public MockTransitionCommand mockTransitionCommand() {
		return new MockTransitionCommand(TestStmActions.stringToAdd);
	}
	
	@Bean public MockTransitionCommand tenant1TransitionCommand() {
		return new MockTransitionCommand(TestStmActions.tenant1);
	}
	
	@Bean public MockTransitionCommand otherTenantsTransitionCommand() {
		return new MockTransitionCommand(TestStmActions.others);
	}
	
	@Bean public Router<SomeEntity> tenantRouter(){
		return new OgnlRouter<SomeEntity>();
	}
	
	@Bean public Chain<TransitionContext<SomeEntity>> tenant1Chain(){
		return new Chain<TransitionContext<SomeEntity>>();
	}
	
	@Bean public Chain<TransitionContext<SomeEntity>> defaultChain(){
		return new Chain<TransitionContext<SomeEntity>>();
	}
	
	@Bean public Router<SomeEntity> tenantXmlRouter(){
		return new OgnlRouter<SomeEntity>();
	}
	
	@Bean public Chain<TransitionContext<SomeEntity>> tenant1XmlChain(){
		return new Chain<TransitionContext<SomeEntity>>();
	}
	
	@Bean public Chain<TransitionContext<SomeEntity>> defaultXmlChain(){
		return new Chain<TransitionContext<SomeEntity>>();
	}
	
	@Bean OrchExecutor<TransitionContext<SomeEntity>> mockOrchExecutor() throws Exception{
		XmlOrchConfigurator<TransitionContext<SomeEntity>> xmlOrchConfigurator = new XmlOrchConfigurator<TransitionContext<SomeEntity>>();
		xmlOrchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {
			@Override
			public Object lookup(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		xmlOrchConfigurator.setFilename(TestStmActions.XML_FILE);
		OrchExecutorImpl<TransitionContext<SomeEntity>> orchExecutor = new OrchExecutorImpl<TransitionContext<SomeEntity>>();
		orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
		return orchExecutor;
	}
}

