package com.cmread.cmu.css.service.usecheck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.dao.DataAccessException;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * 内容关联性检查的通用类
 * 
 * 这个任务主要是实时接口；实时接口理论上应该走一个单独任务调度通道为好；
 * 
 * @author zhangtieying
 *
 */
public class CommonUseCheck implements TaskHandler {

	private CheckSql[] checkSqls;

	public void setCheckSqls(CheckSql[] sqls) {
		this.checkSqls = sqls;
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		// 没有关联性任务，无需排队；
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		try {
			Map<String, String> resultMap = new HashMap<String, String>();
			
			for (CheckSql checkSql : checkSqls) {
				SqlExecutor sqlExec = task.getSqlExecutor(checkSql.getDbName());
				String sql = checkSql.getSql();
				sql = replaceSqlPara(sql, task.getContent().getMap());

				long num = sqlExec.queryForLong(sql).get();
				if (num > 0) {
					// 设置任务结果map值
					resultMap.put(checkSql.getCheckName(), "true");
				} else {
					resultMap.put(checkSql.getCheckName(), "false");
				}
			}

			task.getResuleMap().putAll(resultMap);
			return TaskResult.SUCCESS;
		} catch (DataAccessException | InterruptedException | ExecutionException e) {
			return TaskResult.FAILED;
		}
	}

	private String replaceSqlPara(String sql, Map<String, String> params) {
		for (Map.Entry<String, String> param : params.entrySet()) {
			sql = sql.replace("$(" + param.getKey() + ")", param.getValue());
		}
		return sql;
	}

}
