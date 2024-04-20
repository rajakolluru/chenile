package org.chenile.http.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.chenile.base.exception.ServerException;
import org.chenile.core.annotation.ChenileAnnotation;
import org.chenile.core.init.AbstractServiceInitializer;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.service.HealthChecker;
import org.chenile.core.util.MethodUtils;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.init.od.DeleteMappingProducer;
import org.chenile.http.init.od.GetMappingProducer;
import org.chenile.http.init.od.PatchMappingProducer;
import org.chenile.http.init.od.PostMappingProducer;
import org.chenile.http.init.od.PutMappingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class AnnotationChenileServiceInitializer extends AbstractServiceInitializer{
	@Autowired ApplicationContext applicationContext;
	@Autowired ChenileConfiguration chenileConfiguration;
	
	private DeleteMappingProducer deleteMappingProducer; 
	private GetMappingProducer getMappingProducer ;
	private PatchMappingProducer patchMappingProducer;
	private PostMappingProducer postMappingProducer;
	private PutMappingProducer putMappingProducer;

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws Exception {
		deleteMappingProducer = new DeleteMappingProducer(applicationContext);
		getMappingProducer = new GetMappingProducer(applicationContext);
		patchMappingProducer = new PatchMappingProducer(applicationContext);
		postMappingProducer = new PostMappingProducer(applicationContext);
		putMappingProducer = new PutMappingProducer(applicationContext);
		
		Map<String,Object> beans = applicationContext.getBeansWithAnnotation(ChenileController.class);
		
		// register all of these beans
		for(Entry<String, Object> e: beans.entrySet()) {
			Object bean = e.getValue();
			ChenileController chenileController = bean.getClass().getAnnotation(ChenileController.class);
			ChenileServiceDefinition csd = new ChenileServiceDefinition();
			csd.setModuleName(chenileConfiguration.getModuleName());
	        csd.setVersion(chenileConfiguration.getVersion());
			String id = chenileController.value();
			csd.setId(id);
			String name = chenileController.serviceName();
			if (name.isEmpty()) {
				name = "_" + id + "_";
			}
			Object serviceRef = lookup(name);
			if (serviceRef != null) {
				csd.setName(name);
				csd.setServiceReference(serviceRef);
			}else {
				throw new ServerException(506,"Service " + id + " does not have a configured service reference. Did you miss instantiating it?");
			}
			String healthCheckerName = chenileController.healthCheckerName();
			if (healthCheckerName.isEmpty())
				healthCheckerName = id + "HealthChecker";
			
			Object hcref = lookup(healthCheckerName);
			if (hcref != null) {
				csd.setHealthCheckerName(healthCheckerName);
				csd.setHealthChecker((HealthChecker)hcref);
			}
			String mockName = chenileController.mockName();
			if (mockName.isEmpty())
				mockName = id + "Mock";
			Object mockRef = lookup(mockName);
			if (mockRef != null) {
				csd.setMockName(mockName);
				csd.setMockServiceReference(mockRef);
			}
			
			csd.setOperations(new ArrayList<>());
			collectChenileAnnotations(bean,csd);
			configureOperations(bean.getClass(),csd);
			Class<?> clazz = chenileController.interfaceClass();
			if (clazz == Object.class ){
				// Interface class is not specified see if you can compute the interface class
				clazz = computeInterfaceClass(csd);
			}
			csd.setInterfaceClass(clazz);
			registerService(csd);
		}
	}

	private Class<?> computeInterfaceClass(ChenileServiceDefinition csd){
		Object service = csd.getServiceReference();
		Class<?>[] interfaces = ClassUtils.getAllInterfaces(service);
		for (Class<?> inter : interfaces ){
			boolean found = true;
			for (OperationDefinition od: csd.getOperations()) {
				Method m = MethodUtils.computeMethod(inter, od);
				if (m == null) {
					found = false;
					break;
				}
			}
			if(found) return inter;
		}
		return null;
	}
	
	protected void collectChenileAnnotations(Object controller, ChenileServiceDefinition csd) {
		Annotation[] annotations = controller.getClass().getAnnotations();
		for (Annotation annotation: annotations) {
			Class<? extends Annotation> klass = annotation.annotationType();
			if (klass.isAnnotationPresent(ChenileAnnotation.class)) {
				Map<String,Object> map = AnnotationUtils.getAnnotationAttributes(annotation);
				String n = klass.getName();
				n = n.substring(n.lastIndexOf('.')+1);
				csd.putExtension(n,map);
			}				
		}
	}

	private Object lookup(String name) {
		try {
			return applicationContext.getBean(name);	
		}catch(Throwable t) {
			return null;
		}
	}
	
	private void configureOperations(final Class<?> type, ChenileServiceDefinition csd) {
	    Class<?> klass = type;
	    while (klass != Object.class) { // need to iterate thought hierarchy in order to retrieve methods from above the current instance
	        // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
	        for (final Method method : klass.getDeclaredMethods()) {
	            if (method.isAnnotationPresent(GetMapping.class)) {
	            	getMappingProducer.produceOperationDefinition(csd, method);
	            }else if (method.isAnnotationPresent(DeleteMapping.class)) {
	            	deleteMappingProducer.produceOperationDefinition(csd, method);
	            }else if (method.isAnnotationPresent(PutMapping.class)) {
	            	putMappingProducer.produceOperationDefinition(csd, method);
	            }else if (method.isAnnotationPresent(PostMapping.class)) {
	            	postMappingProducer.produceOperationDefinition(csd, method);
	            }else if (method.isAnnotationPresent(PatchMapping.class)) {
	            	patchMappingProducer.produceOperationDefinition(csd, method);
	            }
	        }
	        // move to the upper class in the hierarchy in search for more methods
	        klass = klass.getSuperclass();
	    }
	}
}
