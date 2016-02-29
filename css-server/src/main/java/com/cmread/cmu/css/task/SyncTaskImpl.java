package com.cmread.cmu.css.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;

public class SyncTaskImpl implements SyncTask {

	private static Logger logger = LoggerFactory.getLogger(SyncTaskImpl.class);
	
	private String taskID;
	private TaskConfig taskConfig;
	private TaskParamter content;
	private TaskSourceInfo source;
	private Date createTime;
	private SqlTaskExecutorServiceRegistry sqlManager;
	private Map<String, String> resultMap;
	
	/*
	 * 0 刚创建
	 * 1 开始运行
	 * 2 成功结束
	 * 3 失败重试
	 * 4 重试失败
	 */
	//需要重构，改为enum等更安全方式，现在先凑乎用吧 :)
	private SyncTaskStatus status;
	
	private CountDownLatch taskFinished;
	private List<SyncTaskRunningRecord> runnigRecords;

	public SyncTaskImpl(TaskConfig taskConfig, Map<String, String> contentID, TaskSourceInfo source) {
		// FIXME 主要是为了能够全程跟踪日志，不好，后面修改
		String traceID = MDC.get("traceID");
		if (traceID == null) {
			this.taskID = UUID.randomUUID().toString();
		} else {
			this.taskID = traceID;
		}
		
		this.taskConfig = taskConfig;
		this.content = new TaskParamter(contentID);
		this.status = SyncTaskStatus.CREATED;
		this.source = source;

		this.taskFinished = new CountDownLatch(1);

		this.createTime = new Date();
		this.runnigRecords = new ArrayList<SyncTaskRunningRecord>(4);
		this.resultMap = new HashMap<>();
	}
	
	@Override
	public String getTaskType() {
		return this.getConfig().getTaskType();
	}

	@Override
	public TaskParamter getContent() {
		return this.content;
	}
	
	/**
	 * FIXME 不严谨的实现
	 * @throws InterruptedException
	 */
	public void waitForComplete() throws InterruptedException {
		taskFinished.await();
	}
	
	public String getTaskID() {
		return this.taskID;
	}

	public TaskConfig getConfig() {
		return this.taskConfig;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public TaskSourceInfo getSource() {
		return source;
	}

	public SyncTaskStatus getStatus() {
		return status;
	}
	
	public List<SyncTaskRunningRecord> getRunningRecord() {
		return this.runnigRecords;
	}

	/**
	 * FIXME 需要并发保护
	 */
	@Override
	public void begin(Date date) {
		SyncTaskRunningRecord stri = new SyncTaskRunningRecord();
		stri.setStartTime(date);
		this.runnigRecords.add(stri);
		
		//第一次开始运行，设置status状态为开始，重试状态下的开始不修改状态
		if (status == SyncTaskStatus.CREATED) {
			status = SyncTaskStatus.STARTED;
		}
	}

	/**
	 * FIXME 同上，需要并发保护
	 */
	@Override
	public void finish(Date time, boolean success, boolean retrying) {
		SyncTaskRunningRecord stri = this.runnigRecords.get(this.runnigRecords.size()-1);
		stri.setFinishTime(time);
		
		logger.trace("synctaskimpl finish successful:{} retrying:{}", success, retrying);
		
		if (success) {
			//成功结束
			status = SyncTaskStatus.SUCCESS;
			taskFinished.countDown();
		} else {
			if (retrying) {
				status = SyncTaskStatus.FAILED_TRYING;
			} else {
				status = SyncTaskStatus.FAILED;
				taskFinished.countDown();
			}
		}
	}

	private TaskHandler getTaskExec() {
		return this.taskConfig.getTaskHandler();
	}

	@Override
	public String getRelatedKey() {
		return getTaskExec().getRelatedKey(this);
	}

	@Override
	public TaskResult exec() {
		return getTaskExec().exec(this);
	}

	@Override
	public int compareTo(Object o) {
		SyncTask other = (SyncTask)o;
		
		int prio = getConfig().getPriority() - other.getConfig().getPriority();
		if (prio == 0) {
			long timeDiff = getCreateTime().getTime() - other.getCreateTime().getTime();
			return (timeDiff > 0) ? -1 : ((timeDiff == 0) ? 0 : 1); 
		}
		return prio;
	}

	public void setSqlManager(SqlTaskExecutorServiceRegistry sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public SqlTaskExecutorServiceRegistry getSqlExectorManager() {
		return this.sqlManager;
	}

	@Override
	public SqlExecutor getSqlExecutor(String dbName) {
		return this.sqlManager.getExecutor(dbName, this);
	}

	@Override
	public boolean isSync() {
		String syncFlag = this.getContent().getMap().get("sync");
		return StringUtils.equals("true", syncFlag);
	}

	@Override
	public Map<String, String> getResuleMap() {
		return this.resultMap;
	}

}
