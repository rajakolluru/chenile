package org.chenile.owiz.impl.whilechain;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.owiz.impl.ognl.ForLoop;
import org.chenile.owiz.impl.ognl.SetCommand;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestWhile {
    protected OrchExecutor<WhileContext> setupForLoop() throws Exception{
        return setupConfig("org/chenile/owiz/config/impl/whilechain/whilechain.xml");
    }

    protected OrchExecutor<WhileContext> setupForLoopWithSet() throws Exception{
        return setupConfig("org/chenile/owiz/config/impl/whilechain/whilechain-withset.xml");
    }

    protected OrchExecutor<WhileContext> setupSet() throws Exception{
        return setupConfig("org/chenile/owiz/config/impl/whilechain/set.xml");
    }

    protected OrchExecutor<WhileContext> setupConfig(String filename) throws Exception{
        XmlOrchConfigurator<WhileContext> xmlOrchConfigurator = new XmlOrchConfigurator<WhileContext>();
        xmlOrchConfigurator.setBeanFactoryAdapter(setupBeanFactoryAdapter());
        xmlOrchConfigurator.setFilename(filename);

        OrchExecutorImpl<WhileContext> orchExecutor = new OrchExecutorImpl<WhileContext>();
        orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
        return orchExecutor;
    }

    private BeanFactoryAdapter setupBeanFactoryAdapter(){
        final Map<String, Command<WhileContext>> map = new HashMap<String,Command<WhileContext>>();
        map.put("chain",new Chain<>());
        map.put("while", new ForLoop<>());
        map.put("incrementCounter", new IncrementCommand());
        map.put("track", new TrackLoopExecutions());

        return new BeanFactoryAdapter() {
            public Object lookup(String componentName) {
                // set is a prototype command. Hence, a new instance needs to be
                // created everytime it is looked up. If it is not handled as a
                // prototype then the same Set command instance will be used for
                // both incrementing counter and index which will lead to erroneous results.
                // (see the whilechain-withset.xml)
                if(componentName.equals("set")) return new SetCommand<>();
                return map.get(componentName);
            }
        };
    }
    @Test public void testForLoop() throws Exception{
        WhileContext context = new WhileContext(0);
        OrchExecutor<WhileContext> orchExecutor = setupForLoop();
        orchExecutor.execute(context);
        assertEquals("Loop must have repeated three times",3,context.counter);
    }

    @Test public void testForLoopWithSet() throws Exception{
        WhileContext context = new WhileContext(0);
        OrchExecutor<WhileContext> orchExecutor = setupForLoopWithSet();
        orchExecutor.execute(context);
        assertEquals("Loop must have repeated three times",3,context.counter);
    }

    @Test public void testSet() throws Exception{
        WhileContext context = new WhileContext(0);
        OrchExecutor<WhileContext> orchExecutor = setupSet();
        orchExecutor.execute(context);
        assertEquals("foo must be set to bar","bar",context.foo);
    }
}
