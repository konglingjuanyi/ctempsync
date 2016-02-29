package com.cmread.cmu.css.task;

import java.util.Date;

public class SyncTaskPriority implements Comparable<Object> {

	protected int priority;
	protected Date createTime;
	
	public SyncTaskPriority(int priority, Date createTime) {
		this.priority = priority;
		this.createTime = createTime;
	}
	
	@Override
	public int compareTo(Object o) {
		SyncTaskPriority other = (SyncTaskPriority)o;
		
		int prio = this.priority - other.priority;
		if (prio == 0) {
			long timeDiff = this.createTime.getTime() - other.createTime.getTime();
			return (timeDiff < 0) ? 1 : ((timeDiff == 0) ? 0 : -1);
		}
		return prio;
	}
	
}