package org.chenile.proxy.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.context.HeaderCopier;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Creates a local proxy for a chenile interface. A local proxy allows the
 * clients to avail the benefits of interception, transformation, caching etc.
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class ProxyBuilder {

	public <T> T buildProxy(Class<T> interfaceToProxy, String serviceName, HeaderCopier headerCopier) {
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { interfaceToProxy }, new ProxyClass(interfaceToProxy,serviceName, headerCopier));
		return proxy;
	}

	@Autowired
	private ChenileEntryPoint chenileEntryPoint;
	@Autowired
	private ChenileExchangeBuilder chenileExchangeBuilder;

	private class ProxyClass implements InvocationHandler {
		private String serviceName;
		private HeaderCopier headerCopier;
		private Class<?> interfaceToProxy;

		public ProxyClass(Class<?> interfaceToProxy, String serviceName, HeaderCopier headerCopier) {
			this.headerCopier = headerCopier;
			this.serviceName = serviceName;
			this.interfaceToProxy = interfaceToProxy;
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
			exchange.setLocalInvocation(true);
			chenileEntryPoint.execute(exchange);
			return exchange.getResponse();
		}
		
		public String toString() {
			return "ProxyBuilder.Proxy." + serviceName ;
		}

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
				}
			}
		}
	}

}
