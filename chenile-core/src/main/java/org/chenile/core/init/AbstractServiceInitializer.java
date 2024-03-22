package org.chenile.core.init;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
			if(csd.getMockName() != null && csd.getMockName().length() > 0) {
				csd.setMockServiceReference(applicationContext.getBean(csd.getMockName()));
			}
			if(csd.getHealthCheckerName() != null && csd.getHealthCheckerName().length() > 0) {
				Object bean = applicationContext.getBean(csd.getHealthCheckerName());
				if (! (bean instanceof HealthChecker)) {
					throw new ServerException(503,new Object[] {csd.getName()});
				}
				csd.setHealthChecker((HealthChecker)bean);
			}
		} catch (Exception e) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),"Service " + csd.getName() + " is not configured in spring",e);
		}
		for (OperationDefinition od: csd.getOperations()) {
			validate(csd, od);
		}
    }
    
    private void validate(ChenileServiceDefinition csd,OperationDefinition od) {
    	
    	if (od.getInput() != null && od.getInput().equals(ChenileExchange.class) && od.getParams().size() > 1) {
    		throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),"Chenile Exchange must be the only param accepted");
    	}
    	for (ParamDefinition pd: od.getParams()) {
    		if (pd.getType() == HttpBindingType.BODY) {
    			if (od.getInput() == null && od.getBodyTypeSelectorComponentName() == null) {
    				throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),csd.getId() + "." + od.getName() + "." + pd.getName() + " specifies type as body but operation does not specify input or body type selector"); 
    			}
    			if (pd.getParamClass() == null) {
    				System.err.println("setting the param class for " + csd.getId() + "." + od.getName() + " to " + od.getInput());
    				pd.setParamClass(od.getInput());
    			}
    		}
    		if (pd.getParamClass() == null) {
    			System.err.println("Setting the param class for " + csd.getId() + "." + od.getName() + " to String");
    			pd.setParamClass(String.class); 
    		}  		
    	}
    	// find if method exists in the service. 
    	// cache the method within OperationDefinition so it does not need to be recomputed.
    	Method method = MethodUtils.computeMethod(csd.getId(), csd.getServiceReference(), od);
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
    
	@Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
