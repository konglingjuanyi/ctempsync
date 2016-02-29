package com.cmread.cmu.css.service.sync.author;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class AuthorDiffSync extends BaseAuthorTaskHandler {

	@Override
	public void mapCommonToBookAction(String authorID, OneToOneJobBuilder subJob, SyncTask task) {
		Action authorMcpMapAction = mapAuthorMcpData(authorID, subJob, "bks_authorandmcp").diffSyncAction();
		Action authorClassMapAction = mapAuthorClassData(authorID, subJob, "bks_auclassinfo").diffSyncAction();
		Action authorMapAction = mapAuthorData(authorID, subJob, task).diffSyncAction();
		
		// 设置多个数据类型的同步执行顺序；
		subJob.next(authorMcpMapAction, authorClassMapAction).next(authorMapAction);		
	}

	@Override
	public void mapCommonToCartoonAction(String authorID, OneToOneJobBuilder subJob, SyncTask task) {
		Action authorMcpMapAction = mapAuthorMcpData(authorID, subJob, null).diffSyncAction();
		Action authorClassMapAction = mapAuthorClassData(authorID, subJob, null).diffSyncAction();
		Action authorMapAction = mapAuthorData(authorID, subJob, task).diffSyncAction();
		
		// 设置多个数据类型的同步执行顺序；
		subJob.next(authorMcpMapAction, authorClassMapAction).next(authorMapAction);	
	}

}
