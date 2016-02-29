package com.cmread.cmu.css.task.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.task.TaskLogContext;
import com.cmread.cmu.css.task.TaskResult;

/**
 * 可运行任务
 * 
 * 将TaskLiftcycle包装为Runnable对象，并在run方法中实现Task的执行，同时完成对TaskLifecycle的生命周期方法的调用。
 * 
 * @author zhangtieying
 *
 */
@SuppressWarnings("rawtypes")
public class RunnableTask implements Runnable, Comparable {
	
	private static Logger logger = LoggerFactory.getLogger(RunnableTask.class);
	
	private TaskLifecycle taskLifecycle;
	
	public RunnableTask(TaskLifecycle taskLifecycle) {
		this.taskLifecycle = taskLifecycle;
	}
	
	public TaskLifecycle getTaskLifecycle() {
		return this.taskLifecycle;
	}
	
	@Override
	public void run() {
		TaskLogContext.setContext(taskLifecycle.getTask());
		try {
			logger.debug("task start to run");
			taskLifecycle.start();
			TaskResult result = taskLifecycle.getTask().exec();

			switch (result) {
			case SUCCESS:
				logger.debug("task finished. result is {}", result.toString());
				taskLifecycle.finish();
				break;
			case FAILED:
				logger.error("task finished. result is {}", result.toString());
				taskLifecycle.failed();
				break;
			case FAILED_RETRY:
				logger.error("task finished. result is {}", result.toString());
				taskLifecycle.finish_retry();
				break;
			}
		} catch (Throwable e) {
			logger.error("task finished. result is FAILED. reason is uncatched Exception.", e);
			taskLifecycle.failed(); //FIXME 这一点有疑义，最好task能保证自己返回要求的值
		} finally {
			TaskLogContext.clear();
		}
	}

	@Override
	public int compareTo(Object o) {
		RunnableTask other = (RunnableTask)o;
		
		return this.getTaskLifecycle().getTask().compareTo(other.getTaskLifecycle().getTask());
	}

}
