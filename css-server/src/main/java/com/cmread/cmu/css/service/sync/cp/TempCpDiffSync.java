package com.cmread.cmu.css.service.sync.cp;

import org.apache.commons.lang3.ArrayUtils;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TempCpDiffSync implements TaskHandler {

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
			String cpObjectID = task.getContent().getMap().get("object_id");
			String cpID = task.getContent().getMap().get("cpid");

			SqlExecutor sqlExec = task.getSqlExecutor("common");
			RowSet rs = null;
			if (cpObjectID != null) {
				String sql = "select object_id, cpid, status from t_cp_info where object_id=?";
				rs = sqlExec.queryForDataSet(sql, new Object[] { cpObjectID }).get();
			} else if (cpID != null) {
				String sql = "select object_id, cpid, status from t_cp_info where cpid=?";
				rs = sqlExec.queryForDataSet(sql, new Object[] { cpID }).get();
			}

			if (rs != null) {
				for (Row row : rs.getRows()) {
					String objectID = row.getFieldValue("object_id").toString();
					String status = row.getFieldValue("status").toString();

					if (!ArrayUtils.contains(new String[] {"1", "2", "3", "4"}, status)) {
						// 调用客户端API，创建同步任务；
						clientHelper.cpUpdate(objectID);
					}
				}
			}

			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaskResult.FAILED;
	}

}
