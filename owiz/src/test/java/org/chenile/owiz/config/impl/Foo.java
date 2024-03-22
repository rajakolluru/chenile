package org.chenile.owiz.config.impl;



import org.chenile.owiz.impl.BaseContext;

public class Foo {
	
	//private final Logger log = LoggerFactory.getLogger(Foo.class);
	
	public void abc(BaseContext context) throws Exception {
		
		//log.info("Executing abc method of Foo class");
		
		context.put("foo","abc");
	}

}
