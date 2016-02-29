package com.cmread.cmu.css.task.config;

import com.cmread.cmu.css.task.TaskConfig;

public interface TaskConfigManager {

	TaskConfig get(String taskType);

	void add(TaskConfig taskConfig);
}
