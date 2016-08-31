package com.sxdt.bi.sms.sender.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sxdt.bi.sms.sender.db.DBUtil;
import com.sxdt.bi.sms.sender.db.RowCallBack;
import com.sxdt.bi.sms.sender.db.SQL;
import com.sxdt.bi.sms.sender.entity.MoMessage;
import com.sxdt.bi.sms.sender.entity.MoSendLog;
import com.sxdt.bi.sms.sender.util.DateUtil;

/**
 * 短信操作数据库操作实现类
 * @author ysm
 * @date 2016-8-29 下午05:40:56
 */
public class MessageDaoDBImpl implements MessageDao {
	private static final Log log=LogFactory.getLog(MessageDaoDBImpl.class);

	@Override
	public List<MoMessage> getMoMessage(int num) {
		String sql=SQL.GET_MESSAGE;
		List<MoMessage> msgList=
		DBUtil.doSelect( sql, new RowCallBack<MoMessage>(){

			@Override
			public List<MoMessage> doCallBack(ResultSet rs) {
				List<MoMessage> rowList=new ArrayList<MoMessage>();
				try{
				if(rs.next()){
					MoMessage msg=new MoMessage();
					msg.setMessage(rs.getString("message"));
					msg.setMobile(rs.getString("mobile"));
					rowList.add(msg);
				}
				}catch(Exception e){
					log.error("getMoMessage error!",e);
				}
				return rowList;
			}
		}, 1,num);
		return msgList;
	}
	/**
	 * 更新单个消息状态
	 * @param moMsg 待发送消息
	 */
	@Override
	public void updateMoMessage(MoMessage moMsg)throws SQLException{
		DBUtil.doUpdate(SQL.UPDATE_MESSAGE, DateUtil.now(),moMsg.getPk());
	}
	/**
	 * 批量更新消息状态
	 * @param moMsgList 待发送消息列表
	 */
	@Override
	public void batchUpdateMoMessage(List<MoMessage> moMsgList){
		throw new UnsupportedOperationException();
	}
	/**
	 * 保存短信发送结果
	 * @param log 短信发送结果
	 */
	@Override
	public void saveSendLog(MoSendLog log)throws SQLException{
		DBUtil.doInsert(SQL.SAVE_SENDLOG, log.getRequest(),log.getResponse(),log.getRequestTime(),log.getResponseTime());
	}

}
