package com.cmread.cmu.css.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.HttpRequestHandler;

import com.cmread.cmu.css.db.asyncsql.ResultSetConvert;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;
import com.cmread.cmu.css.http.server.utils.ServletUtils;
import com.google.gson.stream.JsonWriter;

/**
 * 这里目前用了两个json库，一个是org.json，一个是gson。先用的是org.json，但是它不支持流模式，后来将sql查询结果的输出用
 * gson流模式输出。暂时没有时间将org.json替换，后面有时间再做；
 * 
 * @author zhangtieying
 *
 */
public class SqlInvokeServlet implements HttpRequestHandler {
	
	private static Logger logger = LoggerFactory.getLogger(SqlInvokeServlet.class);
	
	private SqlTaskExecutorServiceRegistry sqlManager;
	
	public void setSqlExecutorManager(SqlTaskExecutorServiceRegistry sqlManager) {
		this.sqlManager = sqlManager;
	}
	
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MDC.put("sql-invoke", "true"); //增加一个日志上下文，用于过滤

		try {
			String reqStr = ServletUtils.getPostBody(request, "UTF-8");
			SqlRequest sqlReq = SqlRequestParser.parse(reqStr);
			
			response.setContentType("application/json;charset=UTF-8");
			runRequest(sqlReq, response);
		} catch (InterruptedException | ExecutionException | SQLException e) {
			logger.error("process sqlinvoke failed", e);
			throw new IOException(e);
		} finally {
			MDC.clear();
		}
	}

	private void runRequest(SqlRequest sqlReq, HttpServletResponse response) throws InterruptedException, ExecutionException, SQLException, IOException {
		if (sqlReq instanceof ExecSqlRequest) {
			SqlExecutor sqlExec = sqlManager.getExecutor(sqlReq.getDBName(), null);
			new JsonSqlRun().runSql(((ExecSqlRequest)sqlReq).getSql(), sqlExec, response);
		} else if (sqlReq instanceof TableMetaRequest) {
			String tableMetaResult = runTableMetaRequest((TableMetaRequest)sqlReq);
			
			response.getWriter().write(tableMetaResult);
			response.getWriter().close();
		}
	}

	private String runTableMetaRequest(TableMetaRequest sqlReq) throws SQLException {
		SqlExecutor sqlExec = sqlManager.getExecutor(sqlReq.getDBName(), null);
		
		String tableName = sqlReq.getTableName();
		
		return getTableMetaData(tableName, sqlExec);
	}
	
	/*
	 * @return null 如果找不到指定名字的表
	 */
	private String getTableMetaData(String tableName, SqlExecutor sqlExec) throws SQLException {
		try (Connection conn = sqlExec.getConnection()) {
			DatabaseMetaData dbMeta = conn.getMetaData();

			// FIXME 这个地方总是有些不对！！！
			String schema = dbMeta.getUserName();
			if ("SA".equals(dbMeta.getUserName())) {
				// 使用hsql测试的时候使用这个用户
				// 做这个判断的原因是hsql如果加了这个约束，就查不出表结构来；
				// 而oracle少了这个约束，可能会查出其他用户的同名表来；
				// 对于getColumn的参数1和参数2的理解有问题，需要进一步研究以后再修改这里，目前
				// 暂时为了实际系统和测试用例都能正常运行，先这样做特例处理。
				schema = null;
			}

			JSONObject json = new JSONObject();
			json.put("tableName", tableName);
			json.put("dbName", sqlExec.getDBName());

			JSONArray tableFields = new JSONArray();
			try (ResultSet rs = dbMeta.getColumns(null, schema, tableName.toUpperCase(), "%")) {
				ResultSetMetaData rsMeta = rs.getMetaData();

				int columnNumber = rsMeta.getColumnCount();

				while (rs.next()) {
					JSONObject field = new JSONObject();
					for (int i = 1; i <= columnNumber; ++i) {
						field.put(rsMeta.getColumnName(i), rs.getObject(i));
					}

					tableFields.put(field);
				}
			}
			json.put("tableFields", tableFields);
			
			return json.toString();
		}
	}

	protected static abstract class SqlRequestParser {
		
		public static SqlRequest parse(String req) {
			JSONObject json = new JSONObject(req);
			
			String dbName = json.get("dbName").toString();
			if (json.has("sql")) {
				String sql = json.getString("sql");
				return new ExecSqlRequest(dbName, sql);
			} 
			if (json.has("tableName")) {
				String tableName = json.getString("tableName");
				return new TableMetaRequest(dbName, tableName);
			}
			return null;
		}
		
	}

	protected static class SqlRequest {
		private String dbName;

		public SqlRequest(String dbName) {
			this.dbName = dbName;
		}

		public String getDBName() {
			return this.dbName;
		}
	}
	
	protected static class ExecSqlRequest extends SqlRequest {

		private String sql;
		
		public ExecSqlRequest(String dbName, String sql) {
			super(dbName);
			this.sql = sql;
		}

		public String getSql() {
			return this.sql;
		}
	}
	
	protected static class TableMetaRequest extends SqlRequest {
		private String tableName;
		
		public TableMetaRequest(String dbName, String tableName) {
			super(dbName);
			this.tableName = tableName;
		}

		public String getTableName() {
			return this.tableName;
		}
	}
	
	public static class JsonSqlRun {

		public void runSql(String sql, SqlExecutor sqlExec, HttpServletResponse response) throws InterruptedException, ExecutionException, IOException {
			sql = StringUtils.strip(sql);

			if (isSelect(sql)) {
				sqlExec.disableNextTaskResultsetLog().executeQuery(sql, new JsonConvert(sql, response.getOutputStream())).get();
				response.getOutputStream().close();
			} else if (isModifySql(sql)) {
				//默认禁止修改语句的执行，仅对外开放查询语句，以免出现误操作，如有需要再打开；
				
				//int updateNum = sqlExec.executeUpdate(sql).get();
				//return updateResultToJson(sql, updateNum);
			} else {
				response.getWriter().write("{}");
				response.getWriter().close();
			}
		}

		private boolean isSelect(String sql) {
			return StringUtils.startsWithIgnoreCase(sql, "select ");
		}

		private boolean isModifySql(String sql) {
			String prefix = sql.substring(0, 6).toLowerCase();
			
			return ArrayUtils.contains(new String[] {"update", "delete", "insert"}, prefix);
		}

		/*
		 * 暂时注释掉
		 */
//		private String updateResultToJson(String sql, int updateNum) {
//			JSONObject result = new JSONObject();
//			
//			result.put("sql", sql);
//			result.put("updateNumber", updateNum);
//			
//			return result.toString();
//		}

		private class JsonConvert implements ResultSetConvert<String> {

			private String sql;
			private OutputStream responseOutputStream;
			
			public JsonConvert(String sql, OutputStream responseOutputStream) {
				this.sql = sql;
				this.responseOutputStream = responseOutputStream;
			}
			
			@Override
			public String convert(ResultSet rs) throws Exception {
				ResultSetMetaData rsMeta = rs.getMetaData();

				JsonWriter writer = new JsonWriter(new OutputStreamWriter(this.responseOutputStream, "UTF-8"));
				
				writer.beginObject();
				
				writer.name("sql").value(sql);
				
				// 写入元数据
				int columnCount = rsMeta.getColumnCount();
				String[] columnNames = new String[columnCount+1];
				
				writer.name("columns");
				writer.beginObject();
				for (int i=1; i<= columnCount; ++i) {
					String columnName = rsMeta.getColumnName(i);
					columnNames[i] = columnName;
					
					writer.name(columnName);
					
					writer.beginObject();
					writer.name("id").value(i);
					writer.name("name").value(columnName);
					writer.name("label").value(rsMeta.getColumnLabel(i));
					writer.name("type").value(rsMeta.getColumnType(i));
					writer.name("typeName").value(rsMeta.getColumnTypeName(i));
					writer.name("displaySize").value(rsMeta.getColumnDisplaySize(i));
					writer.name("className").value(rsMeta.getColumnClassName(i));
					writer.name("tableName").value(rsMeta.getTableName(i));
					writer.name("catalogName").value(rsMeta.getCatalogName(i));
					writer.name("precision").value(rsMeta.getPrecision(i));
					writer.name("scale").value(rsMeta.getScale(i));
					writer.name("schemaName").value(rsMeta.getSchemaName(i));
					writer.name("nullable").value(rsMeta.isNullable(i));
					writer.endObject();
				}
				writer.endObject(); // columns
				
				// 写入查询结果
				writer.name("rows");
				writer.beginArray();
				while (rs.next()) {
					writer.beginObject();
					
					for (int j=1; j<=columnCount; ++j) {
						writer.name(columnNames[j]).value(rs.getString(j));
					}
					writer.endObject();
				}
				writer.endArray(); // rows
				
				writer.endObject(); // 最外层
				
				writer.close();
				return null;
			}

		}
	}

}
