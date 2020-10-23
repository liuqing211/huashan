/**
  * Copyright 2018 bejson.com 
  */
package com.kedacom.haiou.kmtool.dto.viid;

public class ResponseStatus {

    private String Id;
    private String RequestURL;
    private int StatusCode;
    private String StatusString;
    private String LocalTime;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getRequestURL() {
		return RequestURL;
	}
	public void setRequestURL(String requestURL) {
		RequestURL = requestURL;
	}
	public int getStatusCode() {
		return StatusCode;
	}
	public void setStatusCode(int statusCode) {
		StatusCode = statusCode;
	}
	public String getStatusString() {
		return StatusString;
	}
	public void setStatusString(String statusString) {
		StatusString = statusString;
	}
	public String getLocalTime() {
		return LocalTime;
	}
	public void setLocalTime(String localTime) {
		LocalTime = localTime;
	}

}