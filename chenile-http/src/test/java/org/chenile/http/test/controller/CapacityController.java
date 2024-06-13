package org.chenile.http.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.annotation.ChenileParamType;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.http.test.subclass.Capacity;
import org.chenile.http.test.subclass.Vehicle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "capacityService", serviceName = "capacityService")
public class CapacityController extends ControllerSupport {
    @PostMapping("/add-capacity")
    public ResponseEntity<GenericResponse<Capacity>> addCapacity(
            HttpServletRequest request, @ChenileParamType (Vehicle.class) @RequestBody String vehicle) {
        return process("addCapacity",request,vehicle);
    }
}
