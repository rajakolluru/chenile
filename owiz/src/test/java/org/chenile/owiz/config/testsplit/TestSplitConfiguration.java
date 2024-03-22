package org.chenile.owiz.config.testsplit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.config.testbasic.MockCommand;
import org.chenile.owiz.config.testbasic.MockContext;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.ognl.OgnlRouter;
import org.junit.Test;

public class TestSplitConfiguration {
	protected OrchExecutor<MockContext> setupSplitConfig() throws Exception{
		return setupConfig();
	}
	
	protected OrchExecutor<MockContext> setupConfig() throws Exception{
		XmlOrchConfigurator<MockContext> xmlOrchConfigurator = new XmlOrchConfigurator<MockContext>();
		xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
		xmlOrchConfigurator.setFilename("org/chenile/owiz/config/testsplit/owiz-config.xml",
				"org/chenile/owiz/config/testsplit/owiz-config1.xml");
		OrchExecutorImpl<MockContext> orchExecutor = new OrchExecutorImpl<MockContext>();
		orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
		return orchExecutor;
	}
	
	private BeanFactoryAdapter setupBeanFactoryAdapter(){
		final Map<String,Command<MockContext>> map = new HashMap<String,Command<MockContext>>();
		map.put("mock11", new MockCommand("mock11"));
		map.put("mock12", new MockCommand("mock12"));
		map.put("mock13", new MockCommand("mock13"));
		map.put("mock21", new MockCommand("mock21"));
		map.put("mock22", new MockCommand("mock22"));
		map.put("mock23", new MockCommand("mock23"));
		map.put("moduleRouter", new OgnlRouter<MockContext>());
		
		BeanFactoryAdapter bfa = new BeanFactoryAdapter() {
			
			public Object lookup(String componentName) {
				return map.get(componentName);
			}
		};
		return bfa;
	}
	
	@Test public void testEasyConfigMod1()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod1");
		orchExecutor.execute(mc);
		assertEquals("mock11 not invoked properly", "mock11",mc.getLog(0));
		assertEquals("mock12 not invoked properly", "mock12",mc.getLog(1));
		assertEquals("mock13 not invoked properly", "mock13",mc.getLog(2));
	}
	
	@Test public void testEasyConfigMod2()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod2");
		orchExecutor.execute(mc);
		assertEquals("mock21 not invoked properly", "mock21",mc.getLog(0));
		assertEquals("mock22 not invoked properly", "mock22",mc.getLog(1));
		assertEquals("mock23 not invoked properly", "mock23",mc.getLog(2));
	}
	
}
