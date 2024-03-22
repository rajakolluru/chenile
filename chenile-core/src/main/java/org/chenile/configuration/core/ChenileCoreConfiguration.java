package org.chenile.configuration.core;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.context.EventLog;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.event.EventLogger;
import org.chenile.core.event.EventProcessor;
import org.chenile.core.init.ChenileEventInitializer;
import org.chenile.core.init.ChenileEventSubscribersInitializer;
import org.chenile.core.init.ChenileServiceInitializer;
import org.chenile.core.init.ChenileTrajectoryInitializer;
import org.chenile.core.interceptors.ChenileExceptionHandler;
import org.chenile.core.interceptors.ConstructApiInvocation;
import org.chenile.core.interceptors.ConstructServiceReference;
import org.chenile.core.interceptors.GenericResponseBuilder;
import org.chenile.core.interceptors.ServiceInvoker;
import org.chenile.core.interceptors.interpolations.ExceptionHandlerInterpolation;
import org.chenile.core.interceptors.interpolations.OperationSpecificProcessorsInterpolation;
import org.chenile.core.interceptors.interpolations.PostProcessorsInterpolation;
import org.chenile.core.interceptors.interpolations.PreProcessorsInterpolation;
import org.chenile.core.interceptors.interpolations.ServiceSpecificProcessorsInterpolation;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.service.ChenileInfoService;
import org.chenile.core.service.ChenileInfoServiceImpl;
import org.chenile.core.service.InfoHealthChecker;
import org.chenile.core.transform.TransformationClassSelector;
import org.chenile.core.transform.Transformer;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.Chain;
import org.chenile.owiz.impl.FilterChain;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileCoreConfiguration {
	
	@Value("${chenile.service.json.package}")
	private String chenileServiceJsonResources;
	
	@Value("${chenile.event.json.package}")
	private Resource[] chenileEventJsonResources;
	
	@Value("${chenile.interceptors.path}")
	private String chenileInterceptorsPath;
	
	@Value("${chenile.trajectory.json.package:}")
	private String chenileTrajectoryJsonResources;
	
	@Value("${chenile.module.name}")
	private String moduleName;
	
	@Value("${chenile.pre.processors}")
	private String preProcessors;
	
	@Value("${chenile.post.processors}")
	private String postProcessors;
	
	@Value("${chenile.exception.handler:chenileExceptionHandler}")
	private String exceptionHandlerName;
	
	@Value("${chenile.event.logger:eventLogger}")
	private String eventLoggerName;
	
	@Value("${chenile.trajectory.header.name:chenile-trajectory-id}")
	private String trajectoryHeaderName;
	
	
	Resource[] toResources(String resourceList) throws IOException{
		PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
		
		List<Resource> resources = new ArrayList<>();
		if (resourceList == null || resourceList.length()==0)
			return new Resource[] {};
		for(String resourcePattern: resourceList.split(",")) {
			Resource[] r = resourceLoader.getResources(resourcePattern);
			resources.addAll(Arrays.asList(r));
		}
		return resources.toArray(new Resource[] {});
	}
	
	@Autowired EventLogger eventLogger;
	
	@Autowired ApplicationContext applicationContext;
	
    @Bean
    public ChenileConfiguration chenileServiceConfiguration(){
        ChenileConfiguration configuration = new ChenileConfiguration(moduleName,applicationContext);
        configuration.addPreProcessors(preProcessors);
        configuration.addPostProcessors(postProcessors);
        configuration.setChenileExceptionHandlerName(exceptionHandlerName);
        configuration.setEventLoggerName(eventLoggerName);
        return configuration;
    }
    
    @Bean
    public ChenileServiceInitializer chenileServiceInitializer()throws IOException {
    	return new ChenileServiceInitializer(toResources(chenileServiceJsonResources));
    }
    
    @Bean
    public ChenileEventInitializer chenileEventInitializer() {
    	return new ChenileEventInitializer(chenileEventJsonResources);
    }
    
    @Bean
    public ChenileTrajectoryInitializer chenileTrajectoryInitializer()throws IOException {
    	return new ChenileTrajectoryInitializer(toResources(chenileTrajectoryJsonResources));
    }
    
    @Bean
    public ChenileEventSubscribersInitializer chenileEventSubscribersInitializer() {
    	return new ChenileEventSubscribersInitializer();
    }
    
    @Bean 
    public ChenileEntryPoint chenileEntryPoint() {
    	return new ChenileEntryPoint();
    }
    
    @Bean ConstructApiInvocation constructApiInvocation() {
    	return new ConstructApiInvocation();
    }
    
    @Bean
    public ServiceInvoker serviceInvoker() {
    	return new ServiceInvoker();
    }
    
    @Bean
    public ConstructServiceReference constructServiceReference() {
    	return new ConstructServiceReference(trajectoryHeaderName);
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
		return new EventProcessor();
	}
	
	@Bean public FilterChain<ChenileExchange> chenileInterceptorChain() {
		return new FilterChain<ChenileExchange>();
	}

	@Bean public ChenileExchangeBuilder chenileExchangeBuilder() {
		return new ChenileExchangeBuilder();
	}
	
	@Bean public ChenileExceptionHandler chenileExceptionHandler() {
		return new ChenileExceptionHandler();
	}
	

	@Bean public ChenileInfoService infoService() {
		return new ChenileInfoServiceImpl();
	}
	
	@Bean public InfoHealthChecker infoHealthChecker() {
		return new InfoHealthChecker();
	}
	
	// All interpolation commands below
	@Bean public Command<ChenileExchange> exceptionHandlerInterpolation(){
		return new ExceptionHandlerInterpolation(); 
	}
	
	@Bean public Command<ChenileExchange> preProcessorsInterpolation(){
		return new PreProcessorsInterpolation(); 
	}
	
	@Bean public Command<ChenileExchange> postProcessorsInterpolation(){
		return new PostProcessorsInterpolation(); 
	}
	
	@Bean public Command<ChenileExchange> operationSpecificProcessorsInterpolation(){
		return new OperationSpecificProcessorsInterpolation(); 
	}
	
	@Bean public Command<ChenileExchange> serviceSpecificProcessorsInterpolation(){
		return new ServiceSpecificProcessorsInterpolation(); 
	}
	
	@Bean public EventLogger eventLogger() {
		return new EventLogger() {
			
			@Override
			public void log(EventLog eventLog) {
				// do nothing				
			}
		};
	}
	
	@Bean public GenericResponseBuilder genericResponseBuilder() {
		return new GenericResponseBuilder();
	}
	

}
