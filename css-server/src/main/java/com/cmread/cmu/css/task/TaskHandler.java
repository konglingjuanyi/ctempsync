package com.cmread.cmu.css.task;

/**
 * 单例类！！！
 * 
 * @author zhangtieying
 *
 */
public interface TaskHandler {

	String getRelatedKey(SyncTask task);

	TaskResult exec(SyncTask task);

}
