package org.chenile.query.test.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.GenericResponse;
import org.chenile.base.response.ResponseMessage;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This test case demonstrates a multi level retrieve. 
 * Assume we have a location service that gives details about a geographical location. The 
 * service is structured in such a way that people can request for information about specific 
 * locations such as Syracuse,NY,US, North America  or Bangalore, Karnataka, India, Asia
 * It also has the ability to give location information at a coarse level such as China, Asia.
 * This is the supported structure:
 * asia
 *   india
 *      karnataka
 *        bangalore
 *      tamilNadu
 *        chennai
 *   china:
 *  
 * northAmerica
 *   us
 *     newYork
 *        nyc
 *        syracuse
 *     california
 *  canada
 *  
 *  In this case, the http servlet request can contain requests for specific leaf nodes (as shown above)
 *  It can also contain a query that spans across multiple leaf nodes. The query service will integrate all
 *  the queries execute them in parallel and return the results.
 *  
 *  
 * @author Raja Shankar Kolluru
 *
 */
@RunWith(SpringRunner.class)
// @ContextConfiguration(classes={SpringTestConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = SpringTestConfig.class)
public class TestQueryService {

	@Autowired private ChenileEntryPoint chenileEntryPoint;
	@Autowired private ChenileExchangeBuilder chenileExchangeBuilder;
	private ObjectMapper objectMapper;
	{
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	private ChenileExchange makeExchange(String serviceName,String operationName) {
		return chenileExchangeBuilder.makeExchange(serviceName, operationName,null);
	}
	
	/**
	 * Request for all projections i.e.for a query that spans all nodes.
	 * Make sure that the results are returned for all the nodes
	 * @throws Exception
	 */
	@Test
	public void testAllProjections() throws Exception{
		ChenileExchange exchange = makeExchange("queryService","query");
		String s = objectMapper.writeValueAsString(buildRequest());
		exchange.setBody(s);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> genericResponse = (GenericResponse<?>)exchange.getResponse();
		validateResponse(castToMap(genericResponse.getData(),"Cannot cast the response to a map"));
	}
	
	private void validateResponse(Map<String,Object> response) {
		assertEquals("2 children must exist for root element",2,response.size());
		Map<String,Object> asiaResponse = castToMap(response.get("asia"),"Cannot cast asia to map");
		assertEquals("2 children must exist for asia element",2,asiaResponse.size());
		Map<String,Object> northAmericaResponse = castToMap(response.get("northAmerica"),"Cannot cast northAmericaResponse to map");
		assertEquals("2 children must exist for northAmericaResponse element",2,northAmericaResponse.size());
		validateAsiaResponse(asiaResponse);
		validateNorthAmericaResponse(northAmericaResponse);
	}
	
	private void validateAsiaResponse(Map<String,Object> asiaResponse) {
		assertEquals("2 children must exist for asiaResponse element",2,asiaResponse.size());
		Map<String,Object> indiaResponse = castToMap(asiaResponse.get("india"),"Cannot cast indiaResponse to map");
		assertEquals("2 children must exist for indiaResponse element",2,indiaResponse.size());
		Map<String,Object> karnatakaResponse = castToMap(indiaResponse.get("karnataka"),"Cannot cast karnatakaResponse to map");
		Map<String,Object> tamilNaduResponse = castToMap(indiaResponse.get("tamilNadu"),"Cannot cast tamilNaduResponse to map");
		SearchResponse bangaloreResponse = castToSearchResponse(karnatakaResponse.get("bangalore"),"Cannot cast bangaloreResponse to Search Response");
		assertSearchResponseContainsTag(bangaloreResponse,"bangalore");
		SearchResponse chennaiResponse = castToSearchResponse(tamilNaduResponse.get("chennai"),"Cannot cast chennaiResponse to Search Response");
		assertSearchResponseContainsTag(chennaiResponse,"chennai");
		SearchResponse chinaResponse = castToSearchResponse(asiaResponse.get("china"),"Cannot cast chinaResponse to Search Response");
		assertSearchResponseContainsTag(chinaResponse,"china");
	}
	
	private void validateNorthAmericaResponse(Map<String,Object> northAmericaResponse) {
		assertEquals("2 children must exist for asiaResponse element",2,northAmericaResponse.size());
		Map<String,Object> usResponse = castToMap(northAmericaResponse.get("us"),"Cannot cast usResponse to map");
		assertEquals("2 children must exist for usResponse element",2,usResponse.size());
		Map<String,Object> newYorkResponse = castToMap(usResponse.get("newYork"),"Cannot cast newYorkResponse to map");
		SearchResponse californiaResponse = castToSearchResponse(usResponse.get("california"),"Cannot cast californiaResponse to Search Response");
		assertSearchResponseContainsTag(californiaResponse,"california");
		SearchResponse nycResponse = castToSearchResponse(newYorkResponse.get("nyc"),"Cannot cast nycResponse to Search Response");
		assertSearchResponseContainsTag(nycResponse,"nyc");
		SearchResponse syracuseResponse = castToSearchResponse(newYorkResponse.get("syracuse"),"Cannot cast syracuseResponse to Search Response");
		assertSearchResponseContainsTag(syracuseResponse,"syracuse");
		SearchResponse canadaResponse = castToSearchResponse(northAmericaResponse.get("canada"),"Cannot cast canadaResponse to Search Response");
		assertSearchResponseContainsTag(canadaResponse,"canada");
	}
	
	private void assertSearchResponseContainsTag(SearchResponse s, String tag) {
		ResponseRow rr = s.getData();
		assertEquals(tag,rr.getRow());
	}
	
	private Map<String,Object> castToMap(Object o,String message) {
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>) o;
			return map;
		}catch(ClassCastException cce) {
			fail(message);
			return null;
		}
	}
	
	private SearchResponse castToSearchResponse(Object o, String message) {
		try {
			SearchResponse sr = (SearchResponse) o;
			return sr;
		}catch(ClassCastException cce) {
			fail(message);
			return null;
		}
	}

	private Map<String,Object> buildRequest(){
		Map<String,Object> request = new HashMap<>();
		Map<String,Object> northAmericaRequest = new HashMap<>();
				
		northAmericaRequest.put("us",buildUsRequest());
		SearchRequest<MockFilter> canadaRequest = new SearchRequest<MockFilter>();
		northAmericaRequest.put("canada",canadaRequest);
		request.put("northAmerica",northAmericaRequest);
		
		Map<String,Object> asiaRequest = new HashMap<>();
		asiaRequest.put("india",buildIndiaRequest());
		SearchRequest<MockFilter> chinaRequest = new SearchRequest<MockFilter>();
		asiaRequest.put("china",chinaRequest);
		request.put("asia",asiaRequest);
		
		
		return request;
	}
	
	Map<String,Object> buildUsRequest(){
		Map<String,Object> usRequest = new HashMap<>();
		SearchRequest<MockFilter> californiaRequest = new SearchRequest<MockFilter>();
		usRequest.put("california",californiaRequest);
		
		Map<String,Object> newYorkRequest = new HashMap<>();
		SearchRequest<MockFilter> nycRequest = new SearchRequest<MockFilter>();
		newYorkRequest.put("nyc", nycRequest);
		SearchRequest<MockFilter> syracuseRequest = new SearchRequest<MockFilter>();
		newYorkRequest.put("syracuse", syracuseRequest);
		
		usRequest.put("newYork",newYorkRequest);
		return usRequest;
	}
	
	Map<String,Object> buildIndiaRequest(){
		Map<String,Object> indiaRequest = new HashMap<>();
		
		Map<String,Object> karnatakaRequest = new HashMap<>();
		SearchRequest<MockFilter> bangaloreRequest = new SearchRequest<MockFilter>();
		karnatakaRequest.put("bangalore", bangaloreRequest);
		indiaRequest.put("karnataka",karnatakaRequest);
		
		Map<String,Object> tamilNaduRequest = new HashMap<>();
		SearchRequest<MockFilter> chennaiRequest = new SearchRequest<MockFilter>();
		tamilNaduRequest.put("chennai", chennaiRequest);
		indiaRequest.put("tamilNadu",tamilNaduRequest);
		return indiaRequest;
	}
	
	/**
	 * Request for all projections i.e.for a query that spans all nodes.
	 * However, make sure that there is an exception thrown for Syracuse,NY, US, North America
	 * 
	 * Make sure that the results are returned for all the nodes. The Syracuse node will throw an exception
	 * Hence this exception will be sent back as a warning 
	 * @throws Exception
	 */
	@Test
	public void testOneExceptionProjection() throws Exception{
		String exceptionMessage = "SyracuseException";
		ChenileExchange exchange = makeExchange("queryService","query");
		Map<String,Object> request = buildRequest();
		insertExceptionForSyracuse(request,exceptionMessage);
		String s = objectMapper.writeValueAsString(request);
		exchange.setBody(s);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> genericResponse = (GenericResponse<?>)exchange.getResponse();
		validateExceptionResponse(castToMap(genericResponse.getData(),"Cannot cast response to Map"),exceptionMessage,
				exchange);
	}
	
	// Check if syracuse does not have a response
	@SuppressWarnings("unchecked")
	private void validateExceptionResponse(Map<String, Object> response, String expectedMessage, 
			ChenileExchange exchange) {
		Map<String,Object> naMap = (Map<String,Object>)response.get("northAmerica");
		Map<String,Object> usMap = (Map<String,Object>)naMap.get("us");
		Map<String,Object> newYorkMap = (Map<String,Object>)usMap.get("newYork");
		assertNull("Syracuse response is expected to be null",newYorkMap.get("syracuse"));
		
		// check if the expected message is there at the top level
		List<ResponseMessage> warnings = exchange.getResponseMessages();
		assertEquals(1,warnings.size());
		assertEquals(expectedMessage,warnings.get(0).getDescription());
	}
	

	// Mutate the Syracuse request to throw exception
	@SuppressWarnings("unchecked")
	private void insertExceptionForSyracuse(Map<String, Object> request, String exceptionMessage) {
		Map<String,Object> naMap = (Map<String,Object>)request.get("northAmerica");
		Map<String,Object> usMap = (Map<String,Object>)naMap.get("us");
		Map<String,Object> newYorkMap = (Map<String,Object>)usMap.get("newYork");
		SearchRequest<MockFilter> syracuseRequest = (SearchRequest<MockFilter>)newYorkMap.get("syracuse");
		MockFilter filter = new MockFilter();
		filter.exceptionMessage = exceptionMessage;
		syracuseRequest.setFilters(filter);
	}
	
	/**
	 * If an invalid key is given at the root level, the query service will throw an exception that indicates this
	 * @throws Exception
	 */
	@Test
	public void testRootLevelInvalidProjection() throws Exception{
		String invalidKey = "invalidKey";
		ChenileExchange exchange = makeExchange("queryService","query");
		Map<String,Object> request = buildRequest();
		request.put(invalidKey, "someStuff");
		String s = objectMapper.writeValueAsString(request);
		exchange.setBody(s);
		chenileEntryPoint.execute(exchange);
		assertTrue(exchange.getException() instanceof ErrorNumException);
		ErrorNumException ene = (ErrorNumException)exchange.getException();
		assertEquals("Error code returned is not expected error code",101,ene.getSubErrorNum());
		compareObjects(new Object[] {invalidKey,"root"},ene.getParams());
	}
	
	/**
	 * If an invalid key is inserted at a sub level (not at the root). In this case we send 
	 * invalid key at the asia level. The whole asia node will not execute and will throw a warning
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSubLevelInvalidProjection() throws Exception{
		String invalidKey = "invalidKey";
		ChenileExchange exchange = makeExchange("queryService","query");
		Map<String,Object> request = buildRequest();
		Map<String,Object> asiaRequest = (Map<String,Object>)request.get("asia");
		asiaRequest.put(invalidKey, "someStuff"); // put an invalid key in the asia request 
		String s = objectMapper.writeValueAsString(request);
		exchange.setBody(s);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> genericResponse = (GenericResponse<?>)exchange.getResponse();
		Map<String,Object> response = castToMap(genericResponse.getData(),"Response not castable to map");
		Map<String,Object> northAmericaResponse = castToMap(response.get("northAmerica"),
				"North America Response not castable to map");
		validateNorthAmericaResponse(northAmericaResponse); // must get a valid north america response since that part
		// of the request is just fine
		Object asiaResponse = response.get("asia");
		assertNull("asia response must be null",asiaResponse);
		// look for warnings in the response
		List<ResponseMessage> warnings = exchange.getResponseMessages();
		// There should be an invalid key warning
		ResponseMessage rm = warnings.get(0);
		assertEquals("Warning code returned is not expected warning code",101,rm.getSubErrorCode());
		
		compareObjects(new Object[] {invalidKey,"root.asia"},rm.getParams());
	}
	
	private void compareObjects(Object[] expected, Object[] actual) {
		assertEquals("Warning params must match expected",expected.length,actual.length);
		for (int i = 0; i < expected.length;i++) {
			Object eo = expected[i]; Object ao = actual[i];
			assertEquals("Warning param must match expected",eo,ao);
		}
	}
	
	/**
	 * Request for partial projections - the query will not ask for "asia" information.
	 * Make sure that the results are returned for all the nodes for northAmerica. But the 
	 * asia information is not retrieved.
	 * @throws Exception
	 */
	@Test
	public void testPartialProjection() throws Exception{
		ChenileExchange exchange = makeExchange("queryService","query");
		Map<String,Object> req = buildRequest();
		req.remove("asia");
		String s = objectMapper.writeValueAsString(req);
		exchange.setBody(s);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> genericResponse = (GenericResponse<?>)exchange.getResponse();
		Map<String,Object> response = castToMap(genericResponse.getData(),"Response not castable to map");
		Map<String,Object> northAmericaResponse = castToMap(response.get("northAmerica"),
				"North America Response not castable to map");
		validateNorthAmericaResponse(northAmericaResponse); // must get a valid north america response since that part
		// of the request is just fine
		assertNull("asia response must be null since we did not query for asia request", response.get("asia"));
	}
	
	
}
