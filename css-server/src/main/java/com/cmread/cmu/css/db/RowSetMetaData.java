package com.cmread.cmu.css.db;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本上相当于ResutSetMetaData的离线版本，但是内容不一定全，有需要的话可以扩展。
 * 
 * @author zhangtieying
 *
 */
public class RowSetMetaData {
	
	private List<Column> columns;
	
	public RowSetMetaData() {
		this.columns = new ArrayList<>();
	}
	
	public void addColumn(Column column) {
		this.columns.add(column);
	}

	public int getColumnCount() {
		return columns.size();
	}

	public Column getColumn(int i) {
		return columns.get(i - 1);
	}

	public static class Column {
		private int index;
		private String name;
		private int type;
		private String typeName;
		private String className;
		private int displaySize;
		private String label;
		private String tableName; // column所述的表，有可能同一个dataset的记录来自不同的表
		private int nullAble;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getTypeName() {
			return typeName;
		}
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public int getDisplaySize() {
			return displaySize;
		}
		public void setDisplaySize(int displaySize) {
			this.displaySize = displaySize;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public int isNullAble() {
			return nullAble;
		}
		public void setNullAble(int nullAble) {
			this.nullAble = nullAble;
		}
		public int getIndex() {
			return this.index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
	}

}
