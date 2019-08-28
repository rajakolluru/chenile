package org.chenile.ognl;

import java.util.Map;

import org.chenile.ognl.MapAccessor;
import org.chenile.owiz.impl.BaseContext;

import junit.framework.TestCase;
import ognl.Ognl;
import ognl.OgnlRuntime;

public class MapAccessorTest extends TestCase {
	
	public void setUp(){
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}
	
	public void testIt() throws Exception {
		BaseContext baseContext = new BaseContext();
		baseContext.setModule("module1");
		baseContext.put("zzz", "some value");
		
		assertEquals("module1",Ognl.getValue("module", baseContext));
		assertEquals("module2",Ognl.getValue("module = 'module2',module", baseContext));
		assertNull(baseContext.get("module")); // must not introduce a new key called module since module exists as a bean property.
		assertEquals("module2",baseContext.getModule());
		assertEquals("some value",Ognl.getValue("zzz", baseContext));
		assertEquals("foo",Ognl.getValue("abc='foo',abc",baseContext));		
		assertEquals("foo",baseContext.get("abc")); // a new key called abc must now be introduced.
	}

}
