package it.demo.test_log;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TestLogMove implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "test_log_move";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		Map<String, String> content = task.getContent().getMap();
		
		String operator = content.get("operator");
		String logtime = content.get("logtime");

		//HSQL不支持to_date, sequence也没有测试，但是支持datetime字段的字符串写入（oracle）不支持
		//另外 HSQL的date字段似乎不支持时间，而oracle好像是支持的，这一点需要验证
//		String date = String.format("to_date('%s', 'yyyy-MM-dd HH24:mi:ss')", logtime);
//		System.out.println(date);
		
		String insertSql = String.format("insert into test_log (logid, operator, logtime) values (%s, '%s', '%s')",
				"null", operator, logtime);
		System.out.println(insertSql);
		
		SqlExecutor sqlExec = task.getSqlExecutor("common");

		Future<Integer> result = sqlExec.executeUpdate(insertSql);
		
		try {
			int updateNumber = result.get();
			System.out.println("--->  result " + updateNumber);

			return TaskResult.SUCCESS;
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}
	
}
