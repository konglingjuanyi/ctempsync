package com.cmread.cmu.css.db.asyncsql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.RowSetMetaData;
import com.cmread.cmu.css.db.RowSetMetaData.Column;

public class ResultSetToRowSet implements ResultSetConvert<RowSet> {

	@Override
	public RowSet convert(ResultSet rs) throws SQLException {
		RowSet dataSet = new RowSet();
		
		ResultSetMetaData rsMeta = rs.getMetaData();
		RowSetMetaData dsMeta = convertMetaData(rsMeta);
		dataSet.setMetaData(dsMeta);
		
		int columnCount = dsMeta.getColumnCount();
		while (rs.next()) {
			Row row = new Row();

			for (int i = 1; i <= columnCount; ++i) {
				Column column = dsMeta.getColumn(i);
				
				Object value = rs.getObject(i);
				row.addField(column.getName(), value);
			}
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	/*
	 * ResultSetMetaData到DataSetMetaData的转换器，有需要的话可以提取为独立类
	 */
	public static RowSetMetaData convertMetaData(ResultSetMetaData meta) throws SQLException {
		RowSetMetaData dsmd = new RowSetMetaData();
		
		int columnCount = meta.getColumnCount();
		for (int i=1; i<=columnCount; ++i) {
			Column column = new Column();
			
			column.setIndex(i);
			column.setName(meta.getColumnName(i));
			column.setType(meta.getColumnType(i));
			column.setTypeName(meta.getColumnTypeName(i));
			column.setClassName(meta.getColumnClassName(i));
			column.setDisplaySize(meta.getColumnDisplaySize(i));
			column.setNullAble(meta.isNullable(i));
			column.setLabel(meta.getColumnLabel(i));
			column.setTableName(meta.getTableName(i));
			
			dsmd.addColumn(column);
		}
		
		return dsmd;
	}
	

}
