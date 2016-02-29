package com.cmread.cmu.css.db.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 一个简单的Sql语句Builder，不全面，随需随写
 * 
 * @author zhangtieying
 *
 */
public abstract class SqlBuilder {

	public static InsertSqlBuilder insert() {
		return new InsertSqlBuilder();
	}
	
	public static UpdateSqlBuilder update() {
		return new UpdateSqlBuilder();
	}
	
	public static DeleteSqlBuilder delete() {
		return new DeleteSqlBuilder();
	}
	
	public static SelectSqlBuilder select()	{
		return new SelectSqlBuilder();
	}
	
	protected String tableName;
	
	public void table(String tableName) {
		this.tableName = tableName;
	}
	
	public abstract String getSqlString();
	public abstract Object[] getParameters();
	
	public static class InsertSqlBuilder extends SqlBuilder {
		
		private List<String> columnNames;
		private List<String> valuePlaceholder;
		private List<Object> columnValues;
		
		public InsertSqlBuilder() {
			this.columnNames = new ArrayList<>();
			this.columnValues = new ArrayList<>();
			this.valuePlaceholder = new ArrayList<>();
		}
		
		public void addColumn(String name, Object value) {
			this.columnNames.add(name);
			if ((value != null) && (value instanceof String) && ((String)value).endsWith(".nextval")) {
				// 说明value是一个自增序列，自增序列不能加引号
				// TODO 感觉应该有更友好的方式，回头再想想
				this.valuePlaceholder.add((String)value);
			} else if (value == null) {
				this.valuePlaceholder.add("null");
			} else {
				this.valuePlaceholder.add("?");
				this.columnValues.add(value);
			}
		}
		
		@Override
		public String getSqlString() {
			String columns = StringUtils.join(columnNames, ',');
			String placeHolders = StringUtils.join(valuePlaceholder, ',');
			String sql = String.format("insert into %s (%s) values (%s)", tableName, columns, placeHolders);
			
			return sql;
		}

		@Override
		public Object[] getParameters() {
			return this.columnValues.toArray();
		}
		
	}
	
	public static class DeleteSqlBuilder extends SqlBuilder {

		private List<String> condPlaceholder;
		private List<Object> condParameters;
		
		public DeleteSqlBuilder() {
			this.condPlaceholder = new ArrayList<>();
			this.condParameters = new ArrayList<>();
		}
		
		@Override
		public Object[] getParameters() {
			return this.condParameters.toArray();
		}

		@Override
		public String getSqlString() {
			String cond = StringUtils.join(this.condPlaceholder, " and ");
			return String.format("delete from %s where %s", tableName, cond);
		}

		public void addCondition(String column, Object value) {
			this.condPlaceholder.add(column + "=?");
			this.condParameters.add(value);
		}

	}
	
	// FIXME 这个部分的代码很多和Insert和Delete部分重复，可以合并一些吗？
	public static class UpdateSqlBuilder extends SqlBuilder {
		
		private List<String> setPlaceholders;
		private List<Object> setParameter;
		
		private List<String> condPlaceholder;
		private List<Object> condParameters;
		
		public UpdateSqlBuilder() {
			this.setPlaceholders = new ArrayList<>();
			this.setParameter = new ArrayList<>();
			
			this.condPlaceholder = new ArrayList<>();
			this.condParameters = new ArrayList<>();
		}
		
		public void addSet(String name, Object value) {
			this.setPlaceholders.add(name + "=?");
			this.setParameter.add(value);
		}
		
		public void addCondition(String column, Object value) {
			this.condPlaceholder.add(column + "=?");
			this.condParameters.add(value);
		}

		@Override
		public String getSqlString() {
			// 这里面应该过滤掉set中的cond部分 TODO 
			String sets = StringUtils.join(this.setPlaceholders, ',');
			String cond = StringUtils.join(this.condPlaceholder, " and ");
			
			return String.format("update %s set %s where %s", tableName, sets, cond);
		}

		@Override
		public Object[] getParameters() {
			List<Object> parameters = new ArrayList<>();
			parameters.addAll(this.setParameter);
			parameters.addAll(this.condParameters);
			return parameters.toArray();
		}

	}
	
	// 简单Select，根据目前需要，先仅支持select * 
	public static class SelectSqlBuilder extends SqlBuilder {
		private List<String> condPlaceholder;
		private List<Object> condParameters;
		private String[] orderBy = null;
		
		public SelectSqlBuilder() {
			this.condPlaceholder = new ArrayList<>();
			this.condParameters = new ArrayList<>();
		}
		
		public void addCondition(String column, Object value) {
			this.condPlaceholder.add(column + "=?");
			this.condParameters.add(value);
		}

		@Override
		public String getSqlString() {
			// 这里面应该过滤掉set中的cond部分 TODO 
			String sql;
			if (this.condParameters.size() > 0) {
				String cond = StringUtils.join(this.condPlaceholder, " and ");
				sql = String.format("select * from %s where %s", tableName, cond);
			} else {
				sql = String.format("select * from %s", tableName);
			}
			
			if ((this.orderBy != null) && (this.orderBy.length > 0)) {
				return sql + " order by " + StringUtils.join(this.orderBy, ",");
			}
			return sql;
		}

		@Override
		public Object[] getParameters() {
			return this.condParameters.toArray();
		}

		public void orderBy(String[] orderBy) {
			this.orderBy = orderBy;
		}

	}

}
