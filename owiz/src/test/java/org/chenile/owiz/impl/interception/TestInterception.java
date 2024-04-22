package org.chenile.owiz.impl.interception;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.config.testbasic.MockCommand;
import org.chenile.owiz.config.testbasic.MockContext;
import org.chenile.owiz.impl.FilterChain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestInterception {
    OrchExecutorImpl<InterceptionContext> orchExecutor;
    final Map<String, Command<InterceptionContext>> map = new HashMap<String,Command<InterceptionContext>>();
    @Before public void setup() throws Exception{
        String filename = "org/chenile/owiz/config/impl/interception/interception.xml";
        XmlOrchConfigurator<InterceptionContext> xmlOrchConfigurator = new XmlOrchConfigurator<InterceptionContext>();
        xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
        xmlOrchConfigurator.setFilename(filename);

        orchExecutor = new OrchExecutorImpl<InterceptionContext>();
        orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
    }

    private BeanFactoryAdapter setupBeanFactoryAdapter() {
        map.put("interceptionChain", new FilterChain<InterceptionContext>());
        map.put("interceptor1", new Interceptor("Interceptor1"));
        map.put("interceptor2", new Interceptor("Interceptor2"));
        map.put("finalCommand", new FinalCommand("finalCommand"));
        return  new BeanFactoryAdapter() {
            public Object lookup(String componentName) {
                return map.get(componentName);
            }
        };
    }

    @Test public void testBasicExecution() throws Exception{
        InterceptionContext context = new InterceptionContext();
        orchExecutor.execute(context);
        assertOrder(context.executionOrder,"preInterceptor1", "preInterceptor2",
                "finalCommand", "postInterceptor2", "postInterceptor1");
    }

    @Test public void testSavePoint() throws Exception{
        InterceptionContext context = new InterceptionContext();
        Interceptor interceptor1 = (Interceptor)map.get("interceptor1");
        interceptor1.setRepeat(true);
        orchExecutor.execute(context);
        // check that the interceptor2 and finalCommands are called again
        assertOrder(context.executionOrder,"preInterceptor1", "preInterceptor2",
                "finalCommand", "postInterceptor2", "preInterceptor2", "finalCommand",
                "postInterceptor2","postInterceptor1");
    }
    private void assertOrder(List<String> list, String... strings){
        int index = 0;
        for (String string: strings){
            assertEquals(string, list.get(index++));
        }
    }
}
