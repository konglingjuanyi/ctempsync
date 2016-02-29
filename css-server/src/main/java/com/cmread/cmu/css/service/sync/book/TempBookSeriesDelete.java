package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class TempBookSeriesDelete implements TaskHandler {

	@Override
	// 无需互斥排队,难得更新的配置直接过了
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookid = task.getContent().getMap().get("bookid");

		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {				
				Action bookSeriesAction = BaseDMHelper.mapBookSeriesByBookID(bookid, job).diffSyncAction();					
				
				job.next(bookSeriesAction);
			}
		});
	}
}
