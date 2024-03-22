package org.chenile.query.test.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.handler.ControllerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "query5Service", serviceName = "queryService")
public class QueryController extends ControllerSupport{
	
	@PostMapping("/query5")
	public ResponseEntity<GenericResponse<Map<String,Object>>> query(
			HttpServletRequest httpServletRequest,
			@RequestHeader Map<String,Object> headers,
			@RequestBody Map<String,Object> request){
		return process("query",httpServletRequest,headers,request);	
	}

}
