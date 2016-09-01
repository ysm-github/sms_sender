package com.sxdt.bi.sms.sender;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sxdt.bi.sms.sender.config.Config;
import com.sxdt.bi.sms.sender.dao.MessageDao;
import com.sxdt.bi.sms.sender.dao.MessageDaoDBImpl;
import com.sxdt.bi.sms.sender.entity.MoMessage;
import com.sxdt.bi.sms.sender.entity.MoSendLog;
import com.sxdt.bi.sms.sender.httpclient.HttpClientContext;
import com.sxdt.bi.sms.sender.httpclient.HttpSender;

public class Main {
	private static final Log log = LogFactory.getLog(Main.class);
	private static volatile boolean run = true;
	private static MessageDao messageDao = new MessageDaoDBImpl();
	private static int num = 1000;
	private static boolean needstatus = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ScheduledExecutorService service = Executors
				.newScheduledThreadPool(10);
		service.scheduleAtFixedRate(new Runnable() {
			List<MoMessage> messages = null;
			public void run() {
				if (run) {
					//test();
					// TODO 1.get mo message
					// TODO 2.send message
					// TODO 3.update mo message status
					// TODO 4.save log message
					messages = messageDao.getMoMessage(num);
					if(messages!=null && messages.size()>0){
						String response = sendMessage(messages,needstatus,"");
						String status = response.substring(response.indexOf(",")+1, response.indexOf(",")+2);
						if("0".equals(status)){
							//更新已发送状态
							messageDao.batchUpdateMoMessage(1, messages);
						}
						MoSendLog msLog = getLog(response,status);
						try {
							messageDao.saveSendLog(msLog);
						} catch (SQLException e) {
							log.error(e.getMessage(), e);
						}
					}
				} else {
					log.info("run : " + String.valueOf(run));
				}
			}
		}, 1, 5, TimeUnit.SECONDS);
		log.info("SMSSender started successfully");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Shutdown start clear ");
				run = false;
				log.info("service stoping");
				// set stop flag and finish working
				// stop ScheduledExecutorService
				service.shutdown(); // Disable new tasks from being submitted
				try {
					// Wait a while for existing tasks to terminate
					if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
						service.shutdownNow(); // Cancel currently executing
												// tasks
						// Wait a while for tasks to respond to being cancelled
						if (!service.awaitTermination(10, TimeUnit.SECONDS))
							log.error("service did not terminate");
					}
				} catch (InterruptedException ie) {
					// (Re-)Cancel if current thread also interrupted
					service.shutdownNow();
					// Preserve interrupt status
					// Thread.currentThread().interrupt();
				}
				// shutdown httpclient

				log.info("Shutdown finish clear ");

			}
		});
		// sleep(15000);
		// System.out.println("exit system");
		// System.exit(1);
	}

	public static void test() {
		// String url =
		// "http://172.16.7.74:8011/eas_server/crm/customer/updatSHECustomer.do";//
		// 应用地址
		// String account = "询问对接人";// 账号
		// String pswd = "询问对接人";// 密码
		String mobile = "15979151751,15818693048";// 手机号码，多个号码使用","分割
		String msg = "短信内容";// 短信内容
		boolean needstatus = false;// 是否需要状态报告，需要true，不需要false
		String extno = "";// 扩展码

		try {
			String returnString = HttpSender.batchSend(mobile, msg, needstatus,
					extno);
			log.info("短信发送结果：" + returnString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}
	
	//发短信
	public static String sendMessage(List<MoMessage> messages,boolean needstatus,String extno){
		String response = "";
		StringBuffer mobile = new StringBuffer();
		String message = messages.get(0).getMessage();
		try {
			for(int i=0; i<messages.size(); i++){
				mobile.append(messages.get(i).getMobile());
				mobile.append(",");
			}
			String mobiles = mobile.toString().substring(0, mobile.length()-1);
			response = HttpSender.batchSend(mobiles, message, needstatus, extno);
			log.info("短信发送结果：" + response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return response;
	}
	
	public static MoSendLog getLog(String response,String status){
		MoSendLog log = new MoSendLog();
		log.setResponse_data(response);
		String rptime = response.substring(0, response.indexOf(","));
		log.setStatus_time(rptime);
		/*log.setResponseTime(rptime.substring(0, 4)+"-"+rptime.substring(4, 6)+"-"+rptime.substring(6, 8)+
				" "+rptime.substring(8, 10)+":"+rptime.substring(10, 12)+":"+rptime.substring(12, 14));*/
		if("0".equals(status)){
			log.setStatus_code(0);
		}else{
			String state = response.substring(response.indexOf(",")+1, response.length()); 
			log.setStatus_code(Integer.parseInt(state));
		}
		log.setRequest_url(Config.getConfig("sms.server.url"));
		log.setPlatform("sms_sender");
		log.setRequest_body(HttpClientContext.request_body);
		return log;
	}
	
}
