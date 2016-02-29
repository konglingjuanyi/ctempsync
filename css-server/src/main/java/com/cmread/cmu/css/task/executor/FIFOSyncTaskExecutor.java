package com.cmread.cmu.css.task.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 顺序执行同步任务，忽略任务优先级参数
 * 
 * @author zhangtieying
 *
 */
public class FIFOSyncTaskExecutor implements TaskExecutor {

	private ExecutorService taskExecutor;
	
	public FIFOSyncTaskExecutor() {
		this.taskExecutor = Executors.newSingleThreadExecutor();
	}
	
	@Override
	public void executeTask(TaskLifecycle task) {
		this.taskExecutor.submit(new RunnableTask(task));
	}

}
