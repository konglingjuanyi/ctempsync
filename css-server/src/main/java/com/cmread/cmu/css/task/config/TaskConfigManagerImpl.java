package com.cmread.cmu.css.task.config;

import java.util.ArrayList;
import java.util.List;

import com.cmread.cmu.css.task.TaskConfig;

public class TaskConfigManagerImpl implements TaskConfigManager {

	private List<TaskConfig> taskConfigList;

	public TaskConfigManagerImpl() {
		this.taskConfigList = new ArrayList<>();
	}
	
	public void setTaskConfigList(List<TaskConfig> taskConfigList) {
		this.taskConfigList.addAll(taskConfigList);
	}

	@Override
	public TaskConfig get(String taskType) {
		for (TaskConfig taskConfig : taskConfigList) {
			if (taskConfig.getTaskType().equals(taskType)) {
				return taskConfig;
			}
		}
		return null;
	}

	@Override
	public void add(TaskConfig taskConfig) {
		this.taskConfigList.add(taskConfig);
	}
}
