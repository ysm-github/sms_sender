package com.sxdt.bi.sms.sender.entity;

/**
 * 待发送消息实体
 * @author ysm
 * @date 2016-8-29 下午05:19:48
 */
public class MoMessage {
	private int mo_sid;
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
	public int getMo_sid() {
		return mo_sid;
	}
	public void setMo_sid(int mo_sid) {
		this.mo_sid = mo_sid;
	}
	
}
