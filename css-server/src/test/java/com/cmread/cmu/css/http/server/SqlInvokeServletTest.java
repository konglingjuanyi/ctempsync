package com.cmread.cmu.css.http.server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cmread.cmu.css.http.server.SqlInvokeServlet.ExecSqlRequest;
import com.cmread.cmu.css.http.server.SqlInvokeServlet.SqlRequest;
import com.cmread.cmu.css.http.server.SqlInvokeServlet.SqlRequestParser;

import it.basic.CSSBasicTestCase;

public class SqlInvokeServletTest extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
	}

//	@Test
//	public void testJsonResult() throws Exception {
//		initCommonDataset("common.xml");
//
//		String sql = "select * from test_table";
//		SqlExecutor sqlExec = this.getSqlExecutorManager().getExecutor("common", null);
//		
//		String jsonStr = new JsonSqlRun().runSql(sql, sqlExec, System.out);
//
//		// not good
//		System.out.println(jsonStr);
//	}

	@Test
	public void testSqlInvokeRequestParse() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sql\"").append(":").append("\"select * from test_table\"").append(",");
		sb.append("\"dbName\"").append(":").append("\"common\"");
		sb.append("}");

		SqlRequest req = SqlRequestParser.parse(sb.toString());
		assertEquals("common", req.getDBName());
		assertEquals(ExecSqlRequest.class, req.getClass());
		assertEquals("select * from test_table", ((ExecSqlRequest)req).getSql());
	}
}
