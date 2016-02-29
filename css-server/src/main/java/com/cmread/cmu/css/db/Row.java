package com.cmread.cmu.css.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 代表数据库查询出来的一条记录，即查询结果中的一行；（注意记录中的字段不一定属于一个表）
 * 
 * 注意：这个类是非线程安全的！！！
 */
public class Row {

	private Map<String, Object> fields;
	
	//这是一个丑陋的补丁，用于同步时在查询出来的原始记录中插入额外字段
	private Map<String, Object> extraFieldsAtInsert;

	public Row() {
		this.fields = new HashMap<>();
	}

	public Map<String, Object> toMap() {
		return this.fields;
	}

	public void addField(String columnName, Object value) {
		fields.put(columnName, value);
	}

	public RowKey getPrimaryKey(String[] primaryKeyNames) {
		RowKey pk = new RowKey();
		for (String keyName : primaryKeyNames) {
			keyName = keyName.toUpperCase(); 
			pk.addKey(keyName, fields.get(keyName));
		}
		return pk;
	}

	public boolean matchPrimaryKey(RowKey rowKey) {
		String[] keyNames = rowKey.getKeys();
		for (int i=0; i<keyNames.length; ++i) {
			String keyName = keyNames[i];
			Object keyValue = rowKey.getValue(i);
			
			if (!compareAsString(fields.get(keyName), keyValue)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 默认将key转为String来比较
	 */
	private boolean compareAsString(Object v1, Object v2) {
		if ((v1 == null) && (v2 == null)) return true;
		if ((v1 == null) && (v2 != null)) return false;
		if ((v2 == null) && (v1 != null)) return false;
		
		if (v1.toString().equals(v2.toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	public Object[] getFieldValues(String[] fieldNames) {
		List<Object> values = new ArrayList<>(fieldNames.length);
		for (String name : fieldNames) {
			values.add(fields.get(name.toUpperCase()));
		}
		
		return values.toArray();
	}

	public void setField(String column, String value) {
		fields.put(column.toUpperCase(), value);
	}

	public Object getFieldValue(String column) {
		return this.fields.get(column.toUpperCase());
	}

	public void setField(String fieldName, Object value) {
		this.fields.put(fieldName.toUpperCase(), value);
	}

	public boolean isFieldExist(String fieldName) {
		return this.fields.containsKey(fieldName.toUpperCase());
	}

	public void addExtraFieldAtInsert(String fieldName, String fieldValue) {
		if (this.extraFieldsAtInsert == null) {
			this.extraFieldsAtInsert = new HashMap<>();
		}
		this.extraFieldsAtInsert.put(fieldName, fieldValue);
	}

	public Map<String, Object> getExtraFieldsAtInsert() {
		return this.extraFieldsAtInsert;
	}

	public void removeField(String fieldName) {
		this.fields.remove(fieldName.toUpperCase());
	}
}
