package com.cmread.cmu.css.db;

import java.util.ArrayList;
import java.util.List;

/*
 * 数据库查询结果记录集合，实际就是row对象的集合；
 * 也可以理解是JDBC的ResultSet的离线版;
 * 
 * RowSet中同样也包含row的元数据（即对应column的元数据）
 */
public class RowSet {
	
	private RowSetMetaData metaData;

	private List<Row> rowList;

	public RowSet() {
		this.rowList = new ArrayList<>();
	}
	
	public void addRow(Row row) {
		rowList.add(row);
	}

	public List<Row> getRows() {
		return this.rowList;
	}
	
	public int size() {
		return this.rowList.size();
	}

	public void setMetaData(RowSetMetaData metaData) {
		this.metaData = metaData;
	}
	
	public RowSetMetaData getMetaData() {
		return this.metaData;
	}

	public Row getRow(RowKey rowKey) {
		for (Row row : this.rowList) {
			if (row.matchPrimaryKey(rowKey)) {
				return row;
			}
		}
		return null;
	}

}
