package com.cmread.cmu.css.task.schedule;

import java.util.Date;

public class TaskLifecycleEvent {

	public static enum EventType {
		BEGIN, FINISH;
	}
	
	private Task task;
	private boolean success;
	private Date time;
	private EventType type;
	private boolean retrying;
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public boolean isRetrying() {
		return retrying;
	}
	public void setRetrying(boolean retrying) {
		this.retrying = retrying;
	}
	
}
