/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 内容校对：章节内容校验
 */
public class BookChapterContentUpdate implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String chapterID= BaseTHHelper.getChapterID(task);
		final String bookID= BaseTHHelper.getBookID(task);

		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapBootToCommonAction(chapterID, bookID, job);
			}
		});
	}
	
	protected void mapBootToCommonAction(String chapterID, String bookID, OneToOneJobBuilder job) {
		Action contentCollateAction = BaseDMHelper.mapContentCollate(chapterID, job).diffSyncAction();
		
		Action chapterUpdateAction = BaseDMHelper.mapBookChapterByChapterID(chapterID, job).diffSyncAction();
		Action contentAction = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();		
		Action auditBatchInfoUpdateAction = BaseDMHelper.mapBookAuditBatchInfoBybookID(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
		Action bookItemAction = BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
		
		// 此处好像没有外键约束
		job.next(chapterUpdateAction);
		job.next(ebookAction);
		job.next(contentAction);
		job.next(bookItemAction);
		job.next(auditBookInfoAction);
		job.next(auditBatchInfoUpdateAction);
		job.next(contentCollateAction);
		
	}



}
