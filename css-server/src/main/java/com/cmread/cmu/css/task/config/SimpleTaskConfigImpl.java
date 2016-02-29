package com.cmread.cmu.css.task.config;

import com.cmread.cmu.css.task.TaskConfig;
import com.cmread.cmu.css.task.TaskHandler;

public class SimpleTaskConfigImpl implements TaskConfig {

	private String taskType;
	
	private boolean persistent;
	
	private int priority;
	
	private TaskHandler taskHandler;
	
	private TaskConfigManager taskConfigManager;
	
	public SimpleTaskConfigImpl() {
		setPersistent(true);  // 默认为true;
		setPriority(5);
	}
	
	@Override
	public String getTaskType() {
		return this.taskType;
	}

	@Override
	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public TaskHandler getTaskHandler() {
		return this.taskHandler;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType;
		registeSelfToManager();
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	
	public void setTaskHandler(TaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}
	
	// 用于注册自己到config-manager中
	public void setTaskConfigManager(TaskConfigManager taskConfigManger) {
		this.taskConfigManager = taskConfigManger;
		registeSelfToManager();
	}
	
	private void registeSelfToManager() {
		if ((this.taskConfigManager != null) && (this.taskType != null)) {
			this.taskConfigManager.add(this);
		}
	}
}
