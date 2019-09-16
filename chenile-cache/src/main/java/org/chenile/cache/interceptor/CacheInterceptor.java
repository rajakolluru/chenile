package org.chenile.cache.interceptor;

import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.OperationDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Caches an operation return value using the service name, operation name and the parameters
 * that were passed to the operation. 
 * This is applicable only to operations that subscribe to caching.
 * @author Raja Shankar Kolluru
 *
 */
public class CacheInterceptor extends BaseChenileInterceptor{

	@Autowired HazelcastInstance hazelcastInstance;
	
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		OperationDefinition od = exchange.getOperationDefinition();
		if(od.getCacheId() == null) {
			doContinue(exchange);
			return;
		}
		CacheKey key = generate(exchange);
		IMap<CacheKey,Object> cache = getCache(exchange);
		if (cache.containsKey(key)) {
			exchange.setResponse(cache.get(key));
			return;
		}
		doContinue(exchange);
		cache.put(key, exchange.getResponse());
	}
	
	private IMap<CacheKey,Object> getCache(ChenileExchange exchange) {
		String name = exchange.getOperationDefinition().getCacheId();
		return hazelcastInstance.getMap(name);
	}
	
	private CacheKey generate(ChenileExchange exchange) {
		CacheKey key = new CacheKey();
		key.apiInvocation = exchange.getApiInvocation();
		key.serviceName = exchange.getServiceDefinition().getId();
		key.opName = exchange.getOperationDefinition().getName();
		return key;
	}

	private class CacheKey {
		public List<Object> apiInvocation;
		public String serviceName;
		public String opName;
		@Override			
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((apiInvocation == null) ? 0 : apiInvocation.hashCode());
			result = prime * result + ((opName == null) ? 0 : opName.hashCode());
			result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (apiInvocation == null) {
				if (other.apiInvocation != null)
					return false;
			} else if (!apiInvocation.equals(other.apiInvocation))
				return false;
			if (opName == null) {
				if (other.opName != null)
					return false;
			} else if (!opName.equals(other.opName))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			return true;
		}
		private CacheInterceptor getEnclosingInstance() {
			return CacheInterceptor.this;
		}
		
	}
}
