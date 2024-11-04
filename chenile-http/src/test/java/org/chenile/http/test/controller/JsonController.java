package org.chenile.http.test.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.annotation.EventsSubscribedTo;
import org.chenile.http.annotation.InterceptedBy;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.http.test.service.JsonData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "jsonController", serviceName = "jsonService")
public class JsonController extends ControllerSupport{
	 @GetMapping("/c/getOne/{key}")
	 @InterceptedBy("jsonInterceptor")
	 public ResponseEntity<GenericResponse<JsonData>> getOne(
			 HttpServletRequest request, @PathVariable String key){
		return process("getOne",request,key);
	}
	 
	 @PostMapping("/c/save")
	 @InterceptedBy("jsonInterceptor")
	 @EventsSubscribedTo({"event1","event2"})
	 public ResponseEntity<GenericResponse<JsonData>> save(
			 HttpServletRequest request, @RequestBody JsonData jsonData) {
		 return process("save",request,jsonData);
	 }
	 
	 @PostMapping("/c/throw-exception") 
	 public ResponseEntity<GenericResponse<JsonData>> throwException(
			 HttpServletRequest request, @RequestBody JsonData jsonData) {
		 return process("throwException",request,jsonData);
	 }
	 
	 @PostMapping("/c/throw-warning")
	 public ResponseEntity<GenericResponse<JsonData>> throwWarning(
			 HttpServletRequest request, @RequestBody JsonData jsonData) {
		 return process("throwWarning",request,jsonData);
	 }

	@PostMapping("/c/throw-multiple-exceptions")
	public ResponseEntity<GenericResponse<JsonData>> throwMultipleErrorsInException(
			HttpServletRequest request, @RequestBody JsonData jsonData) {
		return process("throwMultipleErrorsInException",request,jsonData);
	}

	@PostMapping("/c/ping")
	@InterceptedBy({"jsonInterceptor","jsonInterceptor1"})
	public ResponseEntity<GenericResponse<JsonData>> ping(
			HttpServletRequest request, @RequestBody JsonData jsonData) {
		return process("ping",request,jsonData);
	}
}
