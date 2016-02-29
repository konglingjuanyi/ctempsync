package com.cmread.cmu.css.service.sync.usergroup;

import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskResult;

/*
 * 删除推荐单元
 * 参数：groupID
 */
public class RecommendUnitDelete extends UserGroupDiffSync {

	public TaskResult exec(SyncTask task) {
		TaskResult result = super.exec(task);

		// 删除组成功后，执行一系列清除操作；
		if (result == TaskResult.SUCCESS) {
			SqlExecutor bookSqlExec = task.getSqlExecutor("book");

			try {
				// 删除推荐单元规则
				BaseRecommendGroupHelper.deleteRecommendUnitRule(getUserGroupID(task), bookSqlExec);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
		}
		return result;
	}
	
}
