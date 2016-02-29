package com.cmread.cmu.css.task.manager;

import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.SyncTaskRunningRecord;
import com.cmread.cmu.css.task.TaskConfig;
import com.cmread.cmu.css.task.schedule.TaskEventListener;
import com.cmread.cmu.css.task.schedule.TaskEventNotifier;
import com.cmread.cmu.css.task.schedule.TaskLifecycleEvent;
import com.cmread.cmu.css.task.schedule.TaskLifecycleEvent.EventType;
import com.google.gson.stream.JsonWriter;

/**
 * 同步任务结束时将任务信息整理为json格式，并记录到日志中；
 * 这个日志与持久化任务记录（数据库中）有一定重复，但是相对持久化任务记录，这个日志是最全的同步记录日志，因为有部分非持久化同步任务不会记录到数据库中；
 * 同时这个日志也可方面为后面分析、搜索等增加搜索条件
 * 
 * 主要为运维能够简单看到任务执行记录，避免从详细的task日志中搜索查找同步条目；
 * 也可以用于第三方程序分析使用。
 * 
 * @author zhangtieying
 *
 */
public class SyncTaskRecordLogger implements TaskEventListener {
	
	private static Logger logger = LoggerFactory.getLogger("csync.task.record");
	
	public void setTaskEventNotifier(TaskEventNotifier taskEventNotifier) {
		taskEventNotifier.addListener(this);
	}

	@Override
	public void onEvent(TaskLifecycleEvent event) {
		SyncTask task = (SyncTask) event.getTask();

		if (event.getType() == EventType.FINISH) {
			//仅在任务结束时记录日志；
			StringWriter sw = new StringWriter();
			try (JsonWriter writer = new JsonWriter(sw)) {
				writer.beginObject();
				
				writer.name("taskid").value(task.getTaskID());
				writer.name("tasktype").value(task.getTaskType());
				writer.name("status").value(task.getStatus().toString());
				writer.name("failedtimes").value(SyncTaskDBPersistent.getFailedTimes(task));
				
				writer.name("clientstarttime").value(formatDate(task.getSource().getClientStartTime()));
				writer.name("createtime").value(formatDate(task.getCreateTime()));

				SyncTaskRunningRecord lastRecord = SyncTaskDBPersistent.getLastRunningRecord(task);
				if (lastRecord != null) {
					writer.name("starttime").value(formatDate(lastRecord.getStartTime()));
					writer.name("endtime").value(formatDate(lastRecord.getFinishTime()));
				} 
				
				writer.name("realatedKey").value(getRelatedKey(task));
				writer.name("message").value(task.getSource().getMessage());
				writer.name("clientIp").value(task.getSource().getClientIp());

				writer.name("config");
				TaskConfig config = task.getConfig();
				writer.beginObject();
				writer.name("priority").value(config.getPriority());
				writer.name("persistent").value(config.isPersistent());
				writer.name("handler").value(config.getTaskHandler().getClass().getName());
				writer.endObject();
				
				writer.endObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			logger.info(sw.toString());
		}
	}
	
	private String formatDate(Date date) {
		if (date != null) {
			return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
		} 
		return "";
	}

	private String getRelatedKey(SyncTask task) {
		return (task == null) ? "" : task.getRelatedKey();
	}
}
