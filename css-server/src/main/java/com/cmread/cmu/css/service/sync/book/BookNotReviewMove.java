/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

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

/**
 * @author caidq
 *
 * 2015-12-21
 */
public class BookNotReviewMove implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(BookNotReviewMove.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "not-review-move";
	}

	@Override
	public TaskResult exec(SyncTask task)  {
		SqlExecutor bookSql = task.getSqlExecutor("book");
		SqlExecutor commonSql = task.getSqlExecutor("common");
		
		String sql = "select * from bks_resumechapnotreviewoper";
		try {
			RowSet rs = bookSql.queryForDataSet(sql).get();
			for (Row row : rs.getRows()) {
				// 在common库中新增；
				// 在book库中删除；
				RowInsertAction insert = new RowInsertAction(row, "con_resumechapnotreviewoper", commonSql);
				insert.execute();
			}
			//清空bks表中对应数据
			bookSql.executeUpdate("delete from bks_resumechapnotreviewoper");
			
			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException | SQLException e) {
			logger.error("notice move task failed.", e);
			return TaskResult.FAILED;
		}
	}
	
	

}
