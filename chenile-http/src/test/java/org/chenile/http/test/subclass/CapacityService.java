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

    public Capacity addCapacityGeneric(String type, Object object){
            Capacity capacity = new Capacity();
            switch(type){
                case "room":
                    Room room = (Room) object;
                    room.addCapacity(capacity);
                    return capacity;
                case "vehicle":
                    Vehicle vehicle = (Vehicle) object;
                    vehicle.addCapacity(capacity);
                    return capacity;
                default:
                    return null;
            }
    }
}
