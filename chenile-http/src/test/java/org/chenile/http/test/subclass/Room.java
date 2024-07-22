package org.chenile.http.test.subclass;

public class Room {
    public float height;
    public float length;
    public float width;
    public Room(float l, float w, float h){
        length = l; width = w; height = h;
    }
    public Room(){}
    public Capacity addCapacity(Capacity capacity){
        capacity.volume = height*length*width;
        return capacity;
    }
}
