package org.chenile.swagger.model;

import java.io.Serializable;

public class ResponseCode implements Serializable{
	private static final long serialVersionUID = -4954476803570580096L;
	private int code;
	private String message;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
