package com.sxdt.bi.sms.sender.db;

/**
 * sql配置管理：以下是测试配置，上线根据实际情况修改
 * @author ysm
 * @date 2016-8-29 下午05:47:13
 */
public interface SQL {
	/**
	 * 查询待发短信sql语句
	 */
	String GET_MESSAGE="select number,message from sms_table where status=? order by pk limit ?";
	
	/**
	 * 更新待发短信状态
	 */
	String UPDATE_MESSAGE="update sms_table set status=?,updateTime=? where pk=?";
	/**
	 * 保存发送日志
	 */
	String SAVE_SENDLOG="insert into sms_send_log(requestData,responseData,requestTime,responseTime) values(?,?,?,?)";
}
