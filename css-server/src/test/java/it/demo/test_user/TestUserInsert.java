package it.demo.test_user;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TestUserInsert implements TaskHandler {

	/**
	 * 问题，无法或者ID，无法形成互斥？应该优先执行；不排队；优先级应该高一个！！！
	 */
	@Override
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 获得contentID，并通过ContentID构造sql
		Map<String, String> contentID = task.getContent().getMap();
		
		String querySql = String.format("select userid, username from test_users where username='%s'",
				contentID.get("username"));

		SqlExecutor common = task.getSqlExecutor("common");
		
		try {
			RowSet rows = common.queryForDataSet(querySql).get();
			
			if (rows.getRows().size() > 0) {
				//简单处理，不严谨
				Row row = rows.getRows().get(0);
				String userid = row.getFieldValue("userid").toString();
				String username = row.getFieldValue("username").toString();
				String insertSql = String.format("insert into test_users (userid, username) values (%s, '%s')", userid, username);

				SqlExecutor book = task.getSqlExecutor("book");
				int updateNumber = book.executeUpdate(insertSql).get();
				System.out.println("--> udpate number " + updateNumber);
				
				return TaskResult.SUCCESS;
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}
}
