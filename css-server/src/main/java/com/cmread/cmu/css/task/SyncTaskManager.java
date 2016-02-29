package com.cmread.cmu.css.task;

import java.util.Map;

public interface SyncTaskManager {

	/**
	 * 
	 * @param taskType
	 * @param contentID
	 * @param source
	 * @return null 表示Task不存在；
	 * @throws TaskException
	 */
	SyncTask createTask(String taskType, Map<String, String> contentID, TaskSourceInfo source) throws TaskException;

}
