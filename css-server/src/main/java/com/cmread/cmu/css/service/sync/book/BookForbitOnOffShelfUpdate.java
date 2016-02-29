package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 禁止上下架图书同步；（cms->bks)
 * 
 * 这个同步实际上是仅同步con_auditbookinfo中的forbidtype字段；
 * 目前由于禁止上下架是在CMS上实现，所有需要将此字段值同步回BKS，导致双向同步问题；
 * 
 * 后续需要跟踪其他子系统是否使用此字段，如果没有使用，则在共存期后可以考虑将此功能移到bks中；如果
 * 其他子系统也有此概念，则需要考虑是否将此功能拆分为禁止图书上下架，禁止漫画上下架等功能；主要目的仍
 * 是避免双向同步；
 * 
 * @author zhangtieying
 *
 */
public class BookForbitOnOffShelfUpdate implements TaskHandler {

	@Override
	//暂时把就更新章节信息的单独拉出来，省去查找bookid的过程
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = task.getContent().getMap().get("bookid");

		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				Action updateForbidTypeAction = mapAuditBookInfoForbidType(bookID, job).updateAction();

				job.next(updateForbidTypeAction);
			}
		});
	}
	
	/*
	 * 从cms同步con_auditbookinfo的forbidtype字段到bks
	 */
	private static DataMapBuilder mapAuditBookInfoForbidType(String bookID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String tableName = "con_auditbookinfo";
		String sql = String.format("select bookid, forbidtype from %s where bookid='%s'", tableName, bookID);
		dataMap.from().sql(sql).primaryKey("bookid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("bookid");

		return dataMap;
	}

}
