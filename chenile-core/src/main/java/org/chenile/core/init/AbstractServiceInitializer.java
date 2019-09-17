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

    public void registerService(String name,String jsonPath) throws IOException {
        serviceConfiguration.setService(name,getServiceDefinition(jsonPath));
    }
    
    protected void registerService(String jsonPath) throws IOException{
    	ChenileServiceDefinition csd = getServiceDefinition(jsonPath);
    	String id = csd.getId();
    	serviceConfiguration.setService(id, csd);
    }
    
    protected void registerService(Resource resource) throws IOException{
    	ChenileServiceDefinition csd = getServiceDefinition(resource.getInputStream());
    	String id = csd.getId();
    	serviceConfiguration.setService(id, csd);
    }

    private ChenileServiceDefinition getServiceDefinition(String path) throws IOException {
        return getServiceDefinition(getClass().getClassLoader().getResourceAsStream(path));
    }
    
    @SuppressWarnings("unchecked")
	private ChenileServiceDefinition getServiceDefinition(InputStream is) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ChenileServiceDefinition csd = om.readValue(is, ChenileServiceDefinition.class);
        csd.setModuleName(serviceConfiguration.getModuleName());
        if (csd.getInterceptorComponentNames() != null ) {
        	csd.setInterceptorCommands(initInterceptors(csd.getInterceptorComponentNames()));
        }
        if(csd.getBodyTypeSelectorComponentName() != null) {
        	csd.setBodyTypeSelector((Command<ChenileExchange>)applicationContext.getBean(csd.getBodyTypeSelectorComponentName()));
        }
        for (OperationDefinition od: csd.getOperations() ) {
        	if (od.getInterceptorComponentNames() != null ) {
            	od.setInterceptorCommands(initInterceptors(od.getInterceptorComponentNames()));
            }
        	if(od.getBodyTypeSelectorComponentName() != null) {
            	od.setBodyTypeSelector((Command<ChenileExchange>)applicationContext.getBean(od.getBodyTypeSelectorComponentName()));
            }
        }       
        validate(csd);
        return csd;
    } 
    
    private void validate(ChenileServiceDefinition csd) {
		try {
			csd.setServiceReference(applicationContext.getBean(csd.getName()));
		} catch (Exception e) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),"Service " + csd.getName() + " is not configured in spring",e);
		}
		for (OperationDefinition od: csd.getOperations()) {
			validate(csd, od);
		}
    }
    
    private void validate(ChenileServiceDefinition csd,OperationDefinition od) {
    	List<Class<?>> paramTypes = new ArrayList<Class<?>>();
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
    		paramTypes.add(pd.getParamClass());
    	}
    	Class<?>[] parameterTypes = new Class<?>[paramTypes.size()];
    	try {
    		Method method = csd.getServiceReference().getClass().getMethod(od.getMethodName(), 
    			paramTypes.toArray(parameterTypes));
    		od.setMethod(method);
    	}catch(NoSuchMethodException e) {
    		throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),"Operation " + csd.getId() + "." + od.getName() + " is not found. Did you define the paramClass properly?",e);
    	}
    	
    }

    private List<Command<?>> initInterceptors(List<String> interceptorCommandNames) {
    	List<Command<?>> commands = new ArrayList<Command<?>>();
		for (String compName: interceptorCommandNames) {
			Command<?> command = applicationContext.getBean(compName,Command.class);
			commands.add(command);
		}
		return commands;
	}
    
    

	@Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
