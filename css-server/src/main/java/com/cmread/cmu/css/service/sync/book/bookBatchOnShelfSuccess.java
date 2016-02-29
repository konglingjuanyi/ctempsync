/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.http.client.RemoteTaskException;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskResult;

/**
 * @author caidq
 *
 * 2016-1-5
 */
public class bookBatchOnShelfSuccess extends BookOnShelfSuccess{
	
	private static Logger logger = LoggerFactory
			.getLogger(bookBatchOnShelfSuccess.class);
	
	private CSSClientHelper client;
	
	public void setCSSClientHelper(CSSClientHelper client) {
		this.client = client;
	}
	
	@Override
	// 批量图书上下架，怎么排队？TODO
	public String getRelatedKey(SyncTask task) {
		return null;
	}
	
	@Override
	public TaskResult exec(final SyncTask task) {
		final String[] bookIDs = BaseUtils.getBookIDs(task);
		if (bookIDs.length == 0) {
			logger.warn("task params[bookids] is empty");
			return TaskResult.SUCCESS;
		}

		for (String bookID : bookIDs) {
			// 内部触发新的同步任务（单个上下架更新）；
			try {
				this.client.bookOnShelfSuccess(bookID);
			} catch (RemoteTaskException e) {
				// 可以忽略，内部触发不会失败
				logger.error("create sub task failed.", e);
			}
		}
		
		return TaskResult.SUCCESS;

	}
	
	// 以下是以前的单任务
	
//	return BaseTHHelper.execOneToManyTemplate(task, "common", new OneToOneJobAction(toDB) {
//		@Override
//		public void mapAction(OneToOneJobBuilder job) throws InterruptedException, ExecutionException {	
//			mapActions(bookIDs, job, task, this.getToDB());
//		}
//	});
//	
//	// 将多个book映射为多个id
//	public void mapActions(String[] bookIDs, OneToOneJobBuilder job, SyncTask task, String toDB) throws InterruptedException, ExecutionException {
//		for (String bookID : bookIDs) {
//			if (isBookInBookDB(bookID, task)) {
//				mapCommonToBookAction(bookID, job, toDB);
//			} else {
//				logger.info("bookid {} not in bks db, skip sync.", bookID);
//			}
//		}
//	}

}
