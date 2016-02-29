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
 * 全表差异同步 标签库
 * 
 * @author zhangtieying
 *
 */
public class RecommendLabelTableDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "recommend-label";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		mapCommonToBookAction(commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void mapCommonToBookAction(OneToOneJobBuilder job) {
		Action label = mapRecommendLabel(job).diffSyncAction();
		Action labelGroup = mapRecommendLabelGroup(job).diffSyncAction();
		Action labelSubGroup = mapRecommendLabelSubGroup(job).diffSyncAction();
		Action labelSub = mapRecommendLabelSub(job).diffSyncAction();
		
		job.next(label).next(labelGroup).next(labelSubGroup).next(labelSub);
	}

	protected DataMapBuilder mapRecommendLabel(OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel").primaryKey("labelid");
		dataMap.to().tableName("bks_recommend_clabel").primaryKey("labelid");

		return dataMap;
	}
	
	protected DataMapBuilder mapRecommendLabelGroup(OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_group").primaryKey("groupid");
		dataMap.to().tableName("bks_recommend_clabelgroup").primaryKey("groupid");

		return dataMap;
	}

	protected DataMapBuilder mapRecommendLabelSubGroup(OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_subgroup").primaryKey("groupid");
		dataMap.to().tableName("bks_recommend_clabelsubgroup").primaryKey("groupid");

		return dataMap;
	}
	
	protected DataMapBuilder mapRecommendLabelSub(OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_sub").primaryKey("groupid", "labelid");
		dataMap.to().tableName("bks_recommend_clabelsub").primaryKey("groupid", "labelid");

		return dataMap;
	}
}
