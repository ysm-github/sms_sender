package com.sxdt.bi.sms.sender.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sxdt.bi.sms.sender.config.Config;

/**
 * 数据库连接管理工具类
 * @author ysm
 * @date 2016-8-29 下午01:32:34
 */
public class DataSource {
	static String driver;
	static String url;
	static String user;
	static String pwd;
	static {
		driver=Config.getConfig("jdbc.driver");
		url=Config.getConfig("jdbc.url");
		user=Config.getConfig("jdbc.user");
		pwd=Config.getConfig("jdbc.password");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	// 获取数据库连接
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	// 关闭连接
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 关闭会话
	public static void close(Statement stm) {
		if (stm != null) {
			try {
				stm.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 关闭结果集
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 关闭数据库资源
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		close(rs);
		close(stmt);
		close(conn);
	}
}
