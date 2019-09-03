package org.chenile.core.context;

import org.chenile.base.exception.ErrorNumException;

public class EventLog {
	public enum StatusEnum {
		SUCCESS, FAIL, EXCEPTION, WARNING
	}
	private String eventId;
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public StatusEnum getStatus() {
		return status;
	}
	public void setStatus(StatusEnum status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ErrorNumException getException() {
		return exception;
	}
	public void setException(ErrorNumException exception) {
		this.exception = exception;
	}
	public String getEventSubscriber() {
		return eventSubscriber;
	}
	public void setEventSubscriber(String eventSubscriber) {
		this.eventSubscriber = eventSubscriber;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	private StatusEnum status;
	private String message;
	private ErrorNumException exception;
	private String eventSubscriber;
	private String app;
}
