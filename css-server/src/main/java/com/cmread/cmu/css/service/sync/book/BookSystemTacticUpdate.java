package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 系统分册策略同步
 * 
 * @author caidq
 * 
 */

public class BookSystemTacticUpdate implements TaskHandler{

	@Override
	public String getRelatedKey(SyncTask task) {
		//系统分册策略配置这种八百年改一次不用排队吧，直接上
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String tacticID = task.getContent().getMap().get("tacticid");
		// 并存期方向改为从common到book FIXME
		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				Action systemtacticAction = BaseDMHelper.mapBookSystemtactic(tacticID, job).diffSyncAction();

				job.next(systemtacticAction);
			}
		});
	}



}
