package com.cmread.cmu.css.task.schedule;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.task.TaskLogContext;
import com.cmread.cmu.css.task.executor.TaskExecutor;
import com.cmread.cmu.css.task.executor.TaskLifecycle;

/**
 * 按照遍历来实现吧？
 * 
 * OrderedTaskHandler
 * TaskOrderdHandler
 * TaskPoolHandler
 * TaskPriorityPoolHandler
 * TaskExecutorService 
 * 
 * 单线程：主要为了任务创建者可以立刻返回，无需等待；
 * 
 * FIXME : 没有处理空值的情况
 * 
 * @author zhangtieying
 *
 */
public class ContentRelatedTaskQueue implements TaskQueueModel {

	private static Logger logger = LoggerFactory.getLogger(ContentRelatedTaskQueue.class);
	
	private Map<String, List<TaskLifecycle>> tasks;
	private long currentTaskNumbers;
	private long totalTaskNumbers;
	
	private ExecutorService scheduleExecutor;
	
	private TaskExecutor taskExecutor;
	
	public ContentRelatedTaskQueue() {
		// 因为用单线程来实现调度，所以这里不需要并发容器，但如果不用单线程，则需要保护；
		this.tasks = new HashMap<>();
		this.currentTaskNumbers = 0;
		this.totalTaskNumbers = 0;
		this.scheduleExecutor = Executors.newSingleThreadExecutor(); 
	}
	
	@Override
	public void addTask(final TaskLifecycle task) {
		this.scheduleExecutor.submit(new Runnable() {
			@Override
			public void run() {
				currentTaskNumbers++;
				totalTaskNumbers++;
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
				currentTaskNumbers--;
				onTaskFinish(task);
			}
		});
	}

	/*
	 * FIXME 这几个方法里绝对有个大Bug，没时间想... 兄弟，你要暂时先顶住啊！！！
	 */
	private void onTaskAdd(TaskLifecycle task) {
		TaskLogContext.setContext(task.getTask());
		try {
			logger.debug("task schedule stat : [current-tasks:{}] [total-tasks:{}]", 
					this.currentTaskNumbers, this.totalTaskNumbers);
			
			// 检查当前map中是否已经存在相同的内容队列；
			String relatedContentID = getRelatedContentID(task);
			if (relatedContentID == null) {
				// 未指定，可认为不需要相关性排队，直接跳过
				logger.debug("related-key is null, skip task queue");
				taskExecutor.executeTask(task);
				return;
			}

			List<TaskLifecycle> relatedTasks = tasks.get(relatedContentID);

			if (relatedTasks == null) {
				relatedTasks = new LinkedList<>();
				relatedTasks.add(task); // 实际上是队头；
				tasks.put(relatedContentID, relatedTasks);
				logger.debug("related-key is {}, no other task queue", relatedContentID);
				taskExecutor.executeTask(task);
				logger.debug("add to task executor success");
			} else {
				// 已经存在，表示有一个任务已经在运行了，将当前任务放在队列尾
				logger.debug("related-key is {}, wait in line. queue list size is {}", relatedContentID,
						relatedTasks.size());
				relatedTasks.add(task); // 返回了
			}
		} catch (Exception e) {
			logger.error("task queueing [on-add-task] failed." , e);
		} finally {
			TaskLogContext.clear();
		}
	}
	
	private void onTaskFinish(TaskLifecycle task) {
		TaskLogContext.setContext(task.getTask());
		try {
			logger.debug("task schedule stat : [current-tasks:{}] [total-tasks:{}]", 
					this.currentTaskNumbers, this.totalTaskNumbers);
			
			// 如果这个地方失败，会导致后面task阻塞！！！
			String relatedContentID = getRelatedContentID(task);

			if (relatedContentID == null) {
				// 未指定相关联内容，不会触发后续动作
				logger.debug("related-key is null, no next task.");
				return;
			}

			// 肯定不为null
			List<TaskLifecycle> relatedTasks = tasks.get(relatedContentID);
			relatedTasks.remove(0); // 取出自己
			if (relatedTasks.size() > 0) {
				// 取出队头执行（不删除）
				TaskLifecycle nextTask = relatedTasks.get(0);
				logger.debug("reschedule next related task {}", nextTask.getTask().getTaskID());
				taskExecutor.executeTask(nextTask);
			} else {
				logger.debug("no next related task exist.");
				// 没有后续同步内容了
				this.tasks.remove(relatedContentID);
			}
		} catch (Exception e) {
			logger.error("task queueing [on-task-finish] failed.", e);
		} finally {
			TaskLogContext.clear();
		}
	}

	private String getRelatedContentID(TaskLifecycle task) {
		try {
			return task.getRelatedKey();
		} catch (Exception e) {
			logger.warn("get related content id failed.", e);
			return null;
		}
	}
	
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}	
	
	public void close() {
		this.scheduleExecutor.shutdown();
	}
}
