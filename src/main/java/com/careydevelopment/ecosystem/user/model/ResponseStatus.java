package com.careydevelopment.ecosystem.user.model;

public class ResponseStatus {

	public static enum StatusCode { OK, ERROR };
	
	private StatusCode statusCode;
	private String message;
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
