package com.cmread.cmu.css.service.sync.operator;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

/*
 * 临时同步任务，用于sql拦截器触发操作员状态更新（by name）；
 * 与默认操作员同步的区别默认同步是通过operid的；这个同步是通过operator_name的；
 * 
 * 这个任务在共存期后可以删除
 */
public class TempOperatorUpdate implements TaskHandler {

	private CSSClientHelper clientHelper;
	
	public void setCssClientHelper(CSSClientHelper clientHelper) {
		this.clientHelper = clientHelper;
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return null; //不用排队
	}

	@Override
	public TaskResult exec(SyncTask task) {
		try {
			String operName = task.getContent().getMap().get("operator_name");
			
			SqlExecutor sqlExec = task.getSqlExecutor("common");
			String sql = "select operid from t_bme_operator where name=?";
			RowSet rs = sqlExec.queryForDataSet(sql, new Object[] {operName}).get();
			for (Row row : rs.getRows()) {
				String operID = (String)row.getFieldValue("operid");
				
				// 调用客户端API，创建同步任务；
				clientHelper.operatorUpdate(operID);
			}
			
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
