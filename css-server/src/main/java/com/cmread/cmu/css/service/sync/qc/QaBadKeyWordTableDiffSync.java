package com.cmread.cmu.css.service.sync.qc;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class QaBadKeyWordTableDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "qa-badkeyword_table_diffsync";
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapCommonToBookAction(job);
			}
		});
	}
	
	protected void mapCommonToBookAction(OneToOneJobBuilder job) {
		Action qaBadKeyWordsAction = mapQaBadKeyWordsTable(job).diffSyncAction();
		
		job.next(qaBadKeyWordsAction);
	}

	protected DataMapBuilder mapQaBadKeyWordsTable(OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder(); 
		
		String tableName = "qa_sup_badkeywords";
		dataMap.from().tableName(tableName).primaryKey("keywordid");
		dataMap.to().tableName(tableName).primaryKey("keywordid");

		return dataMap;
	}

}