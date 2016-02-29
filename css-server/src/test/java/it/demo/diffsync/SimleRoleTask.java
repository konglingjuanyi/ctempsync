package it.demo.diffsync;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public class SimleRoleTask implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "role-test";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		String roleID = task.getContent().getMap().get("roleid");
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		
		Action userRoleAction = mapUserRoleData(roleID, commonToBookJob).diffSyncAction();
		Action roleAction = mapRoleData(roleID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(userRoleAction).next(roleAction);
		
		tb.addSubJob(commonToBookJob);
		
		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}

	protected DataMapBuilder mapRoleData(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "simple_role";
		roleMap.from().tableName(roleTable).primaryKey("roleid").cond("roleid", roleID);
		roleMap.to().tableName(roleTable).primaryKey("roleid").cond("roleid", roleID);

		return roleMap;
	}

	protected DataMapBuilder mapUserRoleData(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder userRoleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "simple_user_role";
		userRoleMap.from().tableName(roleTable).primaryKey("userid", "roleid").cond("roleid", roleID);
		userRoleMap.to().tableName(roleTable).primaryKey("userid", "roleid").cond("roleid", roleID);

		return userRoleMap;
	}
	
}
