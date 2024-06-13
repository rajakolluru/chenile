package org.chenile.http.test.subclass;

public class Vehicle  {
    public String type;
    public String id; // vehicle identification number

    /**
     * does nothing. sub classes compute the correct capacity.
     * @param capacity the existing capacity
     * @return the recomputed capacity
     */
    public Capacity addCapacity(Capacity capacity){
        return capacity;
    }
}
