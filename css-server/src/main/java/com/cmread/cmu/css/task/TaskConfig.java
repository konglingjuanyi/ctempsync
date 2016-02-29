package com.cmread.cmu.css.task;

public interface TaskConfig {
	
	String getTaskType();
	
	TaskHandler getTaskHandler();
	
	boolean isPersistent();
	
	int getPriority();
	
/*	
	String getTaskClass();

	String getFromDBName();

	String[] getToDBNames();

	

	
*/
	/*
	 * 返回配置中的任务关联字符串，如果配置中没有指定，则可以返回null；
	 */
/*
	String getRelatedKey();

	OneToOneJob[] getJobs();
	void setJobs(OneToOneJob[] jobs);
*/
}