/**
 * 
 */
package com.cmread.cmu.css.service.sync.publisher;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/**
 * @author caidq
 *
 * 2015-12-9
 */
public abstract class BasePublisherTaskHandler implements TaskHandler {

	protected String getPublisherID(SyncTask task) {
		return task.getContent().getMap().get("publisherid");
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		return "publisher-" + getPublisherID(task);
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 出版社信息同步
		String publisherID = getPublisherID(task);
		// TODO 并存期间，所有类似局数据等都用老系统的，所以暂时从common往bookt同步，后续并存结束后从book同步至common
		String fromDB = "common";

		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB(fromDB);
		commonToBookJob.toDB("book");
		mapBookToCommonAction(publisherID, commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	public abstract void mapBookToCommonAction(String publisherID,
			OneToOneJobBuilder subJob);

	/*
	 * 出版社信息同步：暂时从common往bookt同步，后续并存结束后从book同步至common TODO
	 */
	public DataMapBuilder mapPublisher(String publisherID,
			OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("con_publisher").primaryKey("id")
				.cond("id", publisherID);
		dataMap.to().tableName("bks_publisher").primaryKey("id")
				.cond("id", publisherID);

		return dataMap;
	}

}
