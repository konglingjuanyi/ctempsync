package com.cmread.cmu.css.service.sync.copyright;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseCopyrightTaskHandler implements TaskHandler {
	
	protected String getCopyrightID(SyncTask task) {
		return task.getContent().getMap().get("copyrightid");
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		return "copyright-" + getCopyrightID(task);
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 实现作家同步，从这里开始 :)
		String copyrightID = getCopyrightID(task);
		String fromDB = "book";

		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB(fromDB);
		commonToBookJob.toDB("common");
		mapBookToCommonAction(copyrightID, commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}
	
	public abstract void mapBookToCommonAction(String copyrightID, OneToOneJobBuilder subJob);
	
	/*
	 * 版权相关信息：
	 */
	public DataMapBuilder mapCopyrightInfo(String copyrightID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("bks_copyright_info").primaryKey("copyrightid").cond("copyrightid", copyrightID);
		dataMap.to().tableName("con_copyright_info").primaryKey("copyrightid").cond("copyrightid", copyrightID);

		return dataMap;
	}
	
}
