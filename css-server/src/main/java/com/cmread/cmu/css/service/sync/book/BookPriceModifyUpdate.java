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
 * 价格信息同步
 */
public class BookPriceModifyUpdate implements TaskHandler {

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
				mapBookToCommonAction(bookID, job);
			}
		});
	}

	protected void mapBookToCommonAction(String bookID, OneToOneJobBuilder job) {
		Action bookExtraAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();
		Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, "bks_productinfohead").diffSyncAction();
		
		Action chapterAciton = BaseDMHelper.mapBookChapterByBookID(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();

		job.next(bookExtraAction).next(bookProductinfoAction).next(chapterAciton).next(ebookAction).next(auditBookInfoAction);

	}

}
