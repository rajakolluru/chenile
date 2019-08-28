package org.chenile.http.test.service;

public class JsonServiceImpl implements JsonService {


    @Override
    public JsonData getOne(String id) {

        JsonData j=new JsonData();
        j.setName("Hello");
        j.setId(id);
        return j;
    }

    @Override
    public JsonData save(JsonData jsonData) {
        return jsonData;
    }
}
