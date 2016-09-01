package com.sxdt.bi.sms.sender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sxdt.bi.sms.sender.config.Config;
import com.sxdt.bi.sms.sender.dao.MessageDao;
import com.sxdt.bi.sms.sender.dao.MessageDaoDBImpl;
import com.sxdt.bi.sms.sender.entity.MoMessage;
import com.sxdt.bi.sms.sender.entity.MoSendLog;

public class MessageDaoImplTest {

	public static void main(String[] args) {
		MessageDaoImplTest.getMoMessage();
		//MessageDaoImplTest.updateMoMessage();
		//MessageDaoImplTest.batchUpdateMoMessage();
		//MessageDaoImplTest.saveSendLog();
	}
	
	//获取发送信息
	public static void getMoMessage(){
		MessageDao dao = new MessageDaoDBImpl();
		List<MoMessage> list = dao.getMoMessage(10);
		if(list!=null && list.size()>0){
			for(MoMessage message : list){
				System.out.println(message.getMo_sid()+","+message.getMobile()+","+message.getMessage());
			}
		}
	}
	
	//更新单个
	public static void updateMoMessage(){
		MessageDao dao = new MessageDaoDBImpl();
		MoMessage message = new MoMessage();
		message.setMo_sid(1);
		try {
			dao.updateMoMessage(message);
			System.out.println("更新成功");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//批量更新
	public static void batchUpdateMoMessage(){
		MessageDao dao = new MessageDaoDBImpl();
		List<MoMessage> list = new ArrayList<MoMessage>();
		for(int i=1; i<3; i++){
			MoMessage mm = new MoMessage();
			mm.setMo_sid(i);
			list.add(mm);
		}
		dao.batchUpdateMoMessage(1, list);
		System.out.println("更新成功");
	}
	
	//保存日志
	public static void saveSendLog(){
		MessageDao dao = new MessageDaoDBImpl();
		MoSendLog log = new MoSendLog();
		log.setRequest_body("");
		log.setResponse_data("20160831172253,101");
		log.setStatus_code(101);
		log.setStatus_time("20160831172253");
		log.setRequest_url(Config.getConfig("sms.server.url"));
		log.setPlatform("sms_sender");
		try {
			dao.saveSendLog(log);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
