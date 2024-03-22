/**
 * 
 */
package org.chenile.swagger.init;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.chenile.base.response.GenericResponse;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.HTTPMethod;
import org.chenile.core.model.OperationDefinition;
import org.chenile.swagger.model.SwaggerOptions;
import org.chenile.swagger.util.ChenileDocketHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

/**
 * @author Deepak N
 *
 */
public class ChenileDocketInit {
	
	@Value("${chenile.swagger.version:1.0}")
	private String version;

	@Value("${chenile.swagger.termsUrl:}")
	private String termsUrl;

	@Value("${chenile.swagger.license:}")
	private String license;

	@Value("${chenile.swagger.licenseUrl:}")
	private String licenseUrl;

	@Value("${chenile.swagger.contactName:}")
	private String contactName;

	@Value("${chenile.swagger.contactUrl:}")
	private String contactUrl;

	@Value("${chenile.swagger.contactEmail:}")
	private String contactEmail;
	
	private static final String ALL = "All";
	private static final String DEFAULT_DOCKET = "defaultDocket";
	private static final String API_TITLE = "Chenile Swagger Definitions.";
	private static final String API_DESCRIPTION = "API's available via chenile.";
	
	@Autowired @Qualifier("applicationContext") private ConfigurableApplicationContext context;
	@Autowired @Qualifier("chenileServiceConfiguration") private ChenileConfiguration configuration;
	@Autowired @Qualifier("cachingOperationNameGenerator") private CachingOperationNameGenerator nameGenerator;
	@Autowired private TypeResolver typeResolver;
	private SwaggerOptions swaggerOptions;
	
	public ChenileDocketInit(SwaggerOptions swaggerOptions) {
		this.swaggerOptions = swaggerOptions;
	}
	
	@PostConstruct
	public void init() {
		final SingletonBeanRegistry beanRegistry = context.getBeanFactory();
		beanRegistry.registerSingleton(DEFAULT_DOCKET, defaultDocket());

		int loopIndex = 0;
		
		final Map<String, ChenileServiceDefinition> definitions = configuration.getServices();
		for (final ChenileServiceDefinition s : definitions.values()) {
			final List<OperationDefinition> operationList = s.getOperations();
			final String name = s.getId();
			final String serviceName = name + "_" + (loopIndex++) + "_docket_bean_" + new Random().nextLong();
			final String tagDescription = "Number of available operations: " + operationList.size();
			final Set<String> urlSet = new HashSet<>();
			for (final OperationDefinition opDef : operationList) {
				final HTTPMethod httpMethod = opDef.getHttpMethod();
				final String url = opDef.getUrl();
				if (null != httpMethod && null != url && url.length() > 0) {
					final int index = url.indexOf('/', 1);
					final String baseUrl = 0 < index ? url.substring(0, index) : url;
					final StringBuilder urlBuilder = new StringBuilder(baseUrl).append(".*");
					urlSet.add(urlBuilder.toString());
				}
			}
	
			if (0 != urlSet.size()) {
				final Docket docket = docketDefinition(name, tagDescription, urlSet);
				beanRegistry.registerSingleton(serviceName, docket);
				createApiListScanner(beanRegistry, s);
			}
		}		
	}
	
	private Docket docketDefinition(final String name, final String tagDescription, final Set<String> urlSet) {
		/**
		 * Make sure the ID of the service.json is unique, else the last encountered
		 * with the same ID will be picked by chenile. The same ID will be used to group
		 * in the swagger.
		 */
		final ApiSelectorBuilder selectorBuilder = createApiSelectorBuilder(name);
		/**
		 * Adding all the paths present in the particular service.json to the the docket
		 * group.
		 */
		final Predicate<String> urlPredicates = ChenileDocketHelper.generatePaths(urlSet);
		if (null != urlPredicates) {
			selectorBuilder.paths(urlPredicates);
		}

		final Tag tag = new Tag(name, tagDescription);
		final Docket docket = createDocket(selectorBuilder);
		
		docket.tags(tag);
		
		return docket;
	}
	
	private void createApiListScanner(final SingletonBeanRegistry beanRegistry, final ChenileServiceDefinition serviceDefinition) {
		final ChenileApiListScanner cals = new ChenileApiListScanner(nameGenerator, serviceDefinition, swaggerOptions);
		beanRegistry.registerSingleton("apiListScanner_" + serviceDefinition.getName() + "_" + new Random().nextLong(), cals);
	}
	
	private Docket createDocket(final ApiSelectorBuilder selectorBuilder) {
		final Docket docket = selectorBuilder.build();
		addDocketParameters(docket);
		return docket;
	}
	
	private void addDocketParameters(final Docket docket) {
		docket.globalResponseMessage(RequestMethod.GET, swaggerOptions.getResponseMessageList());
		docket.globalResponseMessage(RequestMethod.POST, swaggerOptions.getResponseMessageList());
		docket.globalResponseMessage(RequestMethod.PUT, swaggerOptions.getResponseMessageList());
		docket.globalResponseMessage(RequestMethod.DELETE, swaggerOptions.getResponseMessageList());
		docket.additionalModels(typeResolver.resolve(GenericResponse.class));
		docket.useDefaultResponseMessages(true);
	}
	
	private ApiSelectorBuilder createApiSelectorBuilder(final String groupName) {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.groupName(groupName).select();
	}
	
	private Docket defaultDocket() {
		final ApiSelectorBuilder selectorBuilder = createApiSelectorBuilder(ALL);
		selectorBuilder.paths(ChenileDocketHelper.otherPaths());
		return createDocket(selectorBuilder);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(API_TITLE).description(API_DESCRIPTION)
				.termsOfServiceUrl(termsUrl).contact(new Contact(contactName, contactUrl, contactEmail))
				.license(license).licenseUrl(licenseUrl).version(version).build();
	}
	
}
