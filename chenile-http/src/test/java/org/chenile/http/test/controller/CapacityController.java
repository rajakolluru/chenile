package org.chenile.http.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.chenile.base.response.GenericResponse;
import org.chenile.http.annotation.BodyTypeSelector;
import org.chenile.http.annotation.ChenileController;
import org.chenile.http.annotation.ChenileParamType;
import org.chenile.http.handler.ControllerSupport;
import org.chenile.http.test.subclass.Capacity;
import org.chenile.http.test.subclass.Vehicle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ChenileController(value = "capacityService", serviceName = "capacityService")
public class CapacityController extends ControllerSupport {
    @PostMapping("/add-capacity")
    @BodyTypeSelector("subclassBodyTypeSelector")
    public ResponseEntity<GenericResponse<Capacity>> addCapacity(
            HttpServletRequest request, @ChenileParamType (Vehicle.class) @RequestBody String vehicle) {
        return process(request,vehicle);
    }
    @PostMapping("/add-capacity-generic/{type}")
    @BodyTypeSelector({"roomVehicleBodyTypeSelector","subclassBodyTypeSelector"})
    public ResponseEntity<GenericResponse<Capacity>> addCapacityGeneric(
            HttpServletRequest request, @PathVariable("type") String type,
            @ChenileParamType (Object.class) @RequestBody String object) {
        return process(request,type,object);
    }
}
