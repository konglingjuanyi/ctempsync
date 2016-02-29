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
 * 推荐：图书分级审核通过同步
 * 
 * 传入参数bookids，用逗号分隔的多个bookid；
 */
public class RecommandBookLevelAuditPass implements TaskHandler {
	
	private static Logger logger = LoggerFactory.getLogger(RecommandBookLevelAuditPass.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		// FIXME 
		// 这个关联列表如何实现？需要确认一下，将这个任务分解为一系列子任务？
		// 目前系统还不支持子任务？暂时放弃互斥！！！ 先把功能实现
		// 还可以提供灵活一些的relatedKey的控制方法，实现一个任务多个关联relateKey以及key的实时删除；
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
			job.next(ebookAction);
		}
	}

}
