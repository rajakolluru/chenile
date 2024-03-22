package org.chenile.http.test.service;

import java.util.ArrayList;
import java.util.List;

import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;

public class JsonData implements WarningAware{

    private String name;
    private String id;
    private String exceptionMessage;
	private int errorNum;
	private String someSpecialHeaderValue; // this value is calculated by the interceptor.
	// so populating this field will test if there is an interceptor in between 
	// and if the interceptor has access to a header passed from request
	private List<ResponseMessage> responseMessages = new ArrayList<>();

    public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

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

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public int getErrorNum() {
		return this.errorNum;
	}

	@Override
	public List<ResponseMessage> getWarningMessages() {
		return responseMessages;
	}

	@Override
	public void addWarningMessage(ResponseMessage m) {
		responseMessages.add(m);
	}

	@Override
	public void removeAllWarnings() {
		responseMessages = new ArrayList<>();		
	}

	public String getSomeSpecialHeaderValue() {
		return someSpecialHeaderValue;
	}

	public void setSomeSpecialHeaderValue(String someSpecialHeaderValue) {
		this.someSpecialHeaderValue = someSpecialHeaderValue;
	}
}
