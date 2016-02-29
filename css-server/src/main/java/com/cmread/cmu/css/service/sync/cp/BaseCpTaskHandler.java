package com.cmread.cmu.css.service.sync.cp;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/*
 * 说明：这里传入的cpid实际上应该是该cp的object_id，不是表中的cpid；
 * FIXME ：为了不弄混，最好改为object_id;
 */
public abstract class BaseCpTaskHandler implements TaskHandler {

	protected String getCpID(SyncTask task) {
		return task.getContent().getMap().get("cpid");
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "cp-" + getCpID(task);
	}
	
	@Override
	public TaskResult exec(SyncTask task) {
		String cpID = getCpID(task);
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		// cms -> bks
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		
		mapCommonToBookAction(cpID, commonToBookJob, task);

		tb.addSubJob(commonToBookJob);

		// cms -> ccs
		OneToOneJobBuilder commonToCartoonJob = tb.createOneToOneJob();
		commonToCartoonJob.fromDB("common");
		commonToCartoonJob.toDB("cartoon");
		
		mapCommonToCartoonAction(cpID, commonToCartoonJob, task);

		tb.addSubJob(commonToCartoonJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}
	
	public abstract void mapCommonToBookAction(String cpID, OneToOneJobBuilder subJob, SyncTask task);
	public abstract void mapCommonToCartoonAction(String cpID, OneToOneJobBuilder subJob, SyncTask task);
	
	protected DataMapBuilder mapCpInfo(String cpID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder(); 
		
		String tableName = "t_cp_info";
		dataMap.from().tableName(tableName).primaryKey("object_id").cond("object_id", cpID);
		dataMap.to().tableName(tableName).primaryKey("object_id").cond("object_id", cpID);

		return dataMap;
	}

	/*
	 * 这个表的特别之处在于：
	 * 这个表是以键值对的方式来扩展cp信息，比如要给cp增加一个叫"cpName"的属性，那么就在这个表中增加一条
	 * [object_id]为cpID，[name]等于"cpName"的记录。
	 * 所以这个表与cp_info表的关系是一对多的关系；
	 */
	protected DataMapBuilder mapCpInfoExt(String cpId, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String tableName = "t_cp_infoext";
		dataMap.from().tableName(tableName).primaryKey("object_id", "name").cond("object_id", cpId);
		dataMap.to().tableName(tableName).primaryKey("object_id", "name").cond("object_id", cpId);

		return dataMap;
	}

	// 还需要映射operator表和user_role两个表 FIXME 需要测试
	// 主要需要确认operid和cpid是不是同一个id

	/*
	 * cp角色表（这个和operator的含义是一致的） 
	 * FIXME 这个定义也和operator的重复，DRY
	 */
	protected DataMapBuilder mapUserRoleData(String cpID, OneToOneJobBuilder subJob) {
		DataMapBuilder userRoleMap = subJob.createDataMapBuilder(); 
		
		String roleTable = "t_bme_user_role";
		userRoleMap.from().tableName(roleTable).primaryKey("operid", "roleid").cond("operid", cpID);
		userRoleMap.to().tableName(roleTable).primaryKey("operid", "roleid").cond("operid", cpID);

		return userRoleMap;
	}
}
