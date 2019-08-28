package org.chenile.http.test;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestChenileHttp.class)
@ActiveProfiles("unittest")
public class TestHttpHandlers {
	@Autowired ApplicationContext applicationContext;
	@Autowired B b;
	@Autowired @Qualifier("props") Map<String, Object> props;
	@Autowired @Qualifier("liquibaseP") LiquibaseProperties liquibaseP;
	
    @Test public void testA() {
    	A a = applicationContext.getBean("a",A.class);
    	a.setSuffix("1");
    	System.out.println("A: " + a.getString());
    	A a1 = applicationContext.getBean("a",A.class);
    	a1.setSuffix("2");
    	System.out.println("A: " + a1.getString());
    	System.out.println("A: " + a.getString());
    }
    
    @Test public void testB() {
    	System.out.println("B: " + b.getString());
    }
    
    @Test public void testMap() {
    	System.out.println("Map retrieved from props.xyz is " + props);
    	System.out.println("liquibaseP.changelog is " + liquibaseP.getChangeLog());
    	System.out.println("liquibaseP.user is " + liquibaseP.getUser());
    }
}
