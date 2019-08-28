package org.chenile.owiz.config.impl;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.OrchConfigurator;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.BaseContext;
import org.chenile.owiz.impl.DelegatorCommand;
import org.chenile.owiz.impl.MockCommand;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.Router;
import org.chenile.owiz.impl.ognl.OgnlRouter;

import junit.framework.TestCase;

/**
 * The test case tests the functionality of XmlOrchConfigurator.
 * The following flow is set up to test the functionality:
 * 1. A new BaseContext object was created and the entire flow is created around it.
 * 2. A module router was set up as the first command in the flow.
 * 3. mod1Chain and mod2Chain are attached to the moduleRouter
 * 4. mod1Command is attached to mod1Chain
 * 5. mod2Command and mod2Command1 are attached to mod2Chain.
 * 
 * The test case verifies the following:
 * <ul>
 * <li> The correct chain must be executed depending on the module. This tests moduleRouter functionality (OgnlRouter) and also the base router functionality.
 * <li> The order of the commands for module2 would be verified. mod2Command must be executed BEFORE mod2Command1.
 * <li> Verifies the functionality of DelegatorCommand.
 * </ul>
 * @author Raja Shankar Kolluru
 *
 */
public class XmlOrchConfiguratorTest extends TestCase {
	private static final String MOD1_COMMAND_NAME = "mod1Command";
	private static final String MOD2_COMMAND_NAME = "mod2Command";
	private OrchConfigurator<BaseContext> oc;
	private OrchExecutor<BaseContext> oe;
	private static final int MOD1_COMMAND = 50;
	private static final int MOD2_COMMAND = 100;
	private static final String MODULE_ROUTER_NAME = "moduleRouter";
	private final static String MOD1 = "mod1";
	private final static String MOD2 = "mod2";
	
	public void setUp() throws Exception{
		/**
		 * Replicate Bean factory functionality. 
		 */
		final Map<String, Object> commandCatalog = new HashMap<String, Object>();
		BeanFactoryAdapter beanFactoryAdapter = new BeanFactoryAdapter() {
			
			public Object lookup(String componentName) {
				return commandCatalog.get(componentName);
			}
		};
		
		XmlOrchConfigurator<BaseContext> xoc = new XmlOrchConfigurator<BaseContext>();
		xoc.setBeanFactoryAdapter(beanFactoryAdapter);
		// populate command catalog with some commands.
		MockCommand mod1Command = new MockCommand(MOD1_COMMAND);
		commandCatalog.put(MOD1_COMMAND_NAME,mod1Command);
		MockCommand mod2Command = new MockCommand(MOD2_COMMAND);
		commandCatalog.put(MOD2_COMMAND_NAME,mod2Command);
		Router<BaseContext> moduleRouter = new OgnlRouter<BaseContext>();
		commandCatalog.put(MODULE_ROUTER_NAME, moduleRouter);
		
		// delegate command and foo which the delegator would delegate to - see owiz-test.xml
		DelegatorCommand<BaseContext> delegatorCommand = new DelegatorCommand<BaseContext>();
		delegatorCommand.setBeanFactoryAdapter(beanFactoryAdapter);
		commandCatalog.put("delegatorCommand",delegatorCommand);
		
		/* instantiate the command that the delegator command delegates to */
		Foo foo = new Foo();
		commandCatalog.put("foo",foo);
		// finally read the file.
		
		xoc.setFilename("org/chenile/owiz/config/impl/owiz-test.xml");
		
		oc = xoc;
		OrchExecutorImpl<BaseContext> oeimpl = new OrchExecutorImpl<BaseContext>();
		oeimpl.setOrchConfigurator(oc);
		oe = oeimpl;
	}
	
	public void testObjectCreation() throws Exception{
		CommandDescriptor<BaseContext> cd = oc.obtainFirstCommandInfo();
		assertEquals(MODULE_ROUTER_NAME,cd.getId());
		BaseContext baseContext = new BaseContext();
		baseContext.setModule(MOD1);
		oe.execute(baseContext);
		assertEquals(Integer.valueOf(MOD1_COMMAND),baseContext.getListOfIndexes().get(0));
		// ensure that the base context has been set the correct commandDescriptor before executing MockCommand
		// In other words, we are testing the CommandDescriptorAware functionality.
		assertEquals(MOD1_COMMAND_NAME,baseContext.getCommandIdExecuted());
		baseContext.getListOfIndexes().clear();
		baseContext.setModule(MOD2);
		oe.execute(baseContext);
		assertEquals(Integer.valueOf(MOD2_COMMAND),baseContext.getListOfIndexes().get(0));
		assertEquals(MOD2_COMMAND_NAME,baseContext.getCommandIdExecuted());
		assertEquals("abc", baseContext.get("foo"));
	}
}
