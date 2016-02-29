package com.cmread.cmu.css.service.usecheck;

public class CheckSql {
	private String checkName;
	private String dbName;
	private String sql;

	public String getCheckName() {
		return this.checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
