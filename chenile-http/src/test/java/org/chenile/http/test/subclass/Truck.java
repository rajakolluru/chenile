package org.chenile.http.test.subclass;

public class Truck extends Vehicle{
    // for type  = 'truck'
    public int carryingCapacityInKgs;
    public Truck(){}
    public Truck(String id, int carryingCapacityInKgs){
        this.type = "truck";
        this.id = id;
        this.carryingCapacityInKgs = carryingCapacityInKgs;
    }
    @Override
    public Capacity addCapacity(Capacity capacity) {
        capacity.weightCarryingCapacityInKgs += carryingCapacityInKgs;
        return capacity;
    }
}
