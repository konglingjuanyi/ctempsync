package com.cmread.cmu.css.db;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 这里的rowkey也不一定是主键，但是必须是能够将这个row与其他row区别开来的字段或者字段的组合。（UniqueKey）
 * 
 * 目前在CMU数据库中看到的RowKey，其类型基本上不是int就是string了；
 * 因为需要将来自不同库中同步表的记录进行比较，而同步表的主键类型不一定相同（有的使用int，有的使用string），
 * 所以从比较的角度，可能需要都转为string来比较；
 * 
 * FIXME 应该将所有主键值，在这个对象中转换为string，可用于统一比较；
 * 
 * @author zhangtieying
 *
 */
public class RowKey {

	private List<String> keys;
	private List<Object> values;
	
	public RowKey() {
		this.keys = new ArrayList<>(2);
		this.values = new ArrayList<>(2);
	}
	
	public void addKey(String key, Object value) {
		this.keys.add(key);
		this.values.add(value);
	}

	public String[] getKeys() {
		return this.keys.toArray(new String[this.keys.size()]);
	}
	
	public Object[] getValues() {
		return this.values.toArray();
	}
	
	public Object getValue(int keyIndex) {
		return this.values.get(keyIndex);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<this.keys.size(); ++i) {
			sb.append(keys.get(i)).append("=").append(values.get(i)).append(",");
		}
		return sb.toString();
	}
}
