package org.chenile.http.test.service;

import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;
public class JsonServiceImpl implements JsonService{

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
    
    
	@Override
    public JsonData throwException(JsonData jsonData) {
    	throw new ServerException(jsonData.getErrorNum(), jsonData.getExceptionMessage());
    }

	@Override
	public JsonData throwWarning(JsonData jsonData) {
		ResponseMessage rm = new ResponseMessage();
		rm.setSeverity(ErrorType.WARN);
		rm.setSubErrorCode(jsonData.getErrorNum());
		rm.setDescription(jsonData.getExceptionMessage());
		jsonData.addWarningMessage(rm);
		return jsonData;
	}
}
