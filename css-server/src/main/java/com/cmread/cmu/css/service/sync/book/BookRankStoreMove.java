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
 * 2015-12-26
 * 销量排行榜
 * bks->cms（Move）
 */
public class BookRankStoreMove implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(BookRankStoreMove.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return "book-rank-move";
	}

	@Override
	public TaskResult exec(SyncTask task)  {
		SqlExecutor bookSql = task.getSqlExecutor("book");
		SqlExecutor commonSql = task.getSqlExecutor("common");
		
		String sql = "select * from con_rank_bookstore";
		try {
			RowSet rs = bookSql.queryForDataSet(sql).get();
			for (Row row : rs.getRows()) {
				// 在common库中新增；
				// 在book库中删除；
				RowInsertAction insert = new RowInsertAction(row, "con_rank_bookstore", commonSql);
				insert.execute();
			}
			//清空bks表中对应数据
			bookSql.executeUpdate("delete from con_rank_bookstore");
			
			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException | SQLException e) {
			logger.error("book rank move task failed.", e);
			return TaskResult.FAILED;
		}
	}
	
	

}
