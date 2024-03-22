package org.chenile.owiz.config.testbasic;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.ognl.OgnlRouter;
import org.junit.Test;

public class TestBasicConfiguration {
	
	protected OrchExecutor<MockContext> setupEasyConfig() throws Exception{
		return setupConfig("org/chenile/owiz/config/testbasic/owiz-easyconfig.xml");
	}
	
	protected OrchExecutor<MockContext> setupModularConfig() throws Exception{
		return setupConfig("org/chenile/owiz/config/testbasic/owiz-modularconfig.xml");
	}
	
	protected OrchExecutor<MockContext> setupConfig(String filename) throws Exception{
		XmlOrchConfigurator<MockContext> xmlOrchConfigurator = new XmlOrchConfigurator<MockContext>();
		xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
		xmlOrchConfigurator.setFilename(filename);
		
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
		map.put("mock31", new MockCommand("mock31"));
		map.put("moduleRouter", new OgnlRouter<MockContext>());
		
		BeanFactoryAdapter bfa = new BeanFactoryAdapter() {
			
			public Object lookup(String componentName) {
				return map.get(componentName);
			}
		};
		return bfa;
	}
	
	@Test public void testEasyConfigMod1()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupEasyConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod1");
		orchExecutor.execute(mc);
		assertEquals("mock11 not invoked properly", "mock11",mc.getLog(0));
		assertEquals("mock12 not invoked properly", "mock12",mc.getLog(1));
		assertEquals("mock13 not invoked properly", "mock13",mc.getLog(2));
	}
	
	@Test public void testEasyConfigMod2()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupEasyConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod2");
		orchExecutor.execute(mc);
		assertEquals("mock21 not invoked properly", "mock21",mc.getLog(0));
		assertEquals("mock22 not invoked properly", "mock22",mc.getLog(1));
		assertEquals("mock23 not invoked properly", "mock23",mc.getLog(2));
	}
	
	@Test public void testModularConfigMod1()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupModularConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod1");
		orchExecutor.execute(mc);
		assertEquals("mock11 not invoked properly", "mock11",mc.getLog(0));
		assertEquals("mock12 not invoked properly", "mock12",mc.getLog(1));
		assertEquals("mock13 not invoked properly", "mock13",mc.getLog(2));
	}
	
	@Test public void testModularConfigMod2()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupModularConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod2");
		orchExecutor.execute(mc);
		assertEquals("mock21 not invoked properly", "mock21",mc.getLog(0));
		assertEquals("mock22 not invoked properly", "mock22",mc.getLog(1));
		assertEquals("mock23 not invoked properly", "mock23",mc.getLog(2));
	}
	
	// demonstrating the usage of the same chain (chain1) in two different places. (for mod1 and mod3)
	@Test public void testModularConfigMod3()  throws Exception {
		OrchExecutor<MockContext> orchExecutor = setupModularConfig();
		MockContext mc = new MockContext();
		mc.setModule("mod3");
		orchExecutor.execute(mc);
		assertEquals("mock31 not invoked properly", "mock31",mc.getLog(0));
		assertEquals("mock11 not invoked properly", "mock11",mc.getLog(1));
		assertEquals("mock12 not invoked properly", "mock12",mc.getLog(2));
		assertEquals("mock13 not invoked properly", "mock13",mc.getLog(3));
	}
}
