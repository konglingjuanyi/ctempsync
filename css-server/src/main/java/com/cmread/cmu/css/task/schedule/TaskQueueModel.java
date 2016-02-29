package com.cmread.cmu.css.task.schedule;

import com.cmread.cmu.css.task.executor.TaskLifecycle;

/**
 * 同步任务排队策略
 * 
 * 注意，这个接口的实现仅需负责任务互斥排队，不负责优先级执行；
 * 
 * 名字不好
 * MutuxTaskQueue？
 * RelatedTaskMutuxQueue？
 * 
 * @author zhangtieying
 *
 */
public interface TaskQueueModel {

	void addTask(TaskLifecycle task);

	void taskFinish(TaskLifecycle task);

}
