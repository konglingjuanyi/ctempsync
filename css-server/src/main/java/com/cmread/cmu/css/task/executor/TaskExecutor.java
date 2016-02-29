package com.cmread.cmu.css.task.executor;

import com.cmread.cmu.css.task.executor.TaskLifecycle;

public interface TaskExecutor {

	void executeTask(TaskLifecycle task);
}
