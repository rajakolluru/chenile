package org.chenile.cache.interceptor;

import java.io.Serializable;

import org.chenile.cache.model.CacheKey;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.OperationDefinition;
import org.springframework.beans.factory.annotation.Autowired;

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
		OperationDefinition od = exchange.getOperationDefinition();
		if(od.getCacheId() == null) {
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
		cache.put(key, exchange.getResponse());
	}
	
	private ReplicatedMap<CacheKey,Object> getCache(ChenileExchange exchange) {
		String name = exchange.getOperationDefinition().getCacheId();
		return hazelcastInstance.getReplicatedMap(name);
	}
	
	private CacheKey generate(ChenileExchange exchange) {
		CacheKey key = new CacheKey();
		key.apiInvocation = exchange.getApiInvocation();
		key.serviceName = exchange.getServiceDefinition().getId();
		key.opName = exchange.getOperationDefinition().getName();
		return key;
	}
}
