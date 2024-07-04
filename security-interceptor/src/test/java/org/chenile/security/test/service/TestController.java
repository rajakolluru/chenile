package org.chenile.security.test.service;

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

import java.util.Map;

@RestController
@ChenileController(value = "testService", serviceName = "testService")
public class TestController extends ControllerSupport{
	@GetMapping("/premium")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authorities = {"test.premium"})
	ResponseEntity<GenericResponse<Map<String, Object>>> premium(HttpServletRequest request){
		return process("premium",request);
	}

	@GetMapping("/selective-premium/{option}")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authoritiesSupplier = "supplier")
	ResponseEntity<GenericResponse<Map<String, Object>>> selectivelyPremium(HttpServletRequest request,
									   @PathVariable("option") String option){
		return process("selectivelyPremium",request,option);
	}

	@GetMapping("/selective-premium1/{option}")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authoritiesSupplier = "authoritiesSupplier")
	ResponseEntity<GenericResponse<Map<String, Object>>> selectivelyPremium1(HttpServletRequest request,
									   @PathVariable("option") String option){
		return process("selectivelyPremium1",request,option);
	}

	@GetMapping("/normal")
	@InterceptedBy("securityInterceptor")
	@SecurityConfig(authorities = {"test.normal"})
	ResponseEntity<GenericResponse<Map<String, Object>>> normal(HttpServletRequest request){
		return process("normal",request);
	}
}
