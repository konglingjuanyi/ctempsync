package com.cmread.cmu.css.db.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.RowKey;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.RowSetDiff.SqlDataSetDiffItem;

public class RowSetDiffExecutor {

	private static Logger logger = LoggerFactory.getLogger(RowSetDiffExecutor.class);
	
	private SqlExecutor sqlExec;
	
	public RowSetDiffExecutor(SqlExecutor sqlExec) {
		this.sqlExec = sqlExec;
	}

	public void execute(RowSetDiff diff, String toTableName, ActionFilter actionFilter) throws InterruptedException, ExecutionException, SQLException {
		for (SqlDataSetDiffItem diffItem : diff.getDiffRows()) {
			RowKey pk = diffItem.getPrimaryKey();
			
			RowActioin action = null;
			switch (diffItem.getDiffType()) {
			case FROM_NEW:
				action = new RowInsertAction(diffItem.getFromRow(), toTableName, this.sqlExec);
				break;
			case FROM_TO_DIFF:
				action = new RowUpdateAction(diffItem, toTableName, this.sqlExec, pk.getKeys());
				break;
			case TO_NEW:
				action = new RowDeleteAction(pk.getKeys(), pk.getValues(), toTableName, this.sqlExec);
				break;
			}
			
			if (actionFilter != null) {
				action = actionFilter.filter(action);
			} else {
				if (action != null) {
					// 有可能被filter过滤掉
					int num = action.execute().get();
					// try {

					// } catch (Exception e) {
					// //// FIXME 临时方案，为测试先特例处理
					// if (diffItem.getDiffType() == DiffRowType.FROM_NEW) {
					// RowActioin delete = new RowDeleteAction(pk.getKeys(),
					// pk.getValues(), toTableName, this.sqlExec);
					// delete.execute().get();
					// num = action.execute().get();
					// }
					// }
					logger.debug("exec sql return {}", num);
				} else {
					logger.debug("action {} skipped by filter.", diffItem.getDiffType());
				}
			}
			
		}
	}

}
