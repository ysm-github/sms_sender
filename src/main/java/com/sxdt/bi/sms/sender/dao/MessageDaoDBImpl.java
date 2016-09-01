package com.sxdt.bi.sms.sender.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sxdt.bi.sms.sender.db.DBUtil;
import com.sxdt.bi.sms.sender.db.DataSource;
import com.sxdt.bi.sms.sender.db.RowCallBack;
import com.sxdt.bi.sms.sender.db.SQL;
import com.sxdt.bi.sms.sender.entity.MoMessage;
import com.sxdt.bi.sms.sender.entity.MoSendLog;

/**
 * 短信操作数据库操作实现类
 * @author ysm
 * @date 2016-8-29 下午05:40:56
 */
public class MessageDaoDBImpl implements MessageDao {
	private static final Log log=LogFactory.getLog(MessageDaoDBImpl.class);

	@Override
	public List<MoMessage> getMoMessage(int num) {
		String getMessageSql=SQL.GET_MESSAGE;
		String updateMessageSql = SQL.UPDATE_MESSAGE;
		Connection con = null;
		try {
			con = DataSource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			List<MoMessage> msgList=
				DBUtil.doSelect(con, getMessageSql, new RowCallBack<MoMessage>(){
					@Override
					public List<MoMessage> doCallBack(ResultSet rs) {
						List<MoMessage> rowList=new ArrayList<MoMessage>();
						try{
							while(rs.next()){
								MoMessage msg=new MoMessage();
								msg.setMo_sid(rs.getInt("mo_sid"));
								msg.setMessage(rs.getString("message"));
								msg.setMobile(rs.getString("mobile"));
								rowList.add(msg);
							}
						}catch(Exception e){
							log.error("getMoMessage error!",e);
						}
						return rowList;
					}
				}, 0,num);
			if(msgList!=null && msgList.size()>0){
				List<Object[]> params = new ArrayList<Object[]>();
				for(int i=0; i< msgList.size(); i++){
					Object[] obj = {2, msgList.get(i).getMo_sid()};
					params.add(obj);
				}
				DBUtil.batchRevise(con, updateMessageSql, params);
			}
			return msgList;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
		} finally{
			DataSource.close(con);
		}
		return null;
	}
	/**
	 * 更新单个消息状态
	 * @param moMsg 待发送消息
	 */
	@Override
	public void updateMoMessage(MoMessage moMsg)throws SQLException{
		DBUtil.doUpdate(SQL.UPDATE_MESSAGE, 1, moMsg.getMo_sid());
	}
	/**
	 * 批量更新消息状态
	 * @param moMsgList 待发送消息列表
	 */
	@Override
	public void batchUpdateMoMessage(int status,List<MoMessage> moMsgList){
		List<Object[]> params = new ArrayList<Object[]>();
		for(int i=0; i< moMsgList.size(); i++){
			Object[] obj = {status,moMsgList.get(i).getMo_sid()};
			params.add(obj);
		}
		DBUtil.batchUpdate(SQL.UPDATE_MESSAGE, params );
	}
	/**
	 * 保存短信发送结果
	 * @param log 短信发送结果
	 */
	@Override
	public void saveSendLog(MoSendLog log){
		DBUtil.doInsert(SQL.SAVE_SENDLOG, log.getStatus_code(),log.getStatus_time(),log.getRequest_url(),log.getRequest_body(),log.getResponse_data(),log.getPlatform());
	}

}
