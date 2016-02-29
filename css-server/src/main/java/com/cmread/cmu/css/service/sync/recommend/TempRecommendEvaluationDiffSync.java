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
 *         2015-12-22 评分维度表，共存期由老系统同步到bks
 * 
 */
public class TempRecommendEvaluationDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "recommend-evaluation";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		
		String itemID = task.getContent().getMap().get("itemid");
		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		mapCommonToBookAction(itemID,commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void mapCommonToBookAction(String itemID,OneToOneJobBuilder job) {
		Action recommendEvaluation = mapRecommendEvaluation(itemID,job).diffSyncAction();		
		job.next(recommendEvaluation);
	}

	protected DataMapBuilder mapRecommendEvaluation(String itemID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_evaluations").primaryKey("itemid").cond("itemid", itemID);
		dataMap.to().tableName("bks_recommend_evaluations").primaryKey("itemid").cond("itemid", itemID);

		return dataMap;
	}
	
}
