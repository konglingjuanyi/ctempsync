package com.cmread.cmu.css.service.sync.recommend;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TempRecommendEvaluationAdd implements TaskHandler {

	private CSSClientHelper clientHelper;
	
	public void setCssClientHelper(CSSClientHelper clientHelper) {
		this.clientHelper = clientHelper;
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(SyncTask task) {
		try {
			String name = task.getContent().getMap().get("itemname");
			
			SqlExecutor sqlExec = task.getSqlExecutor("common");
			String sql = "select itemid from con_recommend_evaluations where itemname=?";
			RowSet rs = sqlExec.queryForDataSet(sql, new Object[] {name}).get();
			for (Row row : rs.getRows()) {
				Object id = row.getFieldValue("itemid");
				
				if (id != null) {
					// 调用客户端API，创建同步任务；
					clientHelper.RecommendEvaluationAdd(id.toString());
				}
			}
			
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
