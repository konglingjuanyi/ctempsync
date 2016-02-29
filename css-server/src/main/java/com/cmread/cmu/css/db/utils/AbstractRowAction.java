package com.cmread.cmu.css.db.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

/*
 * 这个抽象类很不好，其实公用性不大，反而将结构搞得比较乱，不容易看明白，应该去掉 TODO
 */
public abstract class AbstractRowAction implements RowActioin {
	
	private Row row;
	private String tableName;
	private SqlExecutor sqlExecutor;
	
	public AbstractRowAction(Row row, String tableName, SqlExecutor sqlExecutor) {
		this.row = row;
		this.tableName = tableName;
		this.sqlExecutor = sqlExecutor;
	}
	
	@Override
	public Future<Integer> execute() throws SQLException, InterruptedException, ExecutionException {
		TableMetaData tableMetaData = TableMetaData.find(tableName, sqlExecutor);
		
		// 假设不为空，FIXME 需要修改
		//针对这个dataset，转换为to中的insert语句；
		SqlBuilder sql = toSql(row, tableMetaData);
		
		return executeUpdate(sql, sqlExecutor);
	}
	
	public abstract SqlBuilder toSql(Row row, TableMetaData tableMetaData);
	
	private Future<Integer> executeUpdate(SqlBuilder sql, SqlExecutor sqlExec) throws SQLException, InterruptedException, ExecutionException {
		String sqlStr = sql.getSqlString();
		Object[] sqlParas= sql.getParameters();
		
		return sqlExec.executeUpdate(sqlStr, sqlParas);
	}
	
	protected boolean isNotOwnedPlaceField(String fieldName) {
		return !StringUtils.equalsIgnoreCase(fieldName, "OWNEDPLACE");
	}
}
