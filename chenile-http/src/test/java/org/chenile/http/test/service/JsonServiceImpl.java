package org.chenile.http.test.service;

import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;
public class JsonServiceImpl implements JsonService{
    // this is used to test the event implementation.
    public static String data;
    @Override
    public JsonData getOne(String id) {
        return new JsonData(id,"Hello");
    }

    @Override
    public JsonData save(JsonData jsonData) {
        data = jsonData.getName();
        return jsonData;
    }
    
    
	@Override
    public JsonData throwException(JsonData jsonData) {
    	throw new ServerException(jsonData.getErrorNum(), jsonData.getExceptionMessage());
    }

	@Override
	public JsonData throwWarning(JsonData jsonData) {
		ResponseMessage rm = new ResponseMessage();
        rm.setCode(200);
		rm.setSeverity(ErrorType.WARN);
		rm.setSubErrorCode(jsonData.getErrorNum());
		rm.setDescription(jsonData.getExceptionMessage());
		jsonData.addWarningMessage(rm);
		return jsonData;
	}

    @Override
    public JsonData throwMultipleErrorsInException(JsonData jsonData) {
        ServerException serverException = new ServerException
                (jsonData.getErrorNum(), jsonData.getExceptionMessage());
        ResponseMessage r = new ResponseMessage();
        r.setCode(501);
        r.setSeverity(ErrorType.ERROR);
        r.setSubErrorCode(jsonData.getErrorNum());
        r.setDescription(jsonData.getExceptionMessage());
        serverException.addError(r);
        throw serverException;
    }
    @Override
    public JsonData ping(JsonData jsonData){ return jsonData;}
}
