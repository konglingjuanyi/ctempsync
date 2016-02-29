/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * @author caidq
 * 
 *         2015-12-18 图书连载更新 写得太乱，周一整理下
 */
public class BookSerialPush implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(BookSerialPush.class);

	@Override
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = task.getContent().getMap().get("bookid");
		final String lastonshelfchaptername = task.getContent().getMap().get("lastonshelfchaptername");
		final String bookname = task.getContent().getMap().get("bookname");
		final String status = task.getContent().getMap().get("status");

		SqlExecutor sqlExec = task.getSqlExecutor("common");
		RowSet rows;
		try {
			rows = sqlExec.queryForDataSet("select * from con_serialpush where bookid='" + bookID + "'").get();
			
			if (rows.getRows().size() > 0) {
				// 记录已存在，更新表
				
				if (!isNotNeedUpdate(sqlExec, bookID, lastonshelfchaptername)) {
					String updateSql = "update con_serialpush set lastmodifytime  = sysdate + 10/(24*60), status = 0, promptStatus = 0, holedaystatus = 0,lastchaptername = '"
							+ lastonshelfchaptername + "' where bookid = '" + bookID + "' and rownum <= 1";
					int num = sqlExec.executeUpdate(updateSql).get();
					logger.debug("exec update return {}", num);
				}
			} else {
				// 记录不存在，直接插入
				String insertSql = String.format(
						"insert into con_serialpush (bookid, bookname, lastmodifytime, status, booktype, lastchaptername) values (%s, '%s', %s ,'%s',(%s),'%s')",
						bookID, bookname, "sysdate + 10/(24*60)", status,
						"select info.booktype from con_auditbookinfo info where info.bookid = '" + bookID + "'",
						lastonshelfchaptername);
				int num = sqlExec.executeUpdate(insertSql).get();
				logger.debug("exec update return {}", num);
			}

			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException e) {
			logger.error("exec task failed", e);
			return TaskResult.FAILED;
		}
	}

	private boolean isNotNeedUpdate(SqlExecutor sqlExec, String bookID, String lastonshelfchaptername)
			throws InterruptedException, ExecutionException {
		RowSet rows = sqlExec.queryForDataSet("select p.lastchaptername from con_serialpush p, con_auditbookinfo info"
				+ " where info.bookid = p.bookid and info.bookid ='" + bookID + "'").get();
		if (rows.getRows().size() > 0) {
			Row row = rows.getRows().get(0);
			// 这里逻辑真乱，意思有点不明确，暂时按照伪代码来
			String lastchaptername = row.getFieldValue("lastchaptername").toString();
			if (null == lastonshelfchaptername || "".equals(lastonshelfchaptername)) {
				return true;
			} else if (null == lastchaptername || "".equals(lastchaptername)) {
				return false;
			} else if (lastchaptername.equals(lastonshelfchaptername)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
