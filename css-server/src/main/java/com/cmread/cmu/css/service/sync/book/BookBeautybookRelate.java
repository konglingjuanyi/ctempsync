package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 精装图书关联表同步
 * 
 * @author caidq
 * 
 */
public class BookBeautybookRelate implements TaskHandler{

	@Override
	public String getRelatedKey(SyncTask task) {
		//暂时不排队，不互斥，直接跳过排队，要么用book-?
		return null;
	}
	
	private String getBeautybookID(SyncTask task) {
		return task.getContent().getMap().get("beautybookid");
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String beautybookID = getBeautybookID(task);

		// common=>book
		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				Action BeautybookAction = BaseDMHelper.mapBeautybook(beautybookID, job).diffSyncAction();

				job.next(BeautybookAction);
			}
		});
	}

}
