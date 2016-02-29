package com.cmread.cmu.css.task.executor;

import com.cmread.cmu.css.task.schedule.Task;

public class TaskLifecycle {
	
	//private static Logger logger = LoggerFactory.getLogger(TaskLifecycle.class);
	
	private Task task;
	private TaskLiftcycleListener listener;
	
	private String taskRelatedKeyCache; //缓存task的这个值

	public TaskLifecycle(Task task, TaskLiftcycleListener listener) {
		this.task = task;
		this.listener = listener;
		this.taskRelatedKeyCache = null;
	}

	public Task getTask() {
		return this.task;
	}

	public TaskLiftcycleListener getListener() {
		return listener;
	}
	
	public void start() {
		this.listener.onStart(this);
	}
	
	public void finish() {
		this.listener.onFinish(this);
	}
	
	//这里应该加入失败原因
	public void finish_retry() {
		this.listener.onFailedRetry(this);
	}

	public void failed() {
		this.listener.onFailed(this);
	}

	/*
	 * 这里加一个relatedkey的缓存，主要是确保这个key仅取一次，并且值必须相同，否则会影响排队调度，甚至阻塞某种类型的队列；
	 */
	public String getRelatedKey() {
		if (this.taskRelatedKeyCache == null) {
			this.taskRelatedKeyCache = this.task.getRelatedKey();
		}
		return this.taskRelatedKeyCache;
	}
}
