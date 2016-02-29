package com.cmread.cmu.css.http;

import java.util.Map;

public class SyncTaskResponse {

	private String taskID;
	private Map<String, String> result;

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}	
	
	public Map<String, String> getResult() {
		return this.result;
	}
	
	public void setResult(Map<String, String> result) {
		this.result = result;
	}
}
