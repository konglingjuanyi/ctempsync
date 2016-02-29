package com.cmread.cmu.css.service.sync.author;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.CSSClientHelper;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;

public class TempAuthorUpdate implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(TempAuthorUpdate.class);
	
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
			String authorID = task.getContent().getMap().get("authorid");
			
			SqlExecutor sqlExec = task.getSqlExecutor("common");
			String sql = "select austatus from mcp_authorinformation where auid=?";
			RowSet rs = sqlExec.queryForDataSet(sql, new Object[] {authorID}).get();
			for (Row row : rs.getRows()) {
				String auStatus = row.getFieldValue("austatus").toString();
				
				if (StringUtils.equals("0", auStatus)) {
					// 正式作家，触发一次更新同步
					clientHelper.authorUpdate(authorID);
				} else {
					//非正式作家，忽略
					logger.info("authorid {} austatus={}, not a normal author, don't sync", authorID, auStatus);
				}
			}
			
			return TaskResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaskResult.FAILED;
	}

}
