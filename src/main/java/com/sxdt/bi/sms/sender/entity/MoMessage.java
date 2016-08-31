package com.sxdt.bi.sms.sender.entity;

/**
 * 待发送消息实体
 * @author ysm
 * @date 2016-8-29 下午05:19:48
 */
public class MoMessage {
	private String pk;
	private String mobile;
	private String message;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	
}
