package org.chenile.http.init.od;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.chenile.base.exception.ServerException;
import org.chenile.base.response.GenericResponse;
import org.chenile.core.annotation.ChenileAnnotation;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.init.AbstractServiceInitializer;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.HTTPMethod;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.MimeType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.http.annotation.*;
import org.chenile.owiz.Command;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class MappingProducerBase {
	
	protected ApplicationContext applicationContext;
	public MappingProducerBase(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
	protected void processChenileOperation(ChenileServiceDefinition csd,Method method, OperationDefinition od) {
		processInterceptedBy(csd,method,od);
		processBodyTypeSelector(csd,method,od);
		processEventsSubscribedTo(csd,method,od);
		collectChenileAnnotations(method,od);
		processChenileResponseCodes(method,od);
	}
	
	protected void processChenileResponseCodes(Method method, OperationDefinition od) {
		if(method.isAnnotationPresent(ChenileResponseCodes.class)) {
			ChenileResponseCodes co = method.getAnnotation(ChenileResponseCodes.class);
			od.setSuccessHttpStatus(co.success());
			od.setWarningHttpStatus(co.warning());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void processInterceptedBy(ChenileServiceDefinition csd,Method method, OperationDefinition od) {
		if(method.isAnnotationPresent(InterceptedBy.class)) {
			InterceptedBy co = method.getAnnotation(InterceptedBy.class);
			if (co.value() != null) {
				List<Command<ChenileExchange>> cmds = new ArrayList<>();
				for (String interceptorName: co.value()) {
					Command<ChenileExchange> cmd = (Command<ChenileExchange>) applicationContext.getBean(interceptorName);
					cmds.add(cmd);
				}
				od.setInterceptorComponentNames(Arrays.asList(co.value()));
				od.setInterceptorCommands(cmds);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void processBodyTypeSelector(ChenileServiceDefinition csd,Method method, OperationDefinition od) {
		if(method.isAnnotationPresent(BodyTypeSelector.class)) {
			BodyTypeSelector co = method.getAnnotation(BodyTypeSelector.class);
			if (co.value() != null) {
				od.setBodyTypeSelectorComponentNames(co.value());
				Command<ChenileExchange> bts = AbstractServiceInitializer.constructBodyTypeInterceptorsChain(co.value(),
						applicationContext);
				od.setBodyTypeSelector(bts);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void processEventsSubscribedTo(ChenileServiceDefinition csd,Method method, OperationDefinition od) {
		if(method.isAnnotationPresent(EventsSubscribedTo.class)) {
			EventsSubscribedTo est = method.getAnnotation(EventsSubscribedTo.class);
			if (est.value() != null) {
                Set<String> set = new HashSet<>(Arrays.asList(est.value()));
				od.setEventSubscribedTo(set);
			}
		}
	}

	/**
	 * The paramClass Annotation tells Chenile that the actual signature of the service's operation
	 * is different from the one that is defined in the controller.If the parameter is of type
	 * String then the Chenile Param type will override it. Else, the actual type is preserved.
	 * @param od the operation definition
	 * @param pd the param definition that might have been annotated with ChenileParamType
	 * @param param the parameter defined by the Java reflection API
	 */
	protected void processParamClassType(OperationDefinition od, ParamDefinition pd,Parameter param) {
		if(param.isAnnotationPresent(ChenileParamType.class)) {
			ChenileParamType co = param.getAnnotation(ChenileParamType.class);
			if (co.value() != null) {
				pd.setParamClass(co.value());
				Class<?> clazz = od.getInput();
				// reflect the correct param class type in the OperationDefinition as well.
				// do this only if the od.getInput() is of type String. Else leave it alone
				if (clazz.equals(String.class)){
					od.setInput(co.value());
				}
			}
		}
	}
	
	protected void collectChenileAnnotations(Method method, OperationDefinition od) {
		Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation: annotations) {
			Class<? extends Annotation> klass = annotation.annotationType();
			if (klass.isAnnotationPresent(ChenileAnnotation.class)) {
				Map<String,Object> map = AnnotationUtils.getAnnotationAttributes(annotation);
				String n = klass.getName();
				n = n.substring(n.lastIndexOf('.')+1);
				od.putExtension(n,map);
				od.putExtensionAsAnnotation(klass,annotation);
			}				
		}
	}
	
	protected void populateParams(ChenileServiceDefinition csd,Method method, OperationDefinition od){
		processChenileOperation(csd,method,od);
		
		List<ParamDefinition> paramList = new ArrayList<>();
		Parameter[] params = method.getParameters();
		int index = 0;
		Parameter param;
		for(index = 0; index < params.length;index++) {
			param = params[index];
			if (index == 0) {
				// first parameter must always be HTTPServletRequest.
				// This parameter does not need to be passed to the underlying service
				if (!param.getType().isAssignableFrom(HttpServletRequest.class)) {
					throw new ServerException(ErrorCodes.INVALID_CONTROLLER_ARGS.getSubError(),
							new Object[] {csd.getId(),method.getName()});
				}
				continue;
			}
			ParamDefinition pd = new ParamDefinition();
			pd.setName(param.getName());
			pd.setParamClass(param.getType());
			if (param.isAnnotationPresent(RequestBody.class)) {
				pd.setType(HttpBindingType.BODY);
				od.setInput(param.getType());
			}else {
				pd.setType(HttpBindingType.HEADER);
			}
			processParamClassType(od,pd,param);
			paramList.add(pd);
		}
		od.setParams(paramList);
	}
	
	protected abstract String[] url(Method method) ;
	protected abstract HTTPMethod httpMethod();
	protected abstract String[] consumes(Method method);
	protected abstract String[] produces(Method method);
	
	public void produceOperationDefinition(ChenileServiceDefinition csd,Method method) {
		OperationDefinition od = new OperationDefinition();
		od.setName(method.getName());
		String[] urls = url(method);
		String url = null;
		if (urls != null && urls.length > 0) {
			url = urls[0];
		}
		od.setHttpMethod(httpMethod());
		String[] c = consumes(method);
		if (c != null && c.length > 0 && !c[0].isEmpty()) {
			od.setConsumes(MimeType.valueOf(c[0]));
		}
		c = produces(method);
		if (c != null && c.length > 0 && !c[0].isEmpty()) {
			od.setProduces(MimeType.valueOf(c[0]));
		}
		
		od.setOutputAsParameterizedReference(findOutputType(ResolvableType.forMethodReturnType(method)));
		od.setUrl(url);
		populateParams(csd,method,od);
		csd.getOperations().add(od);
	}

	private static ParameterizedTypeReference<?> findOutputType(ResolvableType genericType){
		// output type needs to be calculated by removing the surrounding ResponseEntity and
		// GenericResponse. That is why we call getGeneric() twice to remove the two of them
		ResolvableType t = genericType.getGeneric();
		t = t.getGeneric();
		return ParameterizedTypeReference.forType(t.getType());
	}
}