package com.cmread.cmu.css.task.manager;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.SyncTaskRunningRecord;
import com.cmread.cmu.css.task.SyncTaskStatus;

public class SyncTaskDBPersistent implements SyncTaskPersistentStrategy {

	private NamedParameterJdbcTemplate jdbcTemplate;

	private SimpleJdbcInsert insertCSyncTask;
	private SimpleJdbcInsert insertCSyncTaskSuccess;
	private SimpleJdbcInsert insertCSyncTaskFailed;
	
	private boolean globalPersistentSwitch = true;
	
	public void setGlobalSwitch(boolean persistentSwitch) {
		this.globalPersistentSwitch = persistentSwitch;
	}
	
	private boolean isPersistentOn() {
		return this.globalPersistentSwitch;
	}
	
	public void setDataSource(DataSource ds) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		this.insertCSyncTask = new SimpleJdbcInsert(ds).withTableName("csync_task");
		this.insertCSyncTaskSuccess = new SimpleJdbcInsert(ds).withTableName("csync_task_success");
		this.insertCSyncTaskFailed = new SimpleJdbcInsert(ds).withTableName("csync_task_failed");
	}

	@Override
	public void create(SyncTask task) {
		if (this.isPersistentOn()) {
			insertTask(task, insertCSyncTask);
		}
	}

	@Override
	public void update(SyncTask task) {
		if (!this.isPersistentOn()) {
			return;
		}
		
		SyncTaskStatus taskStatus = task.getStatus();

		switch (taskStatus) {
		case SUCCESS:
			// 插入到成功表中，删除运行表
			insertTask(task, insertCSyncTaskSuccess);
			deleteAtCSyncTask(task);
			return;
		case FAILED:
			// 插入到失败表，删除运行表
			insertTask(task, insertCSyncTaskFailed);
			deleteAtCSyncTask(task);
			return;
		default:
			updateCSyncTaskStatus(task);
		}
	}
	
	public void insertTask(SyncTask task, SimpleJdbcInsert insertTask) {
		Map<String, Object> parameters = new HashMap<String, Object>(10);

		parameters.put("taskid", task.getTaskID());
		parameters.put("tasktype", task.getTaskType());
		parameters.put("clientIp", task.getSource().getClientIp());
		parameters.put("clientstarttime", toTimestamp(task.getSource().getClientStartTime()));
		parameters.put("createtime", toTimestamp(task.getCreateTime()));
		parameters.put("status", task.getStatus().getValue());
		parameters.put("message", task.getSource().getMessage());

		SyncTaskRunningRecord lastRecord = getLastRunningRecord(task);
		if (lastRecord != null) {
			parameters.put("starttime", toTimestamp(lastRecord.getStartTime()));
			parameters.put("endtime", toTimestamp(lastRecord.getFinishTime()));
		} else {
			parameters.put("starttime", toTimestamp(null));
			parameters.put("endtime", toTimestamp(null));
		}

		parameters.put("failedtimes", getFailedTimes(task));

		insertTask.execute(parameters);
	}

	private void updateCSyncTaskStatus(SyncTask task) {
		String updateSql = "update csync_task set starttime=:starttime"
				+ ",endtime=:endtime, status=:status, failedtimes=:failedtimes where taskid=:taskid";

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("taskid", task.getTaskID());

		SyncTaskRunningRecord lastRecord = getLastRunningRecord(task);
		if (lastRecord != null) {
			namedParameters.addValue("starttime", toTimestamp(lastRecord.getStartTime()), Types.TIMESTAMP);
			namedParameters.addValue("endtime", toTimestamp(lastRecord.getFinishTime()), Types.TIMESTAMP);
		} else {
			namedParameters.addValue("starttime", toTimestamp(null), Types.TIMESTAMP);
			namedParameters.addValue("endtime", toTimestamp(null), Types.TIMESTAMP);
		}

		namedParameters.addValue("status", task.getStatus().getValue());
		namedParameters.addValue("failedtimes", getFailedTimes(task));

		this.jdbcTemplate.update(updateSql, namedParameters);
	}
	
	private void deleteAtCSyncTask(SyncTask task) {
		String deleteSql = "delete from csync_task where taskid=?";
		this.jdbcTemplate.getJdbcOperations().update(deleteSql, task.getTaskID());
	}

	public static SyncTaskRunningRecord getLastRunningRecord(SyncTask task) {
		List<SyncTaskRunningRecord> strr = task.getRunningRecord();
		if (strr.isEmpty()) {
			return null;
		} else {
			return strr.get(strr.size() - 1);
		}
	}

	public static int getFailedTimes(SyncTask task) {
		List<SyncTaskRunningRecord> strr = task.getRunningRecord();
		if (strr.isEmpty()) {
			return 0;
		} else {
			return strr.size() - 1;
		}
	}

	private Timestamp toTimestamp(Date date) {
		if (date == null) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

}
