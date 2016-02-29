/**
 * 
 */
package com.cmread.cmu.css.service.sync.recommend;

import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/**
 * @author caidq
 *
 * 推荐：图书子系统  推荐库管理 列表中需展示  图书最新上架时间
 * 注意：con_recomend_on_off_his表为日志表，不全量同步，
 * 根据bookid查询是否为bks侧数据，然后以bookid为主键更新bks_recomend_on_off_his表中对应记录，
 * 以此方式，bks中该记录表的数量级与book主表等同，而非日志级别
 * 
 * 
 * TODO 待删除,该同步方法已合并到上下架同步中，BookOnShelfSuccess，稳定后删除
 */
public class RecomendOnOffHisSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "recommend-onoff-his";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		try {

			String bookID = task.getContent().getMap().get("bookid");
			//获取最新上架时间
			String onShelfTime = queryOnShelfTimeByBookID(bookID,task);
			//判断book中con_recomend_on_off_his表是否有对应的记录
			SqlExecutor bookSqlExec = task.getSqlExecutor("book");
			RowSet rows= bookSqlExec.queryForDataSet(
			"select * from bks_recomend_on_off_his where bookid='" + bookID
					+ "'").get();
			// 更新表
			if (rows.getRows().size() > 0) {
				String updateSql= "update bks_recomend_on_off_his set on_shelf_time ='"+onShelfTime+"' where bookid='" + bookID
					+ "'";
				bookSqlExec.executeUpdate(updateSql).get();
			}
			//首次的时候插入
			else{
				String insertSql = "insert into bks_recomend_on_off_his (bookid,on_shelf_time) values ("+ bookID + ",'"+onShelfTime+"')";
				bookSqlExec.executeUpdate(insertSql).get();
			}
			
			return TaskResult.SUCCESS;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaskResult.FAILED;
	}
	
	private String queryOnShelfTimeByBookID(String bookID, SyncTask task) throws InterruptedException, ExecutionException {	
		SqlExecutor commonSqlExec = task.getSqlExecutor("common");
		String sql = "select * from con_recomend_on_off_his where bookid ='"
				+ bookID + "' and on_shelf_time is not null order by on_shelf_time desc";
		RowSet rs = commonSqlExec.queryForDataSet(sql).get();
		if (rs.getRows().size() <= 0) {
			return null;
		} 	
		Object onShelfTime = rs.getRows().get(0).getFieldValue("on_shelf_time");
		if (onShelfTime != null) {
			return onShelfTime.toString();
		}
		return null;
	}

}


