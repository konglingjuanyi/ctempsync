package com.cmread.cmu.css.db.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class TableMetaData {

	private static Logger logger = LoggerFactory.getLogger(TableMetaData.class);
	
	private static ConcurrentHashMap<String, TableMetaData> tableMetaDataCache = new ConcurrentHashMap<>();

	/*
	 * @return null 如果找不到指定名字的表
	 */
	public static TableMetaData find(String tableName, SqlExecutor sqlExec) throws SQLException {
		String tableNameWithDB = sqlExec.getDBName() + "." + tableName;
		
		TableMetaData tableMetaData = tableMetaDataCache.get(tableNameWithDB);
		
		if (tableMetaData == null) {
			tableMetaData = new TableMetaData();
			tableMetaData.setTableName(tableName);

			try (Connection conn = sqlExec.getConnection()) {
				DatabaseMetaData dbMeta = conn.getMetaData();
				// FIXME 这个地方总是有些不对！！！
				String schema = dbMeta.getUserName();
				if ("SA".equals(dbMeta.getUserName())) {
					//使用hsql测试的时候使用这个用户
					//做这个判断的原因是hsql如果加了这个约束，就查不出表结构来；
					//而oracle少了这个约束，可能会查出其他用户的同名表来；
					//对于getColumn的参数1和参数2的理解有问题，需要进一步研究以后再修改这里，目前
					//暂时为了实际系统和测试用例都能正常运行，先这样做特例处理。
					schema = null;
				}
				
				try (ResultSet rs = dbMeta.getColumns(null, schema, tableName.toUpperCase(), "%")) {
					if (rs.getMetaData().getColumnCount() == 0) {
						// FIXME 这个判断好像没有意义，确认后删除这个判断
						logger.warn("can't find table {} metadata ", tableNameWithDB);
						return null;
					}
					
					boolean columnExist = false;
					while (rs.next()) {
						columnExist = true;
						Column column = new Column();
						column.setName(rs.getString("COLUMN_NAME"));
						column.setDataType(rs.getString("DATA_TYPE"));
						column.setTypeName(rs.getString("TYPE_NAME"));

						tableMetaData.addColumn(column);
					}
					
					if (!columnExist) {
						logger.warn("can't find table {} metadata ", tableNameWithDB);
					}
				}
			}
			
			tableMetaDataCache.put(tableNameWithDB, tableMetaData);
		}
		
		return tableMetaData;
	}
	
	private String tableName;
	private List<Column> columns;
	
	public TableMetaData() {
		this.columns = new ArrayList<>();
	}

	private void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private void addColumn(Column column) {
		this.columns.add(column);
	}

	public Column getColumn(String key) {
		key = key.toUpperCase();
		for (Column col : columns) {
			if (col.getName().equals(key)) {
				return col;
			}
		}
		return null;
	}

	public String getTableName() {
		return this.tableName;
	}

	public static class Column {

		private String name;
		private String dataType;
		private String typeName;
		
		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		
		public String getDataType() {
			return this.dataType;
		}
		
		public String getTypeName() {
			return this.typeName;
		}

	}

}
