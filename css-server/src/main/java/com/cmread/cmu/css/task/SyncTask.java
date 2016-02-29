package com.cmread.cmu.css.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;
import com.cmread.cmu.css.task.schedule.Task;

/**
 * 应该改为TaskContext?
 * @author zhangtieying
 *
 */
public interface SyncTask extends Task {

	String getTaskType();
	TaskParamter getContent();

	TaskConfig getConfig();
	
	void begin(Date date);

	void finish(Date time, boolean success, boolean retrying);
	
	void waitForComplete() throws InterruptedException;
	
	TaskSourceInfo getSource();
	Date getCreateTime();
	SyncTaskStatus getStatus();
	List<SyncTaskRunningRecord> getRunningRecord();
	
	SqlTaskExecutorServiceRegistry getSqlExectorManager();
	
	SqlExecutor getSqlExecutor(String dbName);
//	TaskOperate getTaskOperate();
	/*
	 * 表示是否是同步请求
	 */
	boolean isSync();
	Map<String, String> getResuleMap();
	
}