package org.chenile.configuration.controller;

import java.util.Map;

import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This sets up a query service. This can be used by pointing this service to
 * a database that can be configured in  application.yml.
 */
@RestController
@ChenileController(value = "chenileMybatisQuery", serviceName = "searchService")
public class QueryController extends ControllerSupport{
	@PostMapping("/q/{queryName}")
	// @InterceptedBy("securityInterceptor")
	 public ResponseEntity<GenericResponse<SearchResponse>> search(
			 HttpServletRequest request, 
			 @PathVariable String queryName,
			 @RequestBody SearchRequest<Map<String, Object>> searchRequest) {
		 return process("search",request,queryName,searchRequest);
	 }
}
