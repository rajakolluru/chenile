package org.chenile.owiz.impl.ognl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;

public class ExclusiveRule implements Command<FindFulfillmentTypeContext>  {

	public void execute(FindFulfillmentTypeContext context) throws Exception {
		if (context.getFulfillmentType() == null) return;
		Map<String,List<String>> excMap = getListOfExclusiveSkusForFT();
		for (String ft: excMap.keySet()){
			List<String> excSkus = excMap.get(ft);
			if (excSkus.contains(context.getSku()) && 
					context.getFulfillmentType().equalsIgnoreCase(ft))
				context.setFulfillmentType(null);
		}
	}
	
	protected  Map<String,List<String>> getListOfExclusiveSkusForFT() throws Exception {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> ft1List = Arrays.asList(new String[] {"sku9"});
		
		map.put("FT1",ft1List );
		
		return map;		
	}
	
	private static ExclusiveRule exclusiveRule = new ExclusiveRule();
	public static boolean isExcluded(FindFulfillmentTypeContext context) throws Exception{
		if (context.getFulfillmentType() == null) return true;
		exclusiveRule.execute(context);
		return (context.getFulfillmentType() == null);
	}

}
