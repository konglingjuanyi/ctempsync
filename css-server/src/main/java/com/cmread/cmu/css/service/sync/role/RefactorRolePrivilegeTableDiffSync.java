package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class RefactorRolePrivilegeTableDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "refactor_role_privilege_table_diffsync";
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
		Action refactorRolePrivilege = mapRefactorRolePrivilegeTable(job).diffSyncAction();
		
		job.next(refactorRolePrivilege);
	}

	protected DataMapBuilder mapRefactorRolePrivilegeTable(OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 

		String tableName = "refactor_t_bme_role_privilege";
		roleMap.from().tableName(tableName).primaryKey("roleid", "privilegeid");
		roleMap.to().tableName(tableName).primaryKey("roleid", "privilegeid");

		return roleMap;
	}

}