package com.sxdt.bi.sms.sender.db;

import java.sql.ResultSet;
import java.util.List;

public interface RowCallBack<T> {
	List<T> doCallBack(ResultSet rs);
}
