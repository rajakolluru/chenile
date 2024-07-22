package org.chenile.http.test.subclass;

import org.chenile.http.test.TestChenileHttp;
import org.chenile.http.test.TestUtil;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test case tests if the correct subclass is used by the Chenile transformation
 * framework. The CapacityService accepts a Vehicle. However, the correct subclass of
 * Vehicle needs to be instantiated and passed to the service so that the capacity
 * can be computed correctly.<br/>
 * The correct subclass must be inferred from the JSON that was passed based on the type.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestChenileHttp.class)
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
public class TestSubclassing {
    @Autowired
    private MockMvc mvc;
    @Test
    @DisplayName("Tests if the Car sub class is used.")
    public void testCar() throws Exception {
        Car car = new Car("123",5);
        mvc.perform( MockMvcRequestBuilders
            .post("/add-capacity")
            .content(TestUtil.asJsonString(car))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.numPassengers").value(5))
            .andExpect(jsonPath("$.payload.weightCarryingCapacityInKgs").value(0));

    }

    @Test
    @DisplayName("Tests if the Truck sub class is used.")
    public void testTruck() throws Exception {
        Truck truck = new Truck("123",5);
        mvc.perform( MockMvcRequestBuilders
                        .post("/add-capacity")
                        .content(TestUtil.asJsonString(truck))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.numPassengers").value(0))
                .andExpect(jsonPath("$.payload.weightCarryingCapacityInKgs").value(5));

    }
    @Test
    @DisplayName("Test bodyTypeSelector chaining - class Car")
    public void testChaining() throws Exception {
        Car car = new Car("123",5);
        mvc.perform( MockMvcRequestBuilders
                .post("/add-capacity-generic/vehicle")
                .content(TestUtil.asJsonString(car))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.numPassengers").value(5))
                .andExpect(jsonPath("$.payload.weightCarryingCapacityInKgs").value(0));
    }

    @Test
    @DisplayName("Test bodyTypeSelector chaining - No need to use the subclass selector ")
    public void testChainingWithNoNeedForSubclassing() throws Exception {
        Room room = new Room(10,20,10) ;
        mvc.perform( MockMvcRequestBuilders
                .post("/add-capacity-generic/room")
                .content(TestUtil.asJsonString(room))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.volume").value(2000))
                .andExpect(jsonPath("$.payload.numPassengers").value(0))
                .andExpect(jsonPath("$.payload.weightCarryingCapacityInKgs").value(0));
    }
}
