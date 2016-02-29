package com.cmread.cmu.css.service.sync.usergroup;

import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskResult;

/*
 * 删除推荐组
 * 参数：groupID 
 */
public class RecommendGroupDelete extends UserGroupDiffSync {

	public TaskResult exec(SyncTask task) {
		TaskResult result = super.exec(task);

		// 删除组成功后，执行一系列清除操作；
		if (result == TaskResult.SUCCESS) {
			SqlExecutor bookSqlExec = task.getSqlExecutor("book");

			try {
				// 删除推荐组人工规则
				BaseRecommendGroupHelper.deleteRecommendGroupRule(getUserGroupID(task), "2", bookSqlExec);
				// 删除推荐组自动规则
				BaseRecommendGroupHelper.deleteRecommendGroupRule(getUserGroupID(task), "0", bookSqlExec);
				
				// 删除不再使用推荐总编；
				BaseRecommendGroupHelper.deleteUnusedRecommendAuditor(bookSqlExec);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
		}
		return result;
	}

}
