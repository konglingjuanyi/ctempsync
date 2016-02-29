package com.cmread.cmu.css.task.executor;

// 这里有问题，待纠正
public interface TaskLiftcycleListener {

	void onStart(TaskLifecycle task);
	void onFinish(TaskLifecycle task);
	void onFailedRetry(TaskLifecycle taskLiftcycle);
	void onFailed(TaskLifecycle taskLiftcycle);
	
}
