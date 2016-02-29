package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseRoleTaskHandler implements TaskHandler {

	protected String getRoleID(SyncTask task) {
		return task.getContent().getMap().get("roleid");
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "role-" + getRoleID(task);
	}
	
	@Override
	public TaskResult exec(SyncTask task) {
		String roleID = getRoleID(task);
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		
		mapCommonToBookAction(roleID, commonToBookJob);

		tb.addSubJob(commonToBookJob);
		
		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}
	
	public abstract void mapCommonToBookAction(String roleID, OneToOneJobBuilder subJob);

	protected DataMapBuilder mapRoleData(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "t_bme_role";
		roleMap.from().tableName(roleTable).primaryKey("roleid").cond("roleid", roleID);
		roleMap.to().tableName(roleTable).primaryKey("roleid").cond("roleid", roleID);

		return roleMap;
	}

	protected DataMapBuilder mapUserRoleData(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder userRoleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "t_bme_user_role";
		userRoleMap.from().tableName(roleTable).primaryKey("operid", "roleid").cond("roleid", roleID);
		userRoleMap.to().tableName(roleTable).primaryKey("operid", "roleid").cond("roleid", roleID);

		return userRoleMap;
	}
	
}
