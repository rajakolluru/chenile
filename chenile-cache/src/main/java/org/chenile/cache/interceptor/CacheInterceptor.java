package org.chenile.cache.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.chenile.cache.model.CacheKey;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.OperationDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import com.chenile.cache.Cacheable;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;

/**
 * Caches an operation return value using the service name, operation name and the parameters
 * that were passed to the operation. 
 * This is applicable only to operations that subscribe to caching.
 * @author Raja Shankar Kolluru
 *
 */
public class CacheInterceptor extends BaseChenileInterceptor implements Serializable{
	private static final long serialVersionUID = 5426626774012030375L;
	@Autowired HazelcastInstance hazelcastInstance;
	
	@Override
	public void execute(ChenileExchange exchange) throws Exception {	
		if(bypassInterception(exchange)) {
			doContinue(exchange);
			return;
		}
		CacheKey key = generate(exchange);
		ReplicatedMap<CacheKey,Object> cache = getCache(exchange);
		
		if (cache.containsKey(key)) {
			exchange.setResponse(cache.get(key));
			return;
		}
		doContinue(exchange);
		if (exchange.getException() != null) return; // don't cache it it erred out
		cache.put(key, exchange.getResponse());
	}
	
	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
		OperationDefinition od = exchange.getOperationDefinition();
		if(od.getCacheId() == null) {
			return true;
		}
		return false;
	}

	private ReplicatedMap<CacheKey,Object> getCache(ChenileExchange exchange) {
		String name = exchange.getOperationDefinition().getCacheId();
		return hazelcastInstance.getReplicatedMap(name);
	}
	
	private CacheKey generate(ChenileExchange exchange) {
		CacheKey key = new CacheKey();
		List<Object> apiInvocation = new ArrayList<>();
		// Treat cacheable objects specially
		for (Object o: exchange.getApiInvocation()) {
			if (o instanceof Cacheable) {
				apiInvocation.add(((Cacheable)o).cacheKey());
			}else {
				apiInvocation.add(o);
			}
		}
		key.apiInvocation = apiInvocation;
		key.serviceName = exchange.getServiceDefinition().getId();
		key.opName = exchange.getOperationDefinition().getName();
		return key;
	}
}
