package com.cmread.cmu.css.task;

import java.util.Map;

public class TaskParamter {
	
	private Map<String, String> content;
	
	public TaskParamter(Map<String, String> content) {
		this.content = content;
	}
	
	public Map<String, String> getMap() {
		return this.content;
	}
}
