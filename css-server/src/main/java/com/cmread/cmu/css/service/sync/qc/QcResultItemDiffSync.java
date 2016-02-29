/**
 * 
 */
package com.cmread.cmu.css.service.sync.qc;


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
 * 2015-12-21
 * 风险等级表,共存期暂时启用
 */
public class QcResultItemDiffSync implements TaskHandler {
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "qc-item";// 随便取了个。。
	}

	@Override
	public TaskResult exec(SyncTask task) {

		String ID = task.getContent().getMap().get("id");
		String fromDB = "common";

		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB(fromDB);
		commonToBookJob.toDB("book");
		mapAction(ID, commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}
	
	private void mapAction(String ID, OneToOneJobBuilder subJob){
		Action qcResultItemAction = mapQcResultItems(ID, subJob).diffSyncAction();
		
		subJob.next(qcResultItemAction);
	}
	
	/*
	 * 风险等级表，并存期从老系统同步到bks：
	 */
	private DataMapBuilder mapQcResultItems(String ID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("con_qc_result_items").primaryKey("id").cond("id", ID);
		dataMap.to().tableName("bks_qc_result_items").primaryKey("id").cond("id", ID);

		return dataMap;
	}
	
	

}
