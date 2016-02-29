package com.cmread.cmu.css.task;

import java.util.Map;

import com.cmread.cmu.css.task.schedule.TaskEngine;

/**
 * 
 * 同步任务服务器端的主入口，外部任务请求都从此入口进入
 * 后面可以考虑将createTask提取为接口
 * 
 * @author zhangtieying
 *
 */
public class SyncTaskServer {
	
	private SyncTaskManager taskManager;
	private TaskEngine taskEngine;
	
	/**
	 * 初始化方法
	 * 1. 从taskManager中获取未完成的任务，初始化并在内部触发（这些任务应该是启动前创建，属于异常情况）；
	 * 2. 调度执行这些任务
	 * 3. 完成后再处理外部请求
	 */
	public void init() {
		synchronized (this) {
			
		}
	}
	
	public SyncTask createTask(String taskType, Map<String, String> contentID, TaskSourceInfo source) throws TaskException {
		// 创建任务
		SyncTask task = taskManager.createTask(taskType, contentID, source);
		
		if (task == null) {
			return null; //任务类型不存在；
		}
		
		try {
			// 将任务交给调度器去执行；
			taskEngine.schedule(task);
	
			return task;
		} catch (Exception e) {
			// 一般不应该走到这里的... 调度器是异步执行的，放到队列不应该出现问题；
			throw new TaskException(String.format("schedule task failed"), e);
		}
	}

	public void setSyncTaskManager(SyncTaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void setTaskEngine(TaskEngine taskEngine) {
		this.taskEngine = taskEngine;
	}

}
