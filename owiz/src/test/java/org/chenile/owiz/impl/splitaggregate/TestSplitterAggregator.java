package org.chenile.owiz.impl.splitaggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.junit.Test;

public class TestSplitterAggregator {
	
	private OrchExecutor<SplitterContext> setupConfig() throws Exception{
		XmlOrchConfigurator<SplitterContext> xmlOrchConfigurator = new XmlOrchConfigurator<SplitterContext>();
		xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
		xmlOrchConfigurator.setFilename("org/chenile/owiz/config/impl/splitaggregate/owiz-test.xml");
		
		OrchExecutorImpl<SplitterContext> orchExecutor = new OrchExecutorImpl<SplitterContext>();
		orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
		return orchExecutor;
	}
	
	private BeanFactoryAdapter setupBeanFactoryAdapter(){
		ExecutorService ex = Executors.newFixedThreadPool(10);
		
		final Map<String,Object> map = new HashMap<String,Object>();
		map.put("individualSplitCommand", new MockSplitCommand());
		map.put("splitAggregate", new SplitterAggregator(ex,60));
		
		BeanFactoryAdapter bfa = new BeanFactoryAdapter() {
			
			public Object lookup(String componentName) {
				return map.get(componentName);
			}
		};
		return bfa;
	}
	
	private SplitterContext makeMockSplitterContext(){
		MockSplitterContext splitterContext = new MockSplitterContext();
		CountDownLatch latch = new CountDownLatch(1);
		List<IndividualSplitContext> list = new ArrayList<IndividualSplitContext>();
		MockIndividualSplitContext isc = new MockIndividualSplitContext("1",latch);
		list.add(isc);
		isc = new MockIndividualSplitContext("2",latch);
		list.add(isc);
		isc = new MockIndividualSplitContext("3",latch);
		list.add(isc);
		splitterContext.setList(list);
		return splitterContext;
	}
	
	private SplitterContext makeMockSplitterContextWithException(){
		MockSplitterContext splitterContext = new MockSplitterContext();
		CountDownLatch latch = new CountDownLatch(1);
		List<IndividualSplitContext> list = new ArrayList<IndividualSplitContext>();
		MockIndividualSplitContext isc = new MockIndividualSplitContext("1",latch);
		isc.setMustThrowException(true);
		list.add(isc);
		isc = new MockIndividualSplitContext("2",latch);
		list.add(isc);
		isc = new MockIndividualSplitContext("3",latch);
		list.add(isc);
		splitterContext.setList(list);
		return splitterContext;
	}
	
	private SplitterContext makeMockSplitterContextWithLatch(){
		MockSplitterContext splitterContext = new MockSplitterContext();
		CountDownLatch latch = new CountDownLatch(1);
		List<IndividualSplitContext> list = new ArrayList<IndividualSplitContext>();
		MockIndividualSplitContext isc = new MockIndividualSplitContext("1",latch);
		isc.setMustWait(true);
		isc.setTimeOutInMilliSeconds(10);
		list.add(isc);
		isc = new MockIndividualSplitContext("2",latch);
		list.add(isc);
		isc = new MockIndividualSplitContext("3",latch);
		list.add(isc);
		splitterContext.setList(list);
		return splitterContext;
	}
	
	private SplitterContext makeMockSplitterContextFor2Fail(){
		MockSplitterContext splitterContext = new MockSplitterContext();
		CountDownLatch latch = new CountDownLatch(1);
		List<IndividualSplitContext> list = new ArrayList<IndividualSplitContext>();
		MockIndividualSplitContext isc = new MockIndividualSplitContext("1",latch);
		isc.setMustWait(true);
		isc.setTimeOutInMilliSeconds(100);
		list.add(isc);
		isc = new MockIndividualSplitContext("2",latch);
		isc.setMustWait(true);
		isc.setTimeOutInMilliSeconds(40);
		list.add(isc);
		isc.setMustWait(true);
		isc.setTimeOutInMilliSeconds(30);
		isc = new MockIndividualSplitContext("3",latch);
		list.add(isc);
		splitterContext.setList(list);
		return splitterContext;
	}
	
	@Test public void testIfAllExecuted()  throws Exception {
		OrchExecutor<SplitterContext> orchExecutor = setupConfig();	
		SplitterContext context = makeMockSplitterContext();
		orchExecutor.execute(context);
		Map<String, Object> x = context.getAggregatedContext();
		assertEquals(3,x.size());
		assertTrue(x.get("1").equals("1" + MockSplitCommand.RETURN));
		assertTrue(x.get("2").equals("2" + MockSplitCommand.RETURN));
		assertTrue(x.get("3").equals("3" + MockSplitCommand.RETURN));
	}
	
	@Test public void testIfAllExecutedException()  throws Exception {
		OrchExecutor<SplitterContext> orchExecutor = setupConfig();	
		SplitterContext context = makeMockSplitterContextWithException();
		orchExecutor.execute(context);
		Map<String, Object> x = context.getAggregatedContext();
		assertEquals(3,x.size());
		Throwable t = (Throwable)x.get("1");
		
		assertTrue(t.getMessage().endsWith(MockSplitCommand.OOOPS_SOMETHING_WENT_WRONG));
		assertTrue(x.get("2").equals("2" + MockSplitCommand.RETURN));
		assertTrue(x.get("3").equals("3" + MockSplitCommand.RETURN));
	}
	
	@Test public void testIfAllExecutedWithTimeout()  throws Exception {
		OrchExecutor<SplitterContext> orchExecutor = setupConfig();	
		SplitterContext context = makeMockSplitterContextWithLatch();
		orchExecutor.execute(context);
		Map<String, Object> x = context.getAggregatedContext();
		assertEquals(3,x.size());
		assertTrue(x.get("1").equals("1" + MockSplitCommand.RETURN));
		assertTrue(x.get("2").equals("2" + MockSplitCommand.RETURN));
		assertTrue(x.get("3").equals("3" + MockSplitCommand.RETURN));
	}
	
	@Test public void testIfOneFailedwithTimeoutForAll()  throws Exception {
		OrchExecutor<SplitterContext> orchExecutor = setupConfig();	
		SplitterContext context = makeMockSplitterContextFor2Fail();
		orchExecutor.execute(context);
		Map<String, Object> x = context.getAggregatedContext();
		assertEquals(3,x.size());
		assertTrue(x.get("1").equals(SplitterAggregator.TIMEOUT_ERR));
		assertTrue(x.get("2").equals("2" + MockSplitCommand.RETURN));
		assertTrue(x.get("3").equals("3" + MockSplitCommand.RETURN));
	}	
}