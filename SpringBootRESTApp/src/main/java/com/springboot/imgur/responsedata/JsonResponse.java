package com.springboot.imgur.responsedata;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object data;

	private String messageType;

	private String messageCode;

	private String message;
	
	private String[] msgParams;
	
	private String messageKey;

	private boolean success;// To reset page on True Condition

	public JsonResponse() {
		super();
	}

	public JsonResponse(Object obj, String messageType, String messageCode, String message, boolean success) {
		super();
		this.data = obj;
		this.messageType = messageType;
		this.messageCode = messageCode;
		this.message = message;
		this.success = success;
	}
	
	public JsonResponse(Object obj, String msg, String messageKey, boolean success) {
		super();
		this.data = obj;
		this.message = msg;
		this.success = success;
		this.messageKey = messageKey;
	}
	
	public JsonResponse(Object obj, String messageKey, boolean success) {
		super();
		this.data = obj;
		this.success = success;
		this.messageKey = messageKey;
	}
	
	public JsonResponse(Object obj, boolean success) {
		super();
		this.data = obj;
		this.success = success;
	}
	
	public JsonResponse(Object obj, boolean success, String[] msgParams) {
		super();
		this.data = obj;
		this.success = success;
		this.msgParams = msgParams;
	}
	
	public JsonResponse(boolean success, String messageKey, String[] msgParams) {
		super();
		this.messageKey = messageKey;
		this.success = success;
		this.msgParams = msgParams;
	}
	
	public JsonResponse(boolean success) {
		super();
		this.success = success;
	}
	
	public JsonResponse(String messageKey, boolean success) {
		super();
		this.success = success;
		this.messageKey = messageKey;
	}

	public JsonResponse(String msg, String messageKey, boolean success) {
		this(null, msg, messageKey, success);
	}

	 

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String[] getMsgParams() {
		return msgParams;
	}

	public void setMsgParams(String[] msgParams) {
		this.msgParams = msgParams;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	
}
