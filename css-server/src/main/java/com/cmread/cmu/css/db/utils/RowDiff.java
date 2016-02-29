package com.cmread.cmu.css.db.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;

public class RowDiff {

	private static Logger logger = LoggerFactory.getLogger(RowDiff.class);
	
	/**
	 * 比较两个数据库记录是否一致
	 */
	public static RowDiff diffRow(Row from, Row to) {
		RowDiff rowDiff = new RowDiff();
		
		for (Map.Entry<String, Object> fromField : from.toMap().entrySet()) {
			String fieldName = fromField.getKey();
			
			if (to.isFieldExist(fieldName)) {
				Object fromValue = fromField.getValue();
				Object toValue = to.getFieldValue(fieldName);
				
				if (!compareTwoValue(fromValue, toValue)) {
					logger.trace("比较字段 {} 不相同", fieldName);
					if (fromValue != null) {
						logger.trace("from {} : {}", fromValue.getClass().toString(), fromValue.toString());
					}
					if (toValue != null) {
						logger.trace("to {} : {}", toValue.getClass().toString() , toValue.toString());
					}
					
					rowDiff.addFromToDiffField(fieldName);
				}
			} else {
				rowDiff.addFromNewField(fieldName);
			}
		}
		
		for (Map.Entry<String, Object> toField : to.toMap().entrySet()) {
			String fieldName = toField.getKey();
			
			if (!from.isFieldExist(fieldName)) {
				rowDiff.addToNewField(fieldName);
			}
		}

		return rowDiff;
	}
	
	private static boolean compareTwoValue(Object v1, Object v2) {
		if ((v1 == null) && (v2 == null)) return true;
		if ((v1 == null) && (v2 != null)) return false;
		if ((v2 == null) && (v1 != null)) return false;
		
		if (compareTwoFieldValue(v1, v2)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * 名字起得不好，这个方式实际上是对数据库记录比较的适配比较。 比如 同名字段，一个是string，一个是integer的比较；
	 * 在比如，两个Timestamp类型，但是精读不一样等等；
	 * 
	 * 以下判断逻辑有些不友好，以后需要改进，配合column元数据进行分析；
	 */
	private static boolean compareTwoFieldValue(Object v1, Object v2) {
		if ((v1 instanceof Number) || (v2 instanceof Number)) {
			return v1.toString().equals(v2.toString());
		}

		try {
			long v1Time = -1;
			long v2Time = -1;

			if (v1 instanceof oracle.sql.TIMESTAMP) {
				v1Time = ((oracle.sql.TIMESTAMP) v1).timestampValue().getTime()/1000;
			} else if (v1 instanceof Date) {
				v1Time = ((Date) v1).getTime()/1000;
			}

			// DRY FIXME
			if (v2 instanceof oracle.sql.TIMESTAMP) {
				v2Time = ((oracle.sql.TIMESTAMP) v2).timestampValue().getTime()/1000;
			} else if (v2 instanceof Date) {
				v2Time = ((Date) v2).getTime()/1000;
			}

			if ((v1Time != -1) && (v2Time != -1)) {
				return v1Time == v2Time;
			}
		} catch (Exception e) {
			logger.error("compare row field failed", e);
			
			return false;
		}

		// 没有原始类型的情况下，根据String类型进行分析？
		return v1.equals(v2);
	}
	
	private List<String> toNewList;
	private List<String> fromNewList;
	private List<String> diffList;
	
	public RowDiff() {
		this.toNewList = new ArrayList<>();
		this.fromNewList = new ArrayList<>();
		this.diffList = new ArrayList<>();
	}
	
	private void addToNewField(String fieldName) {
		this.toNewList.add(fieldName);
	}

	private void addFromNewField(String fieldName) {
		this.fromNewList.add(fieldName);
	}

	private void addFromToDiffField(String fieldName) {
		this.diffList.add(fieldName);
	}
	
	public boolean hasDiffFields() {
		return (this.diffList.size() != 0);
	}

	public List<String> getDiffFields() {
		return this.diffList;
	}
}
