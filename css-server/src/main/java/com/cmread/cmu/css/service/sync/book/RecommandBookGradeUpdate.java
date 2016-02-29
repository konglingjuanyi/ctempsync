package com.cmread.cmu.css.service.sync.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/*
 * 推荐：评分同步
 */
public class RecommandBookGradeUpdate implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(RecommandBookLevelAuditPass.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		// FIXME 同其他批量操作一起处理
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		final String[] bookIDs = BaseUtils.getBookIDs(task);
		if (bookIDs.length == 0) {
			logger.warn("task params[bookids] is empty");
			return TaskResult.SUCCESS; // 这种情况应该是成功还是失败啊？
		}
		
		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapActions(bookIDs, job);
			}
		});
	}
	
	// 将多个book映射为多个id
	public void mapActions(String[] bookIDs, OneToOneJobBuilder job) {
		for (String bookID : bookIDs) {
			Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
			Action contentAction = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
			Action bookItemAction = BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
			Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
			// bks_seriesandbook; 这个表还未同步！！！
			
			job.next(ebookAction).next(contentAction).next(bookItemAction).next(auditBookInfoAction);
		}
	}
	
}
