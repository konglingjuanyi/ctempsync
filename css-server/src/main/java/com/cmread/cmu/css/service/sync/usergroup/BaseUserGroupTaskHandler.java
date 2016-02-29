package com.cmread.cmu.css.service.sync.usergroup;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.DataMap.Filter;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseUserGroupTaskHandler implements TaskHandler {

	protected String getUserGroupID(SyncTask task) {
		return task.getContent().getMap().get("usergroupid");
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "usergroup-" + getUserGroupID(task);
	}
	
	@Override
	public TaskResult exec(SyncTask task) {
		String userGroupID = getUserGroupID(task);
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		
		mapCommonToBookAction(userGroupID, commonToBookJob);

		tb.addSubJob(commonToBookJob);
		
		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}
	
	public abstract void mapCommonToBookAction(String userGroupID, OneToOneJobBuilder subJob);

	protected DataMapBuilder mapUserGroup(String userGroupID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder(); 
		
		dataMap.from().tableName("sup_usergroup_info").primaryKey("groupid").cond("groupid", userGroupID);
		dataMap.to().tableName("bks_usergroup_info").primaryKey("groupid").cond("groupid", userGroupID);

		return dataMap;
	}

	/*
	 * 这个映射关系的特别之处在于，book里面的表增加了一个ID自增字段作为主键，这个字段在common中是没有的；
	 * 在book中插入记录时，需要调用自增序列来创建该值； 
	 * 目前自增序列使用“hibernate_sequence.nextval”
	 * 
	 */
	protected DataMapBuilder mapUserGroupMember(String userGroupID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("sup_usergroup_member").primaryKey("group_id", "member_id")
				.cond("group_id", userGroupID).addFilter(new Filter() {
					@Override
					public void filter(RowSet rowSet) {
						for (Row row : rowSet.getRows()) {
							row.addExtraFieldAtInsert("ID", "hibernate_sequence.nextval");
						}
					}
				});
		dataMap.to().tableName("bks_usergroup_member").primaryKey("group_id", "member_id").cond("group_id",
				userGroupID);

		return dataMap;
	}
	
	protected DataMapBuilder mapUserPrivileges(String userGroupID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder(); 
		
		String tableName = "sup_group_user_privileges";
		dataMap.from().tableName(tableName).primaryKey("userid", "groupid", "roleid").cond("groupid", userGroupID);
		dataMap.to().tableName(tableName).primaryKey("userid", "groupid", "roleid").cond("groupid", userGroupID);

		return dataMap;
	}
}
