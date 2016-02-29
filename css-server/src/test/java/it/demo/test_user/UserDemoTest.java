package it.demo.test_user;

import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

@SuppressWarnings("serial")
public class UserDemoTest extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
		initBookDatabase("schema-book.sql");
	}
	
	@Test
	public void testUserInsert() throws Exception {
		initCommonDataset("insert_common_before.xml");
		initBookDataset("insert_book_before.xml");
		initCssDataset("css_task_empty.xml");

		getCssClient().createTask("test_user.insert", new HashMap<String, String>() {
			{
				put("username", "panglaoying");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet commonBefore = readDataSet("insert_common_before.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(commonBefore, bookAfter);

		assertCssSyncTask("insert_css_task_after.xml");
	}

	@Test
	public void testUserUpdate() throws Exception {
		initCommonDataset("update_common_before.xml");
		initBookDataset("update_book_before.xml");

		getCssClient().createTask("test_user.update", new HashMap<String, String>() {
			{
				put("userid", "1");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet commonBefore = readDataSet("update_common_before.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(commonBefore, bookAfter);
	}

	@Test
	public void testUserDelete() throws Exception {
		initCommonDataset("delete_common_before.xml");
		initBookDataset("delete_book_before.xml");

		getCssClient().createTask("test_user.delete", new HashMap<String, String>() {
			{
				put("userid", "2");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet commonBefore = readDataSet("delete_common_before.xml");

		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(commonBefore, bookAfter);
	}

}
