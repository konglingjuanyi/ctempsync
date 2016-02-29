package com.cmread.cmu.css.task.schedule;

import java.util.Date;

import com.cmread.cmu.css.task.executor.TaskLifecycle;
import com.cmread.cmu.css.task.executor.TaskLiftcycleListener;

public class TaskEngineImpl implements TaskEngine, TaskLiftcycleListener {

	//private static Logger logger = LoggerFactory.getLogger(TaskEngineImpl.class);
	
	private TaskQueueModel taskQueue; //FIXME 名字不恰当，需要修改；
	private TaskEventNotifier taskEventNotifier;
	
	public void setTaskQueueModel(TaskQueueModel taskQueue) {
		this.taskQueue = taskQueue;
	}
	
	public void setTaskEventNotifier(TaskEventNotifier taskEventNotifier) {
		this.taskEventNotifier = taskEventNotifier;
	}
	
	/**
	 * 有新任务，调度执行该任务；
	 */
	public void schedule(Task task) {
		this.taskQueue.addTask(new TaskLifecycle(task, this));
	}

	// **** 以下方法主要接收同步任务执行结构，以便进行刷新状态，或者触发重新调度等 *******
	
	/**
	 * 任务开始执行
	 */
	@Override
	public void onStart(TaskLifecycle task) {
		//发送开始通知
		this.taskEventNotifier.taskBegin(task.getTask(), new Date());
	}

	/**
	 * 任务成功结束
	 */
	@Override
	public void onFinish(TaskLifecycle task) {
		//通知排队策略对象（排队策略可以重新调度任务了）
		this.taskQueue.taskFinish(task);
		
		//发送任务结束通知
		this.taskEventNotifier.taskFinish(task.getTask(), new Date(), true, false);
	}

//	/**
//	 * 任务执行失败（一次失败）
//	 * 启动redo策略
//	 * redo结束后，发送任务失败结束通知；redo没结束，发送失败redo通知；
//	 * 
//	 * redo问题在于，重启动的任务，还应该排在相应队列前面！！！！
//	 * 
//	 * TODO 以后应该补充，失败原因（分类，或者enum）；部分失败情况可以不用重试了！！！
//	 * 感觉除了网络异常都没必要重试！！！
	 
//	@Override
//	public void onFailed(TaskLiftcycle task) {
//		//目前假设没有redo
//		this.taskQueue.taskFinish(task);
//		
//		//发送任务结束通知 (重试的时候还应该有重试通知, 第四个参数应该为true）
//		// FIXME 参数较多，改为Event可能更加合适
//		this.taskEventNotifier.taskFinish(task.getTask(), new Date(), false, false);
//	}

	public void onFailed(TaskLifecycle task) {
		this.taskQueue.taskFinish(task);
		this.taskEventNotifier.taskFinish(task.getTask(), new Date(), false, false);
	}
	
	/**
	 * 当前实现先忽略掉redo！！！
	 */
	@Override
	public void onFailedRetry(TaskLifecycle taskLiftcycle) {
		this.taskQueue.taskFinish(taskLiftcycle);
		this.taskEventNotifier.taskFinish(taskLiftcycle.getTask(), new Date(), false, false);
	}

}
