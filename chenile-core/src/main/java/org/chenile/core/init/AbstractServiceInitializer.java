package org.chenile.core.init;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.core.service.HealthChecker;
import org.chenile.core.util.MethodUtils;
import org.chenile.owiz.Command;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import static org.chenile.core.errorcodes.ErrorCodes.*;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A super class to instantiate a service. This can be used to instantiate it from a file
 * or from annotation or whatever other mechanism.
 * It registers a service in {@link ChenileConfiguration}
 */
public abstract class AbstractServiceInitializer implements InitializingBean {
    @Autowired
    private ChenileConfiguration serviceConfiguration;
    @Autowired
    private ApplicationContext applicationContext;

    public abstract void init() throws Exception;
    
    protected void registerService(String jsonPath) throws IOException{
    	registerServiceDefinition(jsonPath);
    }
    
    protected void registerService(Resource resource) throws IOException{
    	registerServiceDefinition(resource.getInputStream());
    }

    private void registerServiceDefinition(String path) throws IOException {
        registerServiceDefinition(getClass().getClassLoader().getResourceAsStream(path));
    }
    
    
	private void registerServiceDefinition(InputStream is) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ChenileServiceDefinition csd = om.readValue(is, ChenileServiceDefinition.class);
        registerService(csd);
    }
    
	@SuppressWarnings("unchecked")
    protected void registerService(ChenileServiceDefinition csd) {
        csd.setModuleName(serviceConfiguration.getModuleName());
        csd.setVersion(serviceConfiguration.getVersion());
        if (csd.getInterceptorComponentNames() != null ) {
        	csd.setInterceptorCommands(initInterceptors(csd.getInterceptorComponentNames()));
        }
        if (csd.getClientInterceptorComponentNames() != null ) {
        	csd.setClientInterceptorCommands(initInterceptors(csd.getClientInterceptorComponentNames()));
        }
        if(csd.getBodyTypeSelectorComponentName() != null) {
        	csd.setBodyTypeSelector((Command<ChenileExchange>)applicationContext.getBean(csd.getBodyTypeSelectorComponentName()));
        }
        for (OperationDefinition od: csd.getOperations() ) {
        	if (od.getInterceptorComponentNames() != null ) {
            	od.setInterceptorCommands(initInterceptors(od.getInterceptorComponentNames()));
            }
        	if (od.getClientInterceptorComponentNames() != null ) {
            	od.setClientInterceptorCommands(initInterceptors(od.getClientInterceptorComponentNames()));
            }
        	if(od.getBodyTypeSelectorComponentName() != null) {
            	od.setBodyTypeSelector((Command<ChenileExchange>)applicationContext.getBean(od.getBodyTypeSelectorComponentName()));
            }
        }       
        validate(csd);
        String id = csd.getId();
    	serviceConfiguration.setService(id, csd);
    } 
    
    private void validate(ChenileServiceDefinition csd) {
		try {
			csd.setServiceReference(applicationContext.getBean(csd.getName()));
			if(csd.getMockName() != null && !csd.getMockName().isEmpty()) {
				csd.setMockServiceReference(applicationContext.getBean(csd.getMockName()));
			}
			if(csd.getHealthCheckerName() != null && !csd.getHealthCheckerName().isEmpty()) {
				Object bean = applicationContext.getBean(csd.getHealthCheckerName());
				if (! (bean instanceof HealthChecker)) {
					throw new ServerException(NOT_INSTANCE_HEALTH_CHECKER.getSubError(),new Object[] {csd.getName()});
				}
				csd.setHealthChecker((HealthChecker)bean);
			}
		} catch (Exception e) {
			throw new ServerException(NOT_CONFIGURED_IN_SPRING.getSubError(), new Object[]{csd.getName()},e);
		}
		for (OperationDefinition od: csd.getOperations()) {
			validate(csd, od);
		}
    }
    
    private void validate(ChenileServiceDefinition csd,OperationDefinition od) {
    	
    	if (od.getInput() != null && od.getInput().equals(ChenileExchange.class) && od.getParams().size() > 1) {
    		throw new ServerException(CHENILE_EXCHANGE_ONLY.getSubError(),new Object[]{});
    	}
    	for (ParamDefinition pd: od.getParams()) {
    		if (pd.getType() == HttpBindingType.BODY) {
    			if (od.getInput() == null && od.getBodyTypeSelectorComponentName() == null) {
    				throw new ServerException(MISSING_INPUT_TYPE.getSubError(), new Object[]{csd.getId(), od.getName(),pd.getName()});
    			}
    			if (pd.getParamClass() == null) {
    				pd.setParamClass(od.getInput());
    			}
    		}
    		if (pd.getParamClass() == null) {
    			pd.setParamClass(String.class); 
    		}  		
    	}
    	// find if method exists in the service. 
    	// cache the method within OperationDefinition so it does not need to be recomputed.
    	Method method = MethodUtils.computeMethod(csd.getServiceReference().getClass(), od);
		if (method == null){
			throw new ServerException(MISSING_OPERATION.getSubError(),new Object[]{csd.getId(),od.getName()});
		}
    	od.setMethod(method);    	
    }

    @SuppressWarnings("unchecked")
	private List<Command<ChenileExchange>> initInterceptors(List<String> interceptorCommandNames) {
    	List<Command<ChenileExchange>> commands = new ArrayList<Command<ChenileExchange>>();
		for (String compName: interceptorCommandNames) {
			Command<ChenileExchange> command = (Command<ChenileExchange>)applicationContext.getBean(compName);
			commands.add(command);
		}
		return commands;
	}

	private Command<ChenileExchange> constructBodyTypeInterceptorsChain(List<String> bodyTypeInterceptors){
		return null;
	}
    
	@Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
