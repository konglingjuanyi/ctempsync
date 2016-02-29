package com.cmread.cmu.css.db.asyncsql;

import java.sql.ResultSet;

public interface ResultSetConvert<T> {

	T convert(ResultSet rs) throws Exception;
	
}
