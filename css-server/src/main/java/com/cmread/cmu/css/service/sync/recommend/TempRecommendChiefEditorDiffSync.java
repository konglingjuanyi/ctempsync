/**
 * 
 */
package com.cmread.cmu.css.service.sync.recommend;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/**
 * @author caidq
 *
 * 总编管理
 *
 * 并存期间临时使用，sql拦截器使用
 */
public class TempRecommendChiefEditorDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		
		String auditorID = task.getContent().getMap().get("auditorid");
		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		mapCommonToBookAction(auditorID,commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void mapCommonToBookAction(String auditorID,OneToOneJobBuilder job) {
		Action recommendChiefEditor = mapRecommendChiefEditor(auditorID,job).diffSyncAction();		
		job.next(recommendChiefEditor);
	}

	protected DataMapBuilder mapRecommendChiefEditor(String auditorID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_chiefeditor").primaryKey("auditorid").cond("auditorid", auditorID);
		dataMap.to().tableName("bks_recommend_chiefeditor").primaryKey("auditorid").cond("auditorid", auditorID);

		return dataMap;
	}
	
}
