package com.cmread.cmu.css.task.schedule;

/**
 * 任务调度器
 * 
 * 任务执行的入口，负责任务的互斥排队、优先级执行、失败重试策略等；
 * 
 * 调度器负责实际触发任务的执行，实际上，调度器是整个同步模块中需要保持互斥且唯一的组件。后续在实现
 * 同步进程的集群方案或者热备方案时，主要考虑调度器的集群或者热备即可。
 * 
 * @author zhangtieying
 *
 */
public interface TaskEngine {

	void schedule(Task task);

}
