package com.cmread.cmu.css.service.sync.notice;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.RowInsertAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/*
 * 我的消息/提醒 Move任务（从一个表move到另一个表）
 */
public class NoticeMove implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(NoticeMove.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "notice";
	}

	@Override
	public TaskResult exec(SyncTask task)  {
		SqlExecutor bookSql = task.getSqlExecutor("book");
		SqlExecutor commonSql = task.getSqlExecutor("common");
		
		String sql = "select * from bks_notice";
		try {
			String key = "BULLETINID";
			RowSet rs = bookSql.queryForDataSet(sql).get();
			for (Row row : rs.getRows()) {
				// 在common库中新增；
				String rowKey = row.getFieldValue(key).toString();
				row.removeField(key);
				row.addExtraFieldAtInsert(key, "S_MCP_NOTICE.nextval");

				RowInsertAction insert = new RowInsertAction(row, "mcp_notice", commonSql);
				int updateNum = insert.execute().get();
				logger.debug("exec update return {}", updateNum);
				
				// 在book库中删除；
				int deleteNum = bookSql.executeUpdate(String.format("delete from bks_notice where %s=%s", key, rowKey)).get();
				logger.debug("exec delete return {}", deleteNum);
			}
			
			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException | SQLException e) {
			logger.error("notice move task failed.", e);
			return TaskResult.FAILED;
		}
	}
	
	

}
