package com.cmread.cmu.css.db.asyncsql;

import java.util.concurrent.Future;

import javax.sql.DataSource;

/**
 *  * 每个datasource一个sqlexecutor；
 * 但是多个sqlexecutor可以共享一个sqltaskExecutor，也可以使用不同的sqlTaskExecutor；
 * 主要在于是否共享一个物理数据库
 * @author zhangtieying
 *
 */
public class SqlTaskExecutorService implements SqlTaskExecutor {

	private String dbName;
	
	private DataSource dataSource; //把datasource传递到sqltask中；
	
	private SqlTaskExecutor sqlTaskExecutor; 
	
	public void setSqlTaskExecutor(SqlTaskExecutor sqlTaskExecutor) {
		this.sqlTaskExecutor = sqlTaskExecutor;
	}
	
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public <T> Future<T> execute(SqlTask<T> sqlTask) {
		return this.sqlTaskExecutor.execute(sqlTask);
	}

}
