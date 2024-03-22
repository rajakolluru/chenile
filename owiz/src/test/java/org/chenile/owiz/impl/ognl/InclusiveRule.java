package org.chenile.owiz.impl.ognl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;

public class InclusiveRule implements Command<FindFulfillmentTypeContext> {

	public void execute(FindFulfillmentTypeContext context) throws Exception {
		Map<String,List<String>> incMap = getListOfInclusiveSkusForFT();
		for (String ft: incMap.keySet()){
			List<String> incSkus = incMap.get(ft);
			if (incSkus.contains(context.getSku()))
				context.setFulfillmentType(ft);
		}
	}
	
	protected Map<String,List<String>> getListOfInclusiveSkusForFT() throws Exception {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> ft8List = Arrays.asList(new String[] {"sku1","sku2"});
		List<String> ft9List = Arrays.asList(new String[] {"sku3","sku4"});
		List<String> ft10List = Arrays.asList(new String[] {"sku5","sku6"});
		
		map.put("FT8",ft8List );
		map.put("FT9",ft9List );
		map.put("FT10",ft10List );
		
		return map;		
	}

}
