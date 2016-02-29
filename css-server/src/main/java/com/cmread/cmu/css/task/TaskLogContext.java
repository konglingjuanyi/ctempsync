package com.cmread.cmu.css.task;

import org.slf4j.MDC;

import com.cmread.cmu.css.task.schedule.Task;

public class TaskLogContext {

	public static void setContext(Task task) {
		MDC.put("traceID", task.getTaskID());
	}
	
	public static void clear() {
		MDC.clear();
	}
}
