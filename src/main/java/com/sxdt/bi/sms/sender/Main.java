package com.sxdt.bi.sms.sender;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sxdt.bi.sms.sender.httpclient.HttpSender;

public class Main {
	private static final Log log = LogFactory.getLog(Main.class);
	private static volatile boolean run = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ScheduledExecutorService service = Executors
				.newScheduledThreadPool(10);
		service.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (run) {
					test();
					// TODO 1.get mo message
					// TODO 2.send message
					// TODO 3.update mo message status
					// TODO 4.save log message
					sleep(10000);
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
	public void shutdown(){
		//do something
	}

}
