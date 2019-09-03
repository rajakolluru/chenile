package org.chenile.configuration.core;


import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.entrypoint.ChenileInterceptorChain;
import org.chenile.core.event.EventLogger;
import org.chenile.core.event.EventProcessor;
import org.chenile.core.init.ChenileEventInitializer;
import org.chenile.core.init.ChenileEventSubscribersInitializer;
import org.chenile.core.init.ChenileServiceInitializer;
import org.chenile.core.interceptors.ServiceInvoker;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.transform.TransformationClassSelector;
import org.chenile.core.transform.Transformer;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileCoreConfiguration {
	
	@Value("${chenile.service.json.package}")
	private Resource[] chenileServiceJsonResources;
	
	@Value("${chenile.event.json.package}")
	private Resource[] chenileEventJsonResources;
	
	@Value("${chenile.interceptors.path}")
	private String chenileInterceptorsPath;
	
	@Value("${chenile.module.name}")
	private String moduleName;
	
	@Value("${chenile.pre.processors}")
	private String preProcessors;
	
	@Value("${chenile.post.processors}")
	private String postProcessors;
	
	/**
	 * This needs to be installed by a consuming module since there is no default way to 
	 * log events in Chenile.
	 */
	@Autowired EventLogger eventLogger;
	
	@Autowired ApplicationContext applicationContext;
	
    @Bean
    public ChenileConfiguration chenileServiceConfiguration(){
        ChenileConfiguration configuration = new ChenileConfiguration(moduleName,applicationContext);
        configuration.addPreProcessors(preProcessors);
        configuration.addPostProcessors(postProcessors);
        return configuration;
    }
    
    @Bean
    public ChenileServiceInitializer chenileServiceInitializer() {
    	return new ChenileServiceInitializer(chenileServiceJsonResources);
    }
    
    @Bean
    public ChenileEventInitializer chenileEventInitializer() {
    	return new ChenileEventInitializer(chenileEventJsonResources);
    }
    
    @Bean
    public ChenileEventSubscribersInitializer chenileEventSubscribersInitializer() {
    	return new ChenileEventSubscribersInitializer();
    }
    
    @Bean 
    public ChenileEntryPoint chenileEntryPoint() {
    	return new ChenileEntryPoint();
    }
    
    @Bean
    public ServiceInvoker serviceInvoker() {
    	return new ServiceInvoker();
    }
    
    @Bean
    public OrchExecutor<ChenileExchange> chenileOrchExecutor() throws Exception {
    	XmlOrchConfigurator<ChenileExchange> xmlOrchConfigurator = new XmlOrchConfigurator<ChenileExchange>();
    	xmlOrchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {		
			@Override
			public Object lookup(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		xmlOrchConfigurator.setFilename(chenileInterceptorsPath);
		OrchExecutorImpl<ChenileExchange> executor = new OrchExecutorImpl<ChenileExchange>();
		executor.setOrchConfigurator(xmlOrchConfigurator);
		return executor;		
    }
    
    @Bean public Chain<ChenileExchange> chenileHighway(){
    	return new Chain<ChenileExchange>();
    }
    
    @Bean
    public ChenileInterceptorChain chenileInterceptorChain() {
    	return new ChenileInterceptorChain();
    }
    
    /* 
     * The transformation framework
     */
    @Bean public Transformer transformer() {
    	return new Transformer();
    }
    
    @Bean public TransformationClassSelector transformationClassSelector() {
    	return new TransformationClassSelector();
    }
   
	@Bean public EventProcessor chenileEventProcessor() {
		return new EventProcessor(eventLogger);
	}
	
	

	
}
