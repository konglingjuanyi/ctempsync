package it.demo.ownedplace;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public class OwnedPlaceSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "role-test";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		String roleID = task.getContent().getMap().get("id");
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("book");
		commonToBookJob.toDB("common");
		
		Action userRoleAction = mapRoleData(roleID, commonToBookJob).diffSyncAction();
		Action userRoleAction1 = mapRoleData1(roleID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(userRoleAction).next(userRoleAction1);
		
		tb.addSubJob(commonToBookJob);
		
		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}

	protected DataMapBuilder mapRoleData(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "test_ownedplace_book";
		roleMap.from().tableName(roleTable).primaryKey("id");
		roleMap.to().tableName(roleTable).primaryKey("id");

		return roleMap;
	}
	
	protected DataMapBuilder mapRoleData1(String roleID, OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "test_common_ownedplace";
		roleMap.from().tableName(roleTable).primaryKey("id");
		roleMap.to().tableName(roleTable).primaryKey("id");

		return roleMap;
	}
}