package org.chenile.http.test.subclass;

/**
 * Keeps track of total capacity. Delegates to the respective capacity calculators
 * to add the capacity for different types of vehicles
 */
public class CapacityService {
    public Capacity addCapacity(Vehicle vehicle){
        Capacity capacity = new Capacity();
        vehicle.addCapacity(capacity);
        return capacity;
    }
}
