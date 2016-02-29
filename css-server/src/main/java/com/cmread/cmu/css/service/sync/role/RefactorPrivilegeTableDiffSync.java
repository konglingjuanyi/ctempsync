package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class RefactorPrivilegeTableDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "refactor_privilege_table_diffsync";
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
		Action refactorPrivilege = mapRefactorPrivilegeTable(job).diffSyncAction();
		
		job.next(refactorPrivilege);
	}

	protected DataMapBuilder mapRefactorPrivilegeTable(OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		String tableName = "refactor_t_bme_privilege";
		roleMap.from().tableName(tableName).primaryKey("privilegeid");
		roleMap.to().tableName(tableName).primaryKey("privilegeid");

		return roleMap;
	}

}
