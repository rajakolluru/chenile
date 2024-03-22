package org.chenile.owiz.impl.parallelchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.ParallelChain;
import org.junit.Test;

public class TestParallelChain {
		
		private LatchWaitCommand command1 = new LatchWaitCommand("Command2","Command1");
		private LatchWaitCommand command2 = new LatchWaitCommand("Command3","Command2");
		private LatchWaitCommand command3 = new LatchWaitCommand("Command4","Command3");
		private LatchWaitCommand command4 = new LatchWaitCommand(null,"Command4");
		
		private OrchExecutor<ParallelChainContext> setupConfig() throws Exception{
			XmlOrchConfigurator<ParallelChainContext> xmlOrchConfigurator = new XmlOrchConfigurator<ParallelChainContext>();
			xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
			xmlOrchConfigurator.setFilename("org/chenile/owiz/config/impl/parallelchain/owiz-test.xml");
			
			OrchExecutorImpl<ParallelChainContext> orchExecutor = new OrchExecutorImpl<ParallelChainContext>();
			orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
			return orchExecutor;
		}
		
		private BeanFactoryAdapter setupBeanFactoryAdapter(){
			ExecutorService ex = Executors.newFixedThreadPool(10);
			
			final Map<String,Object> map = new HashMap<String,Object>();
			map.put("Command1", command1);
			map.put("Command2", command2);
			map.put("Command3", command3);
			map.put("Command4", command4);
			ParallelChain<ParallelChainContext> chain = new ParallelChain<ParallelChainContext>();
			chain.setExecutorService(ex);
			
			map.put("parallelChain", chain);
			
			BeanFactoryAdapter bfa = new BeanFactoryAdapter() {
				
				public Object lookup(String componentName) {
					return map.get(componentName);
				}
			};
			return bfa;
		}
		
		
		@Test public void testIfAllExecuted()  throws Exception {
			OrchExecutor<ParallelChainContext> orchExecutor = setupConfig();	
			ParallelChainContext context = new ParallelChainContext();
			command1.exceptionMessage = null;
			orchExecutor.execute(context);
			
			assertEquals(4,context.list.size());
			assertTrue(context.list.get(0).equals("Command4"));
			assertTrue(context.list.get(1).equals("Command3"));
			assertTrue(context.list.get(2).equals("Command2"));
			assertTrue(context.list.get(3).equals("Command1"));
		}
		
		@Test public void testExceptionProcessing()  throws Exception {
			String exceptionMessage = "someException";
			OrchExecutor<ParallelChainContext> orchExecutor = setupConfig();	
			ParallelChainContext context = new ParallelChainContext();
			command1.exceptionMessage = exceptionMessage;
			orchExecutor.execute(context);
			
			assertEquals(3,context.list.size());
			assertTrue(context.list.get(0).equals("Command4"));
			assertTrue(context.list.get(1).equals("Command3"));
			assertTrue(context.list.get(2).equals("Command2"));
			// Command1 will be missing
			assertEquals(1,context.getWarningMessages().size());
			assertEquals("Expected message does not equal actual",exceptionMessage,
					context.getWarningMessages().get(0).getDescription());
		}
		
}		
