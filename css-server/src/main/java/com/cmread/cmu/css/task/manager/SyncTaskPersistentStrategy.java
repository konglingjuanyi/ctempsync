package com.cmread.cmu.css.task.manager;

import com.cmread.cmu.css.task.SyncTask;

public interface SyncTaskPersistentStrategy {

	void create(SyncTask task);

	void update(SyncTask task);

}
