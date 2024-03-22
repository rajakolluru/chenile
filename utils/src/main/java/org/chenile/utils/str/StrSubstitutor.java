package org.chenile.utils.str;

import java.util.Map;
import java.util.Map.Entry;

public class StrSubstitutor {
	
	public static String replaceNamedKeysInTemplate(String template, Map<String,String> valueMap) {
		return replaceNamedKeysInTemplate(template,valueMap,"$");
	}
	
	/**
	 * Replaces a bunch of named string with their values (provided in the map) in a template string.
	 * @param template
	 * @param valueMap
	 * @return the substituted string
	 */
	public static String replaceNamedKeysInTemplate(String template, Map<String,String> valueMap, String delimiter) {
		for( Entry<String, String> entry: valueMap.entrySet()) {
			String k = delimiter + "{" + entry.getKey() + "}";
			String v = entry.getValue();
			template = template.replace(k,v);
		}		
		return template;
	}
	
	
}
