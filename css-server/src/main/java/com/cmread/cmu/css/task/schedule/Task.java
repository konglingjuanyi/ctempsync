package com.cmread.cmu.css.task.schedule;

import com.cmread.cmu.css.task.TaskResult;

public interface Task extends Comparable<Object> {

	String getTaskID();
	/**
	 * @return 任务关联的key，相同key的任务在一个队列中排队执行；返回null表示没有互斥，可以并发执行
	 */
	String getRelatedKey();

	TaskResult exec();
}
