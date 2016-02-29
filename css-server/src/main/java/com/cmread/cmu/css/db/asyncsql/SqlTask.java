package com.cmread.cmu.css.db.asyncsql;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.slf4j.MDC;

/**
 * 
 * FIXME 应该把优先级比较从sqltask中去掉，改为compare比较器感觉比较好！！！ 
 * 
 * @author zhangtieying
 *
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public class SqlTask<T> implements Callable<T>, Comparable<SqlTask> {

	private static AtomicLong sqlTaskNumbers = new AtomicLong(0);
			
	private Comparable<Object> priority;
	private Date createTime;
	private DataSource dataSource;
	private String dbName;
	private Callable<T> sql;
	
	private Map logContext;
	
	private long sqlTaskID;
	
	private boolean disableNextTaskResultsetLog;
	
	public SqlTask() {
		this.createTime = new Date();
		this.sqlTaskID = sqlTaskNumbers.getAndIncrement();
		this.disableNextTaskResultsetLog = false;
		
		// 在这里直接保存MDC上下文，运行的时候设置；
		this.logContext = MDC.getCopyOfContextMap();
	}
	
	public void setPriority(Comparable<Object> initPriority) {
		this.priority = initPriority;
	}
	
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	@Override
	public T call() throws Exception {
		if (logContext != null) {
			MDC.setContextMap(logContext);
		}
		MDC.put("dbName", "[db:" + dbName + ":" + sqlTaskID + "]");
		if (this.disableNextTaskResultsetLog) {
			MDC.put("disable-resultset-log", "true");
		}
		
		try {
			return this.sql.call();
		} finally {
			MDC.clear();
		}
	}

	/**
	 * 先比较sql优先级（发起sql的synctask的优先级）
	 * 优先级相同的话再比较此任务创建时间，时间早的优先级高；
	 */
	@Override
	public int compareTo(SqlTask o) {
		try {
			if ((this.getPriority() == null) && (o.getPriority() != null)) {
				return 1;
			}
			if ((o.getPriority() == null) && (this.getPriority() != null)) {
				return -1;
			}

			int priority = 0;
			if ((this.getPriority() != null) && (o.getPriority() != null)) {
				priority = this.getPriority().compareTo(o.getPriority());
			}

			if (priority == 0) {
				long thisTime = this.getCreateDate().getTime();
				long otherTime = o.getCreateDate().getTime();
				return (thisTime < otherTime ? 1 : (thisTime == otherTime ? 0 : -11));
			}
			return priority;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public Date getCreateDate() {
		return this.createTime;
	}
	
	public Comparable<Object> getPriority() {
		return this.priority;
	}

	public void setSql(Callable<T> sql) {
		this.sql = sql;
		this.sql.setDataSource(dataSource);
	}
	
	public static abstract class Callable<T> implements java.util.concurrent.Callable<T> {
		private DataSource dataSource;
		
		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}
		
		public DataSource getDataSource() {
			return this.dataSource;
		}
		
	}

	public void setDBName(String dbName) {
		this.dbName = dbName;
	}

	public void setDisableResultSetLog(boolean disableNextTaskResultsetLog) {
		this.disableNextTaskResultsetLog = disableNextTaskResultsetLog;
	}

}
