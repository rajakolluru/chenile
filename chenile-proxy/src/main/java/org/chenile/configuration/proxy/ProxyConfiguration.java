package org.chenile.configuration.proxy;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.FilterChain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.proxy.builder.ProxyBuilder;
import org.chenile.proxy.interceptors.HttpInvoker;
import org.chenile.proxy.interceptors.LocalProxyInvoker;
import org.chenile.proxy.interceptors.ProxyTypeRouter;
import org.chenile.proxy.interceptors.ResponseBodyTypeSelector;
import org.chenile.proxy.interceptors.interpolations.OperationSpecificClientProcessorsInterpolation;
import org.chenile.proxy.interceptors.interpolations.ServiceSpecificClientProcessorsInterpolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ProxyConfiguration {
	@Autowired
	ApplicationContext applicationContext;
	@Value("${chenile.proxy.interceptors.path:org/chenile/proxy/chenile-proxy.xml}")
	private String chenileProxyInterceptorsPath;

	@Bean
	public ProxyBuilder localProxyBuilder() {
		return new ProxyBuilder();
	}

	@Bean
	public OrchExecutor<ChenileExchange> chenileProxyOrchExecutor() throws Exception {
		XmlOrchConfigurator<ChenileExchange> xmlOrchConfigurator = new XmlOrchConfigurator<ChenileExchange>();
		xmlOrchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {
			@Override
			public Object lookup(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		xmlOrchConfigurator.setFilename(chenileProxyInterceptorsPath);
		OrchExecutorImpl<ChenileExchange> executor = new OrchExecutorImpl<ChenileExchange>();
		executor.setOrchConfigurator(xmlOrchConfigurator);
		return executor;
	}

	@Bean
	public Chain<ChenileExchange> chenileProxyInterceptorChain() {
		return new FilterChain<ChenileExchange>();
	}

	@Bean
	public Chain<ChenileExchange> chenileProxyHighway() {
		return new Chain<ChenileExchange>();
	}

	@Bean
	public LocalProxyInvoker localProxyInvoker() {
		return new LocalProxyInvoker();
	}

	@Bean
	public HttpInvoker httpInvoker() {
		return new HttpInvoker();
	}

	@Bean
	public Command<ChenileExchange> operationSpecificClientProcessorsInterpolation() {
		return new OperationSpecificClientProcessorsInterpolation();
	}

	@Bean
	public Command<ChenileExchange> serviceSpecificClientProcessorsInterpolation() {
		return new ServiceSpecificClientProcessorsInterpolation();
	}

	@Bean
	public ProxyTypeRouter proxyTypeRouter() {
		return new ProxyTypeRouter();
	}

	@Bean
	ResponseBodyTypeSelector responseBodyTypeSelector() {
		return new ResponseBodyTypeSelector();
	}
	
	@Bean
	Chain<ChenileExchange> httpChain() {
		return new Chain<ChenileExchange>();
	}

}
