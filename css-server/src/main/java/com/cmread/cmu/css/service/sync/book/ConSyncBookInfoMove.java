package com.cmread.cmu.css.service.sync.book;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSetMetaData;
import com.cmread.cmu.css.db.RowSetMetaData.Column;
import com.cmread.cmu.css.db.asyncsql.ResultSetConvert;
import com.cmread.cmu.css.db.asyncsql.ResultSetToRowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.RowInsertAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * 将con_sync_book_info表中记录从book移动到common库中； 主要原因是有个话单任务需要用到这个表；
 * 
 * @author zhangtieying
 *
 */
public class ConSyncBookInfoMove implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(ConSyncBookInfoMove.class);

	@Override
	public String getRelatedKey(SyncTask task) {
		return task.getTaskType();
	}

	@Override
	public TaskResult exec(SyncTask task) {
		SqlExecutor bookSql = task.getSqlExecutor("book");
		final SqlExecutor commonSql = task.getSqlExecutor("common");

		String sql = "select con_sync_book_info.* from con_sync_book_info";

		try {
			bookSql.executeQuery(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
					new ResultSetConvert<Object>() {
						@Override
						public Object convert(ResultSet rs) throws Exception {
							// 以下这段代码可以通过重构来整合到ResultSetToRowSet类中，比如通过迭代器方式，或者回调方式
							ResultSetMetaData rsMeta = rs.getMetaData();
							RowSetMetaData dsMeta = ResultSetToRowSet.convertMetaData(rsMeta);

							int columnCount = dsMeta.getColumnCount();
							while (rs.next()) {
								Row row = new Row();

								for (int i = 1; i <= columnCount; ++i) {
									Column column = dsMeta.getColumn(i);

									Object value = rs.getObject(i);
									row.addField(column.getName(), value);
								}

								// 在common库中新增；
								RowInsertAction insert = new RowInsertAction(row, "con_sync_book_info", commonSql);
								insert.execute();

								// 在book库中删除；
								rs.deleteRow();
							}
							return null;
						}
					}).get();

			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException e) {
			logger.error("con_sync_book_info move task failed.", e);
			return TaskResult.FAILED;
		}
	}

}
