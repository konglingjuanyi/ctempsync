/**
 * 
 */
package com.cmread.cmu.css.service.sync.recommend;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.DataMap.Filter;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/**
 * @author caidq
 * 
 *         2015-12-22 分类维度表，共存期由老系统同步到bks
 * 
 */
public class TempClassEvaluationDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "recommend-class-evaluation";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		
		String secondclassID = task.getContent().getMap().get("secondclassid");
		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		mapCommonToBookAction(secondclassID,commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void mapCommonToBookAction(String secondclassID,OneToOneJobBuilder job) {
		Action classEvaluation = mapClassEvaluation(secondclassID,job).diffSyncAction();		
		job.next(classEvaluation);
	}

	protected DataMapBuilder mapClassEvaluation(String secondclassID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_class_evaluation").primaryKey("secondclassid","evaluationid")
			.cond("secondclassid", secondclassID).addFilter(new Filter() {
			@Override
			public void filter(RowSet rowSet) {
				for (Row row : rowSet.getRows()) {
					row.addExtraFieldAtInsert("ID", "hibernate_sequence.nextval");
				}
			}
		});
		dataMap.to().tableName("bks_class_evaluation").primaryKey("secondclassid","evaluationid")
			.cond("secondclassid", secondclassID);

		return dataMap;
	}
	
}
