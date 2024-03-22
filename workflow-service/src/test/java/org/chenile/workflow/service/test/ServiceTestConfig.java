package org.chenile.workflow.service.test;


import org.chenile.stm.STM;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.impl.BeanFactoryAdapter;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.impl.XmlFlowReader;
import org.chenile.stm.spring.SpringBeanFactoryAdapter;
import org.chenile.workflow.service.impl.StateEntityServiceImpl;
import org.chenile.workflow.service.stmcmds.BaseTransitionAction;
import org.chenile.workflow.service.stmcmds.GenericEntryAction;
import org.chenile.workflow.service.stmcmds.GenericExitAction;
import org.chenile.workflow.service.stmcmds.StmBodyTypeSelector;
import org.chenile.workflow.service.test.issues.AssignIssueAction;
import org.chenile.workflow.service.test.issues.CloseIssueAction;
import org.chenile.workflow.service.test.issues.Issue;
import org.chenile.workflow.service.test.issues.IssueEntityStore;
import org.chenile.workflow.service.test.issues.ResolveIssueAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@PropertySource("classpath:org/chenile/workflow/service/test/TestWorkflowService.properties")
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@ActiveProfiles("unittest")
public class ServiceTestConfig extends SpringBootServletInitializer implements InitializingBean{
	private static final String FLOW_DEFINITION_FILE = "org/chenile/workflow/service/test/issues.xml";
	@Autowired @Qualifier("issueFlowStore") STMFlowStoreImpl flowStore;
	
	@Bean BeanFactoryAdapter issueBeanFactoryAdapter() {
		return new SpringBeanFactoryAdapter();
	}
	
	@Bean STMFlowStoreImpl issueFlowStore(@Qualifier("issueBeanFactoryAdapter") BeanFactoryAdapter issueBeanFactoryAdapter) throws Exception{
		STMFlowStoreImpl stmFlowStore = new STMFlowStoreImpl();
		stmFlowStore.setBeanFactory(issueBeanFactoryAdapter);
		return stmFlowStore;
	}
	
	@Bean @Autowired STM<Issue> issueEntityStm(@Qualifier("issueFlowStore") STMFlowStoreImpl stmFlowStore) throws Exception{
		STMImpl<Issue> stm = new STMImpl<>();		
		stm.setStmFlowStore(stmFlowStore);
		return stm;
	}
	
	@Bean @Autowired STMActionsInfoProvider issueActionsInfoProvider(@Qualifier("issueFlowStore") STMFlowStoreImpl stmFlowStore) {
		return new STMActionsInfoProvider(stmFlowStore);
	}
	
	@Bean IssueEntityStore issueEntityStore() {
		return new IssueEntityStore();
	}
	
	@Bean @Autowired StateEntityServiceImpl<Issue> _issueStateEntityService_(
			@Qualifier("issueEntityStm") STM<Issue> stm,
			@Qualifier("issueActionsInfoProvider") STMActionsInfoProvider issueInfoProvider,
			@Qualifier("issueEntityStore") IssueEntityStore entityStore){
		return new StateEntityServiceImpl<>(stm, issueInfoProvider, entityStore);
	}
	
	// Now we start constructing the STM Components 
	
	@Bean @Autowired GenericEntryAction<Issue> issueEntryAction(@Qualifier("issueEntityStore") IssueEntityStore entityStore,
			@Qualifier("issueActionsInfoProvider") STMActionsInfoProvider issueInfoProvider){
		return new GenericEntryAction<Issue>(entityStore,issueInfoProvider);
	}
	
	@Bean GenericExitAction<Issue> issueExitAction(){
		return new GenericExitAction<Issue>();
	}
	
	@Bean @Autowired StmBodyTypeSelector issueBodyTypeSelector(@Qualifier("issueActionsInfoProvider") STMActionsInfoProvider issueInfoProvider) {
		return new StmBodyTypeSelector(issueInfoProvider);
	}
	
	@Bean @Autowired STMTransitionAction<Issue> issueBaseTransitionAction(){
		return new BaseTransitionAction<>();
	}
	
	@Bean AssignIssueAction assignIssue() {
		return new AssignIssueAction();
	}
	
	@Bean ResolveIssueAction resolveIssue() {
		return new ResolveIssueAction();
	}
	
	@Bean CloseIssueAction closeIssue() {
		return new CloseIssueAction();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Read the XML now after creating all the dependent beans. Else there
		// will be a circular dependency
		
		XmlFlowReader flowReader = new XmlFlowReader(flowStore);
		flowReader.setFilename(FLOW_DEFINITION_FILE);
	}
	
}

