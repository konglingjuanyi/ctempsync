package com.cmread.cmu.css.service.sync.sysconfig;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseSysConfigTaskHandler implements TaskHandler {
	
	protected String getSysConfigKey(SyncTask task) {
		return task.getContent().getMap().get("key");
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "sysconfig";
	}
	
	@Override
	public TaskResult exec(SyncTask task) {
		String key = getSysConfigKey(task);
		
		TaskHandlerBuilder tb = new TaskHandlerBuilder();
		
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");
		
		mapCommonToBookAction(key, commonToBookJob);

		tb.addSubJob(commonToBookJob);
		
		OneToOneJobBuilder commonToCartoonJob = tb.createOneToOneJob();
		commonToCartoonJob.fromDB("common");
		commonToCartoonJob.toDB("cartoon");
		
		mapCommonToCartoonAction(key, commonToCartoonJob);

		tb.addSubJob(commonToCartoonJob);
		
		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);
		
		return autoMapHandler.exec();
	}
	
	public abstract void mapCommonToBookAction(String key, OneToOneJobBuilder subJob);
	public abstract void mapCommonToCartoonAction(String key, OneToOneJobBuilder subJob);

	protected DataMapBuilder mapSysConfig(String key, OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		String tableName = "sup_sys_config";
		roleMap.from().tableName(tableName).primaryKey("key").cond("key", key);
		roleMap.to().tableName(tableName).primaryKey("key").cond("key", key);

		return roleMap;
	}
	
	protected DataMapBuilder mapSysConfigTable(OneToOneJobBuilder subJob) {
		DataMapBuilder roleMap = subJob.createDataMapBuilder(); 
		String tableName = "sup_sys_config";
		roleMap.from().tableName(tableName).primaryKey("key");
		roleMap.to().tableName(tableName).primaryKey("key");

		return roleMap;
	}
}
