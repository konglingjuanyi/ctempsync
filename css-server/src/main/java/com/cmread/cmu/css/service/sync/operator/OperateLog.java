package com.cmread.cmu.css.service.sync.operator;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.SqlBuilder;
import com.cmread.cmu.css.db.utils.SqlBuilder.InsertSqlBuilder;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/*
 * 操作员日志：直接插入common库中
 */
public class OperateLog implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		InsertSqlBuilder insert = SqlBuilder.insert();
		insert.table("sup_operatelog");
		
		Map<String, String> taskParas = task.getContent().getMap();
		insert.addColumn("logID", "LOGINFO_ID_SEQ.nextval");
		insert.addColumn("operateID", taskParas.get("operateID"));
		insert.addColumn("operateName", taskParas.get("operateName"));
		insert.addColumn("roleID", taskParas.get("roleID"));
		insert.addColumn("roleName", taskParas.get("roleName"));
		insert.addColumn("stepID", taskParas.get("stepID"));
		insert.addColumn("stepdes", taskParas.get("stepdes"));
		insert.addColumn("operateTime", new java.sql.Date(Long.parseLong(taskParas.get("operateTime"))));
		insert.addColumn("operateObject", taskParas.get("operateObject"));
		insert.addColumn("workNum", taskParas.get("workNum"));

		SqlExecutor common = task.getSqlExecutor("common");
		try {
			common.executeUpdate(insert.getSqlString(), insert.getParameters()).get();
			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException e) {
			return TaskResult.FAILED;
		}

		
	}

}
