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
 * 删除下架图书同步
 */
public class BookDeleteOffShelfUpdate implements TaskHandler {

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
				Action contentAction = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
				Action bookFasciculeAction = BaseDMHelper.mapBookFascicule(bookID, job).diffSyncAction();
				job.next(contentAction);
				job.next(bookFasciculeAction);
			}
		});
	}

}
