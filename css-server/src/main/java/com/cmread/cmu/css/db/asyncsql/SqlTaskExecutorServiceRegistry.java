package com.cmread.cmu.css.db.asyncsql;

import java.util.Map;

public class SqlTaskExecutorServiceRegistry {

	private Map<String, SqlTaskExecutorService> sqlExecutor;
	
	public SqlExecutor getExecutor(String dbName, Comparable<Object> priority) {
		if (dbName == null) {
			return null;
		}
		
		SqlTaskExecutorService source = sqlExecutor.get(dbName);
		source.setDbName(dbName);
		
		return new SqlExecutor(source, priority);
	}
	
	public void setSqlExecutorSources(Map<String, SqlTaskExecutorService> sqlExecutors) {
		this.sqlExecutor = sqlExecutors;
	}
}
