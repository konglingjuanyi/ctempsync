package com.cmread.cmu.css.http;

import java.util.Date;
import java.util.Map;

public class SyncTaskRequest {

	private String taskType;
	private Map<String, String> content;
	private Date createTime;
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public Map<String, String> getContent() {
		return content;
	}
	public void setContent(Map<String, String> content) {
		this.content = content;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
