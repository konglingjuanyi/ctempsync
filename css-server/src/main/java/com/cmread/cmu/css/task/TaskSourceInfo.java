package com.cmread.cmu.css.task;

import java.util.Date;

/**
 * 任务来源信息。包括任务发起方的ip、发起时间以及原始请求；
 * 
 * 这些信息主要是为故障时排除使用
 * 
 * @author zhangtieying
 *
 */
public class TaskSourceInfo {

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
