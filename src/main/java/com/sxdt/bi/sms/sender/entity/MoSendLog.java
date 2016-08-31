package com.sxdt.bi.sms.sender.entity;

import java.util.Date;

/**
 * 发送消息结果实体
 * @author ysm
 * @date 2016-8-29 下午05:20:25
 */
public class MoSendLog {
	private String request;
	private String response;
	private Date requestTime;
	private Date responseTime;
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	public Date getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}
}
