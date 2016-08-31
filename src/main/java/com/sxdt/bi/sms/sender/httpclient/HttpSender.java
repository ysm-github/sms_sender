package com.sxdt.bi.sms.sender.httpclient;

import java.util.HashMap;
import java.util.Map;

import com.sxdt.bi.sms.sender.config.Config;

/**
 * 
 * @param url
 *            应用地址，类似于http://ip:port/msg/
 * @param account
 *            账号
 * @param pswd
 *            密码
 * @param mobile
 *            手机号码，多个号码使用","分割
 * @param msg
 *            短信内容
 * @param needstatus
 *            是否需要状态报告，需要true，不需要false
 * @return 返回值定义参见HTTP协议文档
 * @throws Exception
 */
public class HttpSender {
	private static HttpClientContext context = new HttpClientContext();
	
	public static String batchSend(String mobile, String msg,
			boolean needstatus, String extno) throws Exception {
		Map<String, String> paramsMap=new HashMap<String, String>();
		paramsMap.put("account", Config.getConfig("sms.account"));
		paramsMap.put("pswd", Config.getConfig("sms.password"));
		paramsMap.put("mobile", mobile);
		paramsMap.put("needstatus", String.valueOf(needstatus));
		paramsMap.put("msg", msg);
		paramsMap.put("extno", extno);
		String response=context.doPost(Config.getConfig("sms.server.url"), paramsMap, null);
		return response;
	}
	
	public static void shutdown(){
		context.dispose();
	}
}