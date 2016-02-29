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
 * 修改图书分类
 */
public class BookClassModifyUpdate implements TaskHandler {

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
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();

		job.next(auditBookInfoAction).next(ebookAction);
	}

}

