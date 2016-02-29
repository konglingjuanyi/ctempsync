package com.cmread.cmu.css.service.sync.qc;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TempQcResultItemAdd implements TaskHandler {

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
			String qcName = task.getContent().getMap().get("qc_name");
			
			SqlExecutor sqlExec = task.getSqlExecutor("common");
			String sql = "select id from con_qc_result_items where name=?";
			RowSet rs = sqlExec.queryForDataSet(sql, new Object[] {qcName}).get();
			for (Row row : rs.getRows()) {
				Object qcID = row.getFieldValue("id");
				
				// 调用客户端API，创建同步任务；
				clientHelper.QcResultItemAdd(qcID.toString());
			}
			
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
