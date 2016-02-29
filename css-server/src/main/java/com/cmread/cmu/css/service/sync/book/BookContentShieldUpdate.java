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
 * 内容屏蔽/解除屏蔽
 */
public class BookContentShieldUpdate implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = BaseTHHelper.getBookID(task);

		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapBootToCommonAction(bookID, job);
			}
		});
	}

	protected void mapBootToCommonAction(String bookID, OneToOneJobBuilder job) {

		Action chapterAciton = BaseDMHelper.mapBookChapterByBookID(bookID, job).diffSyncAction();
		Action contentAciton = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();

		job.next(chapterAciton).next(contentAciton).next(auditBookInfoAction);

	}

}
