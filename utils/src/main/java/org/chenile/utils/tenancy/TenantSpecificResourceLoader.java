package org.chenile.utils.tenancy;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.chenile.base.exception.ServerException;
import org.chenile.utils.str.StrSubstitutor;


/**
 * A class that looks for a specific resource (using {@link #tenantSpecificPath}) from the class path.
 * If the tenant specific resource is not found then a generic path ({@link #genericPath} is used.
 * The resource is then opened and the name and URL are returned by calling {@link #obtainFileName(String, String)}
 * and {@link #obtainURL(String, String)}
 * Variable %{name} and %{tenantId} can be used for finding the name of the resource in both the generic and tenant 
 * specific paths.
 * 
 * 
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class TenantSpecificResourceLoader {
	
	public String delimiter = "%";
	protected String tenantSpecificPath;
	protected String genericPath;
	
	public TenantSpecificResourceLoader(String tenantSpecificPath, String genericPath) {
		this.tenantSpecificPath = tenantSpecificPath;
		this.genericPath = genericPath;
	}

	protected Map<Key,CachedValue> templateStore = new HashMap<Key, CachedValue>();
	protected static final String GENERIC_TENANT_NAME = "__generic__";

	public URL obtainURL(String name, String tenantId) {
		CachedValue value = obtainValue(name,tenantId);
		return value.url;
	}
	
	public String obtainFileName(String name,String tenantId) {
		CachedValue value = obtainValue(name,tenantId);
		return value.fileName;
	}
	
	protected CachedValue obtainValue(String name, String tenantId) {
		Key key = new Key();
		key.name = name;
		key.tenantId = tenantId;
		if (templateStore.containsKey(key)) {
			CachedValue value = templateStore.get(key);
			return value;
		}else {
			try {
				CachedValue value = lookup(key);
				templateStore.put(key,value);
				return value;
			}catch(Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}			
		}
	}

	protected CachedValue lookup(Key key) throws Exception {
		Map<String,String> valueMap = new HashMap<String, String>();
		valueMap.put("tenantId", key.tenantId);
		valueMap.put("name", key.name);
		// first see if we can find a specific file for the tenant
		String filename = StrSubstitutor.replaceNamedKeysInTemplate(tenantSpecificPath, valueMap,delimiter);
		URL res = this.getClass().getClassLoader().getResource(filename);
		if (res == null) {
			// try with the generic key to see if the template exists.
			Key genericKey = new Key();
			genericKey.name = key.name;
			genericKey.tenantId = GENERIC_TENANT_NAME;
			
			CachedValue genericValue = templateStore.get(genericKey);
			if (genericValue != null) return genericValue;
			
			filename = StrSubstitutor.replaceNamedKeysInTemplate(genericPath, valueMap,delimiter);
			res = this.getClass().getClassLoader().getResource(filename);
			if (res == null)
				throw new ServerException(601, 
						"Class " + getClass().getName() + ": Unable to find a default template for " + key.name);
			genericValue = populateValue(filename,res);
			templateStore.put(genericKey, genericValue);
			templateStore.put(key,genericValue);
			return genericValue;
		} 
		CachedValue value = populateValue(filename,res);
		templateStore.put(key, value);
		return value;
	}
	
	/**
	 * Override this to store cached values that have information specific to the particular sub class 
	 * By default the Cached Value stores URL and filename. But the specific sub class might choose to
	 * cache additional resources. 
	 * @param filename the filename to look for
	 * @param url - URL
	 * @return the cached value
	 * @throws Exception if there is a problem
	 */
	protected CachedValue populateValue(String filename, URL url) throws Exception{
		return new CachedValue(url,filename);
	}

}