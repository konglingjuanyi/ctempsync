package com.cmread.cmu.css.task.schedule;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cmread.cmu.css.task.executor.TaskExecutor;
import com.cmread.cmu.css.task.executor.TaskLifecycle;

/**
 * 所有任务都在一个队列中排队；
 * 这个任务排队策略会忽略任务优先级，按照先入先出策略进行任务调度，同一时间内仅有一个任务
 * 会被执行；
 * 
 * @author zhangtieying
 *
 */
public class FIFOTaskQueue implements TaskQueueModel {

	private List<TaskLifecycle> taskList;
	private TaskLifecycle runningTask;
	
	private ExecutorService scheduleExecutor;
	
	private TaskExecutor taskExecutor;
	
	public FIFOTaskQueue() {
		this.taskList = new LinkedList<TaskLifecycle>();
		this.scheduleExecutor = Executors.newSingleThreadExecutor();
		this.runningTask = null;
	}
	
	@Override
	public void addTask(final TaskLifecycle task) {
		this.scheduleExecutor.submit(new Runnable() {
			@Override
			public void run() {
				onTaskAdd(task);
			}
		});
	}

	/**
	 * 任务执行完毕，可以调度下一个任务了；
	 */
	@Override
	public void taskFinish(final TaskLifecycle task) {
		this.scheduleExecutor.submit(new Runnable() {
			@Override
			public void run() {
				onTaskFinish(task);
			}
		});
	}

	private void onTaskAdd(TaskLifecycle task) {
		// 新任务加到队尾
		taskList.add(task);
		
		if (runningTask == null) {
			// 取出队头任务，调度执行
			runningTask = taskList.remove(0);
			taskExecutor.executeTask(runningTask);
		}
	}
	
	private void onTaskFinish(TaskLifecycle task) {
		// 调度下一个任务执行，没有的话设置标志位
		if (taskList.size() > 0) {
			runningTask = taskList.remove(0);
			taskExecutor.executeTask(runningTask);
		} else {
			runningTask = null;
		}
	}
	
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
