package org.chenile.security.test.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.annotation.InterceptedBy;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.security.SecurityConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "testService", serviceName = "testService")
public class TestController extends ControllerSupport{
	@GetMapping("/test")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authorities = {"order.read"})
	ResponseEntity<GenericResponse<Map<String, Object>>> test(HttpServletRequest request){
		return process("test",request);
	}

	@GetMapping("/test1/{option}")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authoritiesSupplier = "supplier")
	ResponseEntity<GenericResponse<Map<String, Object>>> test1(HttpServletRequest request,
									   @PathVariable("option") String option){
		return process("test1",request,option);
	}

	@GetMapping("/test2/{option}")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authoritiesSupplier = "authoritiesSupplier")
	ResponseEntity<GenericResponse<Map<String, Object>>> test2(HttpServletRequest request,
									   @PathVariable("option") String option){
		return process("test2",request,option);
	}
}
