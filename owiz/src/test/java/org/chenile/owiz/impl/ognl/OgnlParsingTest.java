package org.chenile.owiz.impl.ognl;

import java.util.ArrayList;
import java.util.List;

import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;

import junit.framework.TestCase;

public class OgnlParsingTest extends TestCase{
	private OrchExecutor<FindFulfillmentTypeContext> oe;
	
	public void setUp() throws Exception{
		XmlOrchConfigurator<FindFulfillmentTypeContext> xoc = new XmlOrchConfigurator<FindFulfillmentTypeContext>();
		xoc.setFilename("org/chenile/owiz/impl/ognl/ft.xml");
		OrchExecutorImpl<FindFulfillmentTypeContext> oeimpl = new OrchExecutorImpl<FindFulfillmentTypeContext>();
		oeimpl.setOrchConfigurator(xoc);
		oe = oeimpl;
		System.err.println(xoc.toXml());
	}
	
	public void testFT1() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("ELECTRONICS");
		fftc.setWarehouse("LONDON");
		oe.execute(fftc);
		assertEquals("FT1", fftc.getFulfillmentType());
	}
	
	public void testFT11() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("COMMUNICATION");
		fftc.setWarehouse("LONDON");
		oe.execute(fftc);
		assertEquals("FT1", fftc.getFulfillmentType());
	}
	
	public void testFT12() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("ELECTRONICS");
		fftc.setWarehouse("LONDON");
		fftc.setSku("sku9");
		oe.execute(fftc);
		assertEquals("FT3", fftc.getFulfillmentType());
	}
	
	public void testFT2() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("HOME");
		fftc.setWarehouse("LONDON");
		oe.execute(fftc);
		assertEquals("FT2", fftc.getFulfillmentType());
	}
	
	public void testFT3() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("HOME");
		fftc.setWarehouse("HONG KONG");
		oe.execute(fftc);
		assertEquals("FT3", fftc.getFulfillmentType());
	}
	
	public void testFT4() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("HOME");
		fftc.setWarehouse("MAINLAND");
		List<String> categories = new ArrayList<String>();
		categories.add("HOME");
		categories.add("ELECTRONICS");
		fftc.setCategories(categories);
		oe.execute(fftc);
		assertEquals("FT4", fftc.getFulfillmentType());
	}
	
	public void testFT41() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("HOME");
		fftc.setWarehouse("MAINLAND");
		List<String> categories = new ArrayList<String>();
		categories.add("ELECTRONICS");
		fftc.setCategories(categories);
		oe.execute(fftc);
		assertEquals("FT3", fftc.getFulfillmentType());
	}
	
	public void testFT8() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setSku("sku1");
		fftc.setWarehouse("MAINLAND"); // should not alter result.
		
		oe.execute(fftc);
		assertEquals("FT8", fftc.getFulfillmentType());
	}
	
	public void testFT9() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setSku("sku3");
		fftc.setWarehouse("MAINLAND"); // should not alter result.
		
		oe.execute(fftc);
		assertEquals("FT9", fftc.getFulfillmentType());
	}
	
	public void testFT10() throws Exception{
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setSku("sku6");
		fftc.setWarehouse("MAINLAND"); // should not alter result.
		
		oe.execute(fftc);
		assertEquals("FT10", fftc.getFulfillmentType());
	}
	
	public void testFT110() throws Exception {
		FindFulfillmentTypeContext fftc = new FindFulfillmentTypeContext();
		fftc.setCategory("cat1");
		fftc.setWarehouse("MAINLAND"); // should not alter result.
		
		oe.execute(fftc);
		assertEquals("FT11", fftc.getFulfillmentType());
	}
	
}
