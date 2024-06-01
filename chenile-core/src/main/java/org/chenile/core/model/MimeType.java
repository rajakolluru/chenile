package org.chenile.core.model;

/**
 * Defines the MIME type supported by the particular service.
 */
public enum MimeType {
	JSON("application/json") , TEXT("text/plain"), HTML("text/html"), PDF("application/pdf");
	private String type;
	private MimeType(String mimeType){
		this.type = mimeType;
	}
	public String toString(){
		return type;
	}
}