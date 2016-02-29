package com.cmread.cmu.css.db.utils;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.SqlBuilder.DeleteSqlBuilder;

public class RowDeleteAction extends AbstractRowAction {

	private String[] primaryKeyNames;
	private Object[] keyValues;
	
	public RowDeleteAction(String[] primaryKeyNames, Object[] keyValues, String tableName, SqlExecutor sqlExecutor) {
		super(null, tableName, sqlExecutor);
		
		this.primaryKeyNames = primaryKeyNames;
		this.keyValues = keyValues;
	}

	@Override
	public SqlBuilder toSql(Row row, TableMetaData tableMetaData) {
		return this.toDeleteSql(row, tableMetaData);
	}

	public SqlBuilder toDeleteSql(Row row, TableMetaData tableMetaData) {
		DeleteSqlBuilder delete = SqlBuilder.delete();;
		delete.table(tableMetaData.getTableName());
		
		String[] keys = this.primaryKeyNames;
		Object[] keyValues = this.keyValues;
		for (int i=0; i<keys.length; ++i) {
			delete.addCondition(keys[i], keyValues[i]);
		}
		
		return delete;
	}
}
