package org.chenile.http.test.service;

import org.chenile.http.test.service.JsonData;

public interface JsonService {

    JsonData getOne(String id);

    JsonData save(JsonData jsonData);


}
