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
 * 热门书单
 * 
 * 并存期间临时使用，sql拦截器使用
 */
public class TempHotBooksDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		
		String bookID = task.getContent().getMap().get("bookid");
		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		mapCommonToBookAction(bookID,commonToBookJob);

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void mapCommonToBookAction(String bookID,OneToOneJobBuilder job) {
		Action hotBooks = mapHotBooks(bookID,job).diffSyncAction();		
		job.next(hotBooks);
	}

	protected DataMapBuilder mapHotBooks(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_book_hot").primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName("bks_book_hot").primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}
	
}
