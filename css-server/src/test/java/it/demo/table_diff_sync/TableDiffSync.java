package it.demo.table_diff_sync;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.RowSetDiff;
import com.cmread.cmu.css.db.utils.RowSetDiffExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * 简单的两表差异同步示例，主要为测试db.util包中的一些工具是否工作正常
 * 
 * @author zhangtieying
 *
 */
public class TableDiffSync implements TaskHandler {

	/**
	 * 对同一个表的差异同步应该排队；
	 */
	@Override
	public String getRelatedKey(SyncTask task) {
		return "test_table_sync";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		SqlExecutor book = task.getSqlExecutor("book");
		SqlExecutor common = task.getSqlExecutor("common");
		
		String tableName = "test_table_sync";
		
		try {
			String[] primaryKeyName = {"ID"};
			
			String sql = "select * from " + tableName;
			Future<RowSet> bookFuture = book.queryForDataSet(sql);
			Future<RowSet> commonFuture = common.queryForDataSet(sql);
			
			RowSet bookTable = bookFuture.get();
			RowSet commonTable = commonFuture.get();
			
			RowSetDiff diff = RowSetDiff.diff(bookTable, commonTable, primaryKeyName, null);
			
			RowSetDiffExecutor diffExecutor = new RowSetDiffExecutor(common);
			diffExecutor.execute(diff, tableName, null);
			
			//取出from表所有数据，取出to表所有数据，比较差异，执行差异
			return TaskResult.SUCCESS;
		} catch (SQLException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
