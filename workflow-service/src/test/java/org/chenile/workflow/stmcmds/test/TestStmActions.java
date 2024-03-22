package org.chenile.workflow.stmcmds.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.chenile.stm.STM;
import org.chenile.stm.impl.BeanFactoryAdapter;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.impl.XmlFlowReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class demonstrates the orchestration capabilities built into some of the 
 * commands using simple workflows.
 * @author Raja Shankar Kolluru
 *
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("unittest")
// @ContextConfiguration(classes={SpringTestConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = SpringTestConfig.class)
public class TestStmActions {
	
	@Autowired ApplicationContext applicationContext;
	private static final String FLOW_DEFINITION_FILE = "org/chenile/workflow/stmcmds/test/stm.xml";
	public static final String XML_FILE = "org/chenile/workflow/stmcmds/test/orch.xml";
	private STM<SomeEntity> stm = new STMImpl<SomeEntity>();
	public static String stringToAdd = "mock";
	public static String tenant1 = "tenant1";
	public static String others = "others";
	
	@Before public void setUp() throws Exception {
		
		STMFlowStoreImpl stmFlowStore = new STMFlowStoreImpl();
		XmlFlowReader flowReader = new XmlFlowReader(stmFlowStore);
		stmFlowStore.setBeanFactory(new BeanFactoryAdapter() {
			
			@Override
			public Object getBean(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		flowReader.setFilename(FLOW_DEFINITION_FILE);
		stm.setStmFlowStore(stmFlowStore);		
	}
	
	
	@Test public void testCommonTransition() throws Exception{
		SomeEntity someEntity = new SomeEntity();
		stm.proceed(someEntity);
		someEntity = stm.proceed(someEntity,"T1",null);
		List<String> list = someEntity.getListOfStrings();
		assertEquals(1,list.size());
		assertEquals(stringToAdd,list.get(0));
	}
	
	@Test public void testOrchestrationTenant1() throws Exception{
		SomeEntity someEntity = new SomeEntity();
		stm.proceed(someEntity);
		someEntity.setTenantId("tenant1");
		someEntity = stm.proceed(someEntity,"T2",null);
		List<String> list = someEntity.getListOfStrings();
		assertEquals(1,list.size());
		assertEquals(tenant1,list.get(0));
	}
	
	@Test public void testOrchestrationDefault() throws Exception{
		SomeEntity someEntity = new SomeEntity();
		stm.proceed(someEntity);
		someEntity.setTenantId("tenant2");
		someEntity = stm.proceed(someEntity,"T2",null);
		List<String> list = someEntity.getListOfStrings();
		assertEquals(1,list.size());
		assertEquals(others,list.get(0));
	}

	@Test public void testOrchestrationXmlTenant1() throws Exception{
		SomeEntity someEntity = new SomeEntity();
		stm.proceed(someEntity);
		someEntity.setTenantId("tenant1");
		someEntity = stm.proceed(someEntity,"T3",null);
		List<String> list = someEntity.getListOfStrings();
		assertEquals(1,list.size());
		assertEquals(tenant1,list.get(0));
	}
	
	@Test public void testOrchestrationXmlDefault() throws Exception{
		SomeEntity someEntity = new SomeEntity();
		stm.proceed(someEntity);
		someEntity.setTenantId("tenant2");
		someEntity = stm.proceed(someEntity,"T3",null);
		List<String> list = someEntity.getListOfStrings();
		assertEquals(1,list.size());
		assertEquals(others,list.get(0));
	}
}
