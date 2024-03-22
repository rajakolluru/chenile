/**
 * 
 */
package org.chenile.swagger.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.HTTPMethod;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.MimeType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.swagger.model.Header;
import org.chenile.swagger.model.SwaggerOptions;
import org.springframework.http.HttpMethod;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

/**
 * @author Deepak N
 *
 */

public class ChenileApiListScanner implements ApiListingScannerPlugin  {
	
	private CachingOperationNameGenerator operationNames;
	private List<OperationDefinition> operationDefinitons;
	private ChenileServiceDefinition chenileServiceDefinition;
	private List<Header> headers;
	private Set<ResponseMessage> responseMessageSet; 
	private SwaggerOptions swaggerOptions;
	
	private enum ParamTypes {
		BODY ("body"), QUERY ("query"), HEADER ("header"), PATH ("path");
		
		private String type;
		ParamTypes(final String type) {
			this.type = type;
		}
		
		public String getType() {return this.type;}
	}
	
	public ChenileApiListScanner(CachingOperationNameGenerator operationNames, ChenileServiceDefinition chenileServiceDefinition) {
		this.chenileServiceDefinition = chenileServiceDefinition;
		this.operationDefinitons = null == chenileServiceDefinition ? Collections.emptyList() : chenileServiceDefinition.getOperations();
		this.operationNames = operationNames;
	}
	
	public ChenileApiListScanner(CachingOperationNameGenerator operationNames, 
			ChenileServiceDefinition chenileServiceDefinition, 
			SwaggerOptions swaggerOptions) {
		this(operationNames, chenileServiceDefinition);
		this.swaggerOptions = swaggerOptions;
		this.headers = swaggerOptions.getHeaders();
		this.responseMessageSet = new HashSet<>(this.swaggerOptions.getResponseMessageList());
	}
	
	@Override
	public boolean supports(DocumentationType delimiter) {
		return DocumentationType.SWAGGER_2.equals(delimiter);
	}

	@Override
	public List<ApiDescription> apply(DocumentationContext context) {
		final Set<String> tagsSet = new HashSet<>(1);
		final List<ApiDescription> descriptionList = new ArrayList<>(this.operationDefinitons.size());
		final String name = chenileServiceDefinition.getId();
		tagsSet.add(name);
		for (final OperationDefinition od : this.operationDefinitons) {
			final HTTPMethod httpMethod = od.getHttpMethod();
			final String url = od.getUrl();
			/**
			 * Allow only if it has exposed an URL path.
			 * Chenile controller definitions contain scheduler/events definitions too.
			 */
			if (null != httpMethod && null != url && url.length() > 0) {
				final OperationBuilder operationBuilder = createOperationBuilder(od);
				operationBuilder.tags(tagsSet);
				
				final Operation operation = operationBuilder.build();
				final ApiDescription apiDescription = createApiDescription(od.getName(), od.getUrl(), od.getDescription(), Collections.singletonList(operation), false);
				descriptionList.add(apiDescription);
			}
		}
		return descriptionList;
	}
	
	private OperationBuilder createOperationBuilder(final OperationDefinition def) {
		OperationBuilder operationBuilder = new OperationBuilder(operationNames);
		operationBuilder.uniqueId(def.getName());
		operationBuilder.method(HttpMethod.valueOf(def.getHttpMethod().name().toUpperCase()));
		operationBuilder.produces(getMimeTypeSet(def, true));
		operationBuilder.consumes(getMimeTypeSet(def, false));
		operationBuilder.notes(def.getDescription());
//		operationBuilder.responseModel(GenericResponse.class);
		operationBuilder.responseMessages(this.responseMessageSet);
			
		operationBuilder.parameters(createParameterList(def));
		return operationBuilder;
	}
	
	private List<Parameter> createParameterList(final OperationDefinition def) {
		final List<ParamDefinition> pdList = def.getParams();
		final List<Parameter> parameterList = new ArrayList<>(pdList.size());
		for (final ParamDefinition pd : pdList) {
			final ParameterBuilder parameterBuilder = createParameterBuilder(pd, def.getInput());
			if (HttpBindingType.BODY.equals(pd.getType())) {
//				parameterBuilder.allowableValues(allowableValues)
			}
			
			final Parameter parameter = parameterBuilder.build();
			parameterList.add(parameter);
		}
		for (Header header : headers) {
			final ParamDefinition pd = new ParamDefinition();
			pd.setName(header.getName());
			pd.setType(HttpBindingType.HEADERS);
			pd.setDescription(header.getDescription());
			
			final ParameterBuilder parameterBuilder = createParameterBuilder(pd, def.getInput());
			parameterBuilder.required(header.isRequired());
			if (HttpBindingType.BODY.equals(pd.getType())) {
//				parameterBuilder.allowableValues(allowableValues)
			}
			
			final Parameter parameter = parameterBuilder.build();
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	private ParameterBuilder createParameterBuilder(final ParamDefinition def, Class<?> input) {
		Class<?> pdClass = def.getClass();
		if (null == pdClass) {
			pdClass = String.class;
		}
		final String name = def.getName();
		final ParameterBuilder parameterBuilder = new ParameterBuilder()
                .description(def.getDescription())
                .type(new TypeResolver().resolve(pdClass))
                .modelRef(new ModelRef(pdClass.getSimpleName()))
                .name(name)
//                .parameterType("query")
                .parameterAccess(name);
//                .required(true)
//                .modelRef(new ModelRef("string"));
		final HttpBindingType pdType = def.getType();
		String requestModel = "string";
		parameterBuilder.modelRef(new ModelRef(requestModel));
//		if (HttpBindingType.BODY.equals(def.getType())) {
//			input = null == input ? String.class : input;
//			ObjectMapper mapper = new ObjectMapper();
//			try {
//				requestModel = mapper.writeValueAsString(input.newInstance());
//			} catch (JsonProcessingException | InstantiationException | IllegalAccessException e) {
//				e.printStackTrace();
//			}
//			TypeBindings tb = TypeBindings.emptyBindings();
//			ResolvedType rt = new ResolvedObjectType(input, tb, null, Collections.emptyList());
//			parameterBuilder.type(rt);
//		}
		final ParamTypes paramType = getParameterType(pdType);
		parameterBuilder.parameterType(paramType.getType());
		
		return parameterBuilder;
	}
	
	private ParamTypes getParameterType(final HttpBindingType bindingType) {
		ParamTypes type = ParamTypes.QUERY;
		switch (bindingType) {
		case BODY:
			type = ParamTypes.BODY;
			break;
			
		case HEADER:
			type = ParamTypes.PATH;
//			type = ParamTypes.HEADER;
			break;
			
//		case HEADERS:
//			type = ParamTypes.HEADER;
//			break;
			
//		case PATH:
//			type = ParamTypes.HEADER;
//			break;
			
		default:
			type = ParamTypes.HEADER;
			break;
		}
		return type;
	}
	
	private Set<String> getMimeTypeSet(final OperationDefinition def, final boolean isProduces) {
		final Set<String> mimeTypeSet = new HashSet<>(1);
		final MimeType mimeType = isProduces ? def.getProduces() : def.getConsumes();
		final String mimeTypeString = (null == mimeType) ? MimeType.JSON.toString() : mimeType.toString();
		mimeTypeSet.add(mimeTypeString);
		return mimeTypeSet;
	}
	
	private ApiDescription createApiDescription(final String name, final String url, final String description, final List<Operation> operationList, final boolean isHidden) {
		final ApiDescription apiDescription = new ApiDescription(name, url, description, operationList, isHidden);
		return apiDescription;
	}
	
	
}
