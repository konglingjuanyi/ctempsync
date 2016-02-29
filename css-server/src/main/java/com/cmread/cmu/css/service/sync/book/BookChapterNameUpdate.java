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
 * 内容校对：章节名校验
 */
public class BookChapterNameUpdate implements TaskHandler {


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
		Action auditBatchInfoUpdateAction = BaseDMHelper.mapBookAuditBatchInfoBybookID(bookID, job).diffSyncAction();
		
		job.next(chapterUpdateAction,auditBatchInfoUpdateAction).next(contentCollateAction);
	}

}


