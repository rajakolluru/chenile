package org.chenile.http.test.service;

public class JsonData {

    private String name;
    private String id;

    public JsonData(){

    }

    public JsonData(String id,String name){
        this.id=id;
        this.name=name;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
