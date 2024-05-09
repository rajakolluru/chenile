package org.chenile.mqtt.test.service;

import jakarta.servlet.http.HttpServletRequest;
import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.mqtt.model.ChenileMqtt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@ChenileMqtt
@ChenileController(value = "testService", serviceName = "testService")
public class TestController extends ControllerSupport{
	@PostMapping("/f/{num3}")
	ResponseEntity<GenericResponse<Map<String, Object>>> f(HttpServletRequest request,
														   @PathVariable("num3") int num3,
														  @RequestBody Payload payload){
		return process("f",request,num3,payload);
	}
}
