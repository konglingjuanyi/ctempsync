package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 分册信息同步
 * 
 * @author caidq
 * 
 */
public class BookFasciculeUpdate implements TaskHandler{

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
				Action fasciculeInfoAction = BaseDMHelper.mapBookFasciculeInfo(bookID, job).diffSyncAction();
				Action bookFasciculeAction = BaseDMHelper.mapBookFascicule(bookID, job).diffSyncAction();
				Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();

				job.next(fasciculeInfoAction,bookFasciculeAction, ebookAction);
			}
		});
	}

}
