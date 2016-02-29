package com.cmread.cmu.css.task;

import java.util.Date;

public class SyncTaskSourceInfo {

	private String clientIp;
	private Date clientStartTime;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setClientStartTime(Date clientStartTime) {
		this.clientStartTime = clientStartTime;
	}
	
	public String getClientIp() {
		return this.clientIp;
	}

	public Date getClientStartTime() {
		return this.clientStartTime;
	}

}
