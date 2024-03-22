package org.chenile.stm.test;

import junit.framework.TestCase;

import org.chenile.stm.impl.ComponentPropertiesHelper;
import org.chenile.stm.model.BaseStateEntity;
import org.chenile.stm.ognl.OgnlScriptingStrategy;

public class TestComponentPropertiesHelper  extends TestCase{
	BaseStateEntity flowContext = new BaseStateEntity();
	ComponentPropertiesHelper componentPropertiesHelper;
	public void setUp(){
		Customer customer = new Customer();
		customer.setId("1234");
		customer.setName("Johnson");
		flowContext.put("customer",customer);
		componentPropertiesHelper = new ComponentPropertiesHelper();
		componentPropertiesHelper.setScriptingStrategy(new OgnlScriptingStrategy());
	}
	
	public void testTransform() throws Exception{
		String s = "Mr. ${customer.name} has an Id of ${customer.id}";
		String s1 = "${customer.name} has an Id of ${customer.id}";
		String s2= "${customer.name}";
		String s3 = "The customer";
		assertEquals("Mr. Johnson has an Id of 1234",componentPropertiesHelper.transform(s, flowContext));
		assertEquals("Johnson has an Id of 1234",componentPropertiesHelper.transform(s1, flowContext));
		assertEquals("Johnson",componentPropertiesHelper.transform(s2, flowContext));
		assertEquals("The customer",componentPropertiesHelper.transform(s3, flowContext));
	}
	
	public static class Customer {
		private String id;
		private String name;
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
		
	}
}
