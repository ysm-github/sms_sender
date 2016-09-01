package com.sxdt.bi.sms.sender.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBUtil {
	private static final Log log = LogFactory.getLog(DBUtil.class);
	// 执行新增
	public static void doInsert(String sql, Object... params){
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataSource.getConnection();
			pst = conn.prepareStatement(sql);
			if (ArrayUtils.isNotEmpty(params)) {
				for (int i = 1; i <= params.length; i++) {
					pst.setObject(i, params[i-1]);
				}
			}
			 pst.executeUpdate();
		} catch(Exception e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			DataSource.close(pst);
			DataSource.close(conn);
		}
	}
	// 执行删除
	public static void doDelete(String sql, Object... params) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataSource.getConnection();
			pst = conn.prepareStatement(sql);
			if (ArrayUtils.isNotEmpty(params)) {
				for (int i = 1; i <= params.length; i++) {
					pst.setObject(i, params[i]);
				}
			}
			 pst.executeUpdate(sql);
		}finally {
			DataSource.close(pst);
			DataSource.close(conn);
		}
	}

	// 执行更新
	public static void doUpdate(String sql, Object... params) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataSource.getConnection();
			pst = conn.prepareStatement(sql);
			if (ArrayUtils.isNotEmpty(params)) {
				for (int i = 1; i <= params.length; i++) {
					pst.setObject(i, params[i-1]);
				}
			}
			pst.executeUpdate();
		} finally {
			DataSource.close(pst);
			DataSource.close(conn);
		}
	}

	// 批量执行更新
	public static void batchUpdate(String sql, List<Object[]> params) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataSource.getConnection();
			pst = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			if (params != null && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					if (ArrayUtils.isNotEmpty(params.get(i))) {
						for (int j = 1; j <= params.get(i).length; j++) {
							pst.setObject(j, params.get(i)[j-1]);
						}
					}
					pst.addBatch();
					if (i % 100 == 0) {
						pst.executeUpdate();
						conn.commit();
						pst.clearBatch();
						log.info("commit : " + i);
					}
				}
			}
			pst.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error(e.getMessage(), e);
		} finally {
			DataSource.close(pst);
			DataSource.close(conn);
		}
	}

	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return ResultSet
	 * @throws SQLException
	 */
	public static <T> List<T> doSelect(Connection con,String getMessageSql, 
			RowCallBack<T> callback, Object... params){
		//Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			//conn = DataSource.getConnection();
			pst = con.prepareStatement(getMessageSql);
			if (ArrayUtils.isNotEmpty(params)) {
				for (int i = 1; i <= params.length; i++) {
					pst.setObject(i, params[i-1]);
				}
			}
			rs = pst.executeQuery();
			list = callback.doCallBack(rs);
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		} finally {
			DataSource.close(rs);
			DataSource.close(pst);
		}
		return list;

	}

	// 批量执行更新
	public static void batchRevise(Connection con, String sql, List<Object[]> params) {
		//Connection conn = null;
		PreparedStatement pst = null;
		try {
			//conn = DataSource.getConnection();
			pst = con.prepareStatement(sql);
			con.setAutoCommit(false);
			if (params != null && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					if (ArrayUtils.isNotEmpty(params.get(i))) {
						for (int j = 1; j <= params.get(i).length; j++) {
							pst.setObject(j, params.get(i)[j-1]);
						}
					}
					pst.addBatch();
					if (i % 100 == 0) {
						pst.executeUpdate();
						con.commit();
						pst.clearBatch();
						log.info("commit : " + i);
					}
				}
			}
			pst.executeBatch();
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error(e.getMessage(), e);
		} finally {
			DataSource.close(pst);
			//DataSource.close(conn);
		}
	}
	
}
