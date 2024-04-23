package org.chenile.proxy.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.context.HeaderCopier;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.owiz.OrchExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Creates a proxy for a chenile interface. A  proxy allows the
 * clients to avail the benefits of interception, transformation, caching etc.
 * The chain of execution includes a router that determine if a local or remote http
 * proxy must be used to invoke the actual service
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class ProxyBuilder {
	public static final String PROXYMODE = "PROXYMODE"; // this key is used to store in the proxy mode in ChenileExchange
	// Needed for test cases.
	public static final String REMOTE_URL_BASE = "REMOTE_URL_BASE";

	/**
	 * Build proxy for an interface. The headers from the current request are copied using 
	 * the headerCopier. The service name is the service which is being proxied
	 * @param <T>
	 * @param interfaceToProxy
	 * @param serviceName
	 * @param headerCopier
	 * @return
	 */
	public <T> T buildProxy(Class<T> interfaceToProxy, String serviceName, HeaderCopier headerCopier,
							String baseUrl) {
		return buildProxy(interfaceToProxy,serviceName,headerCopier,ProxyMode.COMPUTE_DYNAMICALLY,baseUrl);
	}
	/**
	 * This is used for testing purposes. For production, please make sure that 
	 * proxy mode is set to COMPUTE_DYNAMICALLY
	 * @param <T> Interface to proxy class
	 * @param interfaceToProxy  the interface that needs to be proxied
	 * @param serviceName - name of the service
	 * @param headerCopier this copies headers from the source to the target CHenile exchange
	 * @param proxyMode - can be set to always proxy local or remote for testing purposes
	 * @return the proxy that can be used in lieu of the service
	 */
	public <T> T buildProxy(Class<T> interfaceToProxy, String serviceName, HeaderCopier headerCopier,
			ProxyMode proxyMode, String baseUrl) {
		ProxyClass proxyClass = new ProxyClass(interfaceToProxy,serviceName,headerCopier,proxyMode, baseUrl);
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { interfaceToProxy }, proxyClass);
		return proxy;
	}
	
	public static enum ProxyMode {
		LOCAL, /* Always use local */
		REMOTE, /* Always use remote */
		COMPUTE_DYNAMICALLY /* Compute if it is local or remote - must be used in prod */
	}

	@Autowired @Qualifier("chenileProxyOrchExecutor") OrchExecutor<ChenileExchange> chenileProxyOrchExecutor;
	@Autowired
	private ChenileExchangeBuilder chenileExchangeBuilder;

	private class ProxyClass implements InvocationHandler {
	
		
		private String serviceName;
		private HeaderCopier headerCopier;
		private Class<?> interfaceToProxy;
		private ProxyMode proxyMode;
		private String baseUrl;

		public ProxyClass(Class<?> interfaceToProxy, String serviceName, HeaderCopier headerCopier,
				ProxyMode proxyMode, String baseUrl) {
			this.headerCopier = headerCopier;
			this.serviceName = serviceName;
			this.interfaceToProxy = interfaceToProxy;
			this.proxyMode = proxyMode;
			this.baseUrl = baseUrl;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// basically invoke the methods from this class itself if they are not declared in the interface 
			// that we are trying to proxy.
			if (!method.getDeclaringClass().equals(interfaceToProxy)){
				return method.invoke(this, args);
			}
			
			ChenileExchange exchange = chenileExchangeBuilder.makeExchange(serviceName, method.getName(), headerCopier);
			if (args != null)
				populateArgs(exchange, args);
			exchange.setHeader(PROXYMODE, proxyMode);
			exchange.setHeader(REMOTE_URL_BASE, baseUrl);
			
			chenileProxyOrchExecutor.execute(exchange);
			if (exchange.getException() != null) {
				throw exchange.getException();
			}
			return exchange.getResponse();
		}
		
		public String toString() {
			return "ProxyBuilder.Proxy." + serviceName ;
		}

		@SuppressWarnings("unchecked")
		private void populateArgs(ChenileExchange exchange, Object[] args) {
			exchange.setApiInvocation(Arrays.asList(args));
			OperationDefinition od = exchange.getOperationDefinition();
			// Size of the args and od.getParams() will be identical
			for (int index = 0; index < od.getParams().size(); index++) {
				ParamDefinition pd = od.getParams().get(index);
				Object arg = args[index];
				switch (pd.getType()) {
				case HEADER:
					exchange.setHeader(pd.getName(), arg);
					break;
				case BODY:
					exchange.setBody(arg);
					break;
				case HEADERS:
					exchange.setHeaders((Map<String, Object>)arg);
					break;
				default:
					break;
				}
			}
		}
	}

}
