package com.cmread.cmu.css.task;

import java.util.Date;

public class SyncTaskRunningRecord {

	private Date startTime;
	private Date finishTime;
	
	public void setStartTime(Date date) {
		this.startTime = date;
	}

	public void setFinishTime(Date time) {
		this.finishTime = time;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

}
