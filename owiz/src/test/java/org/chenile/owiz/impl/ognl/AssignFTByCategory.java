package org.chenile.owiz.impl.ognl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;

public class AssignFTByCategory implements Command<FindFulfillmentTypeContext> {

	public void execute(FindFulfillmentTypeContext context) throws Exception {
		Map<String,List<String>> catToFTMap = getListOfCategoriesForFT();
		for (String ft: catToFTMap.keySet()){
			List<String> incCats = catToFTMap.get(ft);
			if (incCats.contains(context.getCategory()))
				context.setFulfillmentType(ft);
		}
	}
	
	protected Map<String,List<String>> getListOfCategoriesForFT() throws Exception {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> ft11List = Arrays.asList(new String[] {"cat1","cat2"});
		List<String> ft12List = Arrays.asList(new String[] {"cat3","cat4"});
		List<String> ft13List = Arrays.asList(new String[] {"cat5","cat6"});
		
		map.put("FT11",ft11List );
		map.put("FT12",ft12List );
		map.put("FT13",ft13List );
		
		return map;		
	}

}
