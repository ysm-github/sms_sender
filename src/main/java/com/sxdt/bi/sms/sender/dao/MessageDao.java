package com.sxdt.bi.sms.sender.dao;

import java.sql.SQLException;
import java.util.List;

import com.sxdt.bi.sms.sender.entity.MoMessage;
import com.sxdt.bi.sms.sender.entity.MoSendLog;

/**
 * 消息处理接口
 * @author ysm
 * @date 2016-8-29 下午05:29:16
 */
public interface MessageDao {
	/**
	 * 获取待发消息
	 * @param num 单次获取的记录数
	 * @return
	 */
	public List<MoMessage> getMoMessage(int num);
	/**
	 * 更新单个消息状态
	 * @param moMsg 待发送消息
	 */
	public void updateMoMessage(MoMessage moMsg)throws SQLException;
	/**
	 * 批量更新消息状态
	 * @param moMsgList 待发送消息列表
	 */
	public void batchUpdateMoMessage(List<MoMessage> moMsgList);
	/**
	 * 保存短信发送结果
	 * @param log 短信发送结果
	 */
	public void saveSendLog(MoSendLog log)throws SQLException;
}
