package com.cmread.cmu.css.http.client;

import java.util.Map;

import com.cmread.cmu.css.http.SyncTaskResponse;

public interface CSSClient {

	/*
	 * 客户端创建同步任务；
	 * 
	 * 方法返回，表示任务请求已经发送到服务器（不代表执行完）
	 */
	SyncTaskResponse createTask(String taskType, Map<String, String> contentID) throws RemoteTaskException;
	
}
