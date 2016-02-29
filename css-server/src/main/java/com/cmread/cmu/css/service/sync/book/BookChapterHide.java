package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/*
 * 章节隐藏同步 
 */
public class BookChapterHide implements TaskHandler {


	@Override
	//暂时把就更新章节信息的单独拉出来，省去查找bookid的过程
	public String getRelatedKey(SyncTask task) {
		return "chapter-" + task.getContent().getMap().get("chapterid");
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String chapterID = task.getContent().getMap().get("chapterid");

		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				Action chapterAction = BaseDMHelper.mapBookChapterByChapterID(chapterID, job).diffSyncAction();

				job.next(chapterAction);
			}
		});
	}



}
