package com.cmread.cmu.css.task.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cmread.cmu.css.utils.ComparableThreadPoolExecutor;
import com.cmread.cmu.css.utils.NamingThreadFactory;

/**
 * 支持任务优先级执行；
 * 
 * 记录：
 * 1. 通过优先级队列优先处理优先级高的任务；
 * 2. 优先级高的任务，处理过程中调用有优先级的sql执行器（支持优先级）；ps，这个是有另外的sql执行器确保的，和这个没有什么关系
 * 
 * @author zhangtieying
 *
 */
public class PrioritySyncTaskExecutor implements TaskExecutor {

	private int threadPoolSize; 
	
	private ThreadPoolExecutor taskExecutor;

	public void setThreadPoolSize(int size) {
		this.threadPoolSize = size;
		this.taskExecutor.setCorePoolSize(size);
		this.taskExecutor.setMaximumPoolSize(size);
	}

	public PrioritySyncTaskExecutor() {
		this.threadPoolSize = 20;

		//注意：优先级队列是无限长队列！！！
		BlockingQueue<Runnable> taskQueue = new PriorityBlockingQueue<Runnable>(100); 

		this.taskExecutor = new ComparableThreadPoolExecutor(threadPoolSize, threadPoolSize, 60L, TimeUnit.SECONDS,
				taskQueue, NamingThreadFactory.createThreadFactory("task-pool-%d"));
	}
	
	@Override
	public void executeTask(final TaskLifecycle task) {
		this.taskExecutor.submit(new RunnableTask(task));
	}
	
	/*
	 * 关闭资源
	 */
	public void close() {
		this.taskExecutor.shutdown();
	}


}
