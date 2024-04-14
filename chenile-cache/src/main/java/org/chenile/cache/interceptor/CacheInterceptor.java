package org.chenile.cache.interceptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.chenile.cache.model.CacheKey;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
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
	@Serial
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
		List<ParamDefinition> params = exchange.getOperationDefinition().getParams();
		// Treat cacheable objects specially
		for (ParamDefinition p: params) {
			apiInvocation.add(key(exchange,p));
		}
		key.apiInvocation = apiInvocation;
		key.serviceName = exchange.getServiceDefinition().getId();
		key.opName = exchange.getOperationDefinition().getName();
		return key;
	}

	private Object key(ChenileExchange exchange, ParamDefinition p){
		HttpBindingType typ = p.getType();
		if (typ == HttpBindingType.HEADER){
			return exchange.getHeader(p.getName());
		}else if (typ == HttpBindingType.BODY){
			Object body = exchange.getBody();
			if ( body instanceof Cacheable) {
				return ((Cacheable)body).cacheKey();
			}else {
				return body;
			}
		} else if (typ == HttpBindingType.HEADERS){
			return exchange.getHeaders();
		}else if (typ == HttpBindingType.MULTI_PART){
			return exchange.getMultiPartMap().get(p.getName());
		}
		return null;
	}
}
