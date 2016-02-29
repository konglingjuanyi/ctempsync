package it.demo.test_user;

import java.util.Map;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TestUserUpdate implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "user-" + task.getContent().getMap().get("userid");
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 获得contentID，并通过ContentID构造sql
		Map<String, String> contentID = task.getContent().getMap();
		String querySql = String.format("select userid, username from test_users where userid=%s",
				contentID.get("userid"));
		
		SqlExecutor common = task.getSqlExecutor("common");
		SqlExecutor book = task.getSqlExecutor("book");
		
		try {
			RowSet rows = common.queryForDataSet(querySql).get();
			if (rows.getRows().size() > 0) {
				Row row = rows.getRows().get(0);
				String userid = row.getFieldValue("USERID").toString();
				String username = row.getFieldValue("USERNAME").toString();

				String updateSql = String.format("update test_users set username='%s' where userid=%s", username, userid);
				
				int updaetNumber = book.executeUpdate(updateSql).get();
				System.out.println("---> update number is " + updaetNumber);
			}
			
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
