package org.chenile.foo.test.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.chenile.base.response.GenericResponse;
import org.chenile.foo.Foo;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.annotation.InterceptedBy;
import org.chenile.http.handler.ControllerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "testService", serviceName = "testService", healthCheckerName = "testHealthChecker")
public class TestController extends ControllerSupport{
	@GetMapping("/test")
	@InterceptedBy("fooInterceptor")
	@Foo(message = "some_message")
	ResponseEntity<GenericResponse<Map<String, Object>>> example(HttpServletRequest request){
		return process("example",request);
	}
}
