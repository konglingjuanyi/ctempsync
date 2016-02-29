package it.demo.test_user;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * 这是同步Task的旧的结构和写法，基本不再使用，暂时放在这里备用
 * 
 * @author zhangtieying
 *
 */
public class TestUserDelete implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "user-" + task.getContent().getMap().get("userid");
	}

	@Override
	public TaskResult exec(SyncTask task) {
		String deleteUserID = task.getContent().getMap().get("userid");

		String deleteSql = String.format("delete from test_users where userid=%s", deleteUserID);

		try {
			SqlExecutor book = task.getSqlExecutor("book");

			int deleteNum = book.executeUpdate(deleteSql).get();

			System.out.println("---> delete number " + deleteNum);
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaskResult.FAILED;
	}

}
