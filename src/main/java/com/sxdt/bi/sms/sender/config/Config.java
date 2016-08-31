package com.sxdt.bi.sms.sender.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 系统配置工具类
 * @author ysm
 * @date 2016-8-29 下午01:28:31
 */
public class Config {
	private static final Log log=LogFactory.getLog(Config.class);
	private static Map<String,String> map;
	static {
		try {
			Properties p = new Properties();
			p.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
			map=new HashMap<String, String>(p.size());
			String key;
			String value;
			for(Object obj:p.keySet()){
				key=obj.toString().trim();
				value=p.getProperty(key, "").trim();
				log.info("load properties["+key+" = "+value+"]");
				map.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public static String getConfig(String key){
		if(StringUtils.isBlank(key)){
			throw new IllegalArgumentException("key must not be empty");
		}
		return map.get(key.trim());
	}
}
