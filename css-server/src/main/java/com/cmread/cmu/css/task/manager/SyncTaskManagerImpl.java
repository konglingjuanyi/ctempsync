package com.cmread.cmu.css.task.manager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.SyncTaskImpl;
import com.cmread.cmu.css.task.SyncTaskManager;
import com.cmread.cmu.css.task.TaskConfig;
import com.cmread.cmu.css.task.TaskException;
import com.cmread.cmu.css.task.TaskSourceInfo;
import com.cmread.cmu.css.task.config.TaskConfigManager;
import com.cmread.cmu.css.task.schedule.TaskEventListener;
import com.cmread.cmu.css.task.schedule.TaskEventNotifier;
import com.cmread.cmu.css.task.schedule.TaskLifecycleEvent;

public class SyncTaskManagerImpl implements SyncTaskManager, TaskEventListener {

	private static Logger logger = LoggerFactory.getLogger(SyncTaskManagerImpl.class);
	
	private TaskConfigManager taskConfigManager;
	private SyncTaskPersistentStrategy taskPersistent;
	private SqlTaskExecutorServiceRegistry sqlManager;
	
	public void setSqlExecutorManager(SqlTaskExecutorServiceRegistry sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setTaskPersistent(SyncTaskPersistentStrategy taskSaver) {
		this.taskPersistent = taskSaver;
	}
	
	public void setTaskEventNotifier(TaskEventNotifier taskEventNotifier) {
		taskEventNotifier.addListener(this);
	}
	
	public void setTaskConfigManager(TaskConfigManager configManager) {
		this.taskConfigManager = configManager;
	}
	
	@Override
	public SyncTask createTask(String taskType, Map<String, String> contentID, TaskSourceInfo source)
			throws TaskException {
		if (logger.isInfoEnabled()) {
			String contentStr = StringUtils.collectionToCommaDelimitedString(contentID.entrySet());
			logger.info("receive new task request. type=[{}] content=[{}]", taskType, contentStr);
		}
		
		TaskConfig taskConfig = taskConfigManager.get(taskType);

		if (taskConfig == null) {
			logger.warn("task type '{}' not exist.", taskType);
			return null;
		}

		try {
			SyncTaskImpl task = createTask(taskConfig, contentID, source);
			task.setSqlManager(this.sqlManager);
			
			return task;
		} catch (Exception e) {
			throw new TaskException("create task failed.", e);
		}
	}

	/**
	 * 创建新的任务；任务ID全局唯一，采用UUID来实现吧！！！任务的排序通过数据库记录插入时间为准；
	 * @param taskConfig
	 * @param contentID
	 * @param source
	 * @return
	 */
	private SyncTaskImpl createTask(TaskConfig taskConfig, Map<String, String> contentID, TaskSourceInfo source) {
		SyncTaskImpl task = new SyncTaskImpl(taskConfig, contentID, source);

		SyncTaskPersistentStrategy thisTaskPersistent = selectTaskPersistent(taskConfig);
		thisTaskPersistent.create(task);
		
		return task;
	}
	
	private SyncTaskPersistentStrategy selectTaskPersistent(TaskConfig taskConfig) {
		if (taskConfig.isPersistent()) {
			return taskPersistent;
		} else {
			return new SyncTaskNotPersistent();
		}
	}

	//synctask更新执行记录，
	//通知persistent执行持久化（随便他愿不愿意）
	//start的时候创建一个新的执行记录
	//结束的时候结束执行记录
	//持久化根据当前最新执行记录决定是否更新
	@Override
	public void onEvent(TaskLifecycleEvent event) {
		SyncTask syncTask = (SyncTask)event.getTask();
		
		switch (event.getType()) {
		case BEGIN:
			syncTask.begin(event.getTime());
			break;
		case FINISH:
			syncTask.finish(event.getTime(), event.isSuccess(), event.isRetrying());
			break;
		}
		
		SyncTaskPersistentStrategy thisTaskPersistent = selectTaskPersistent(syncTask.getConfig());
		thisTaskPersistent.update(syncTask);
	}

}
