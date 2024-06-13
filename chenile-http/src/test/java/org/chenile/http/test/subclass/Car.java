package org.chenile.http.test.subclass;

public class Car extends Vehicle{
    // for type  = 'car'
    public int numPassengers;
    public Car(){}
    public Car(String id, int numPassengers){
        this.type = "car";
        this.numPassengers = numPassengers;
        this.id = id;
    }
    @Override
    public Capacity addCapacity(Capacity capacity) {
        capacity.numPassengers += numPassengers;
        return capacity;
    }
}
