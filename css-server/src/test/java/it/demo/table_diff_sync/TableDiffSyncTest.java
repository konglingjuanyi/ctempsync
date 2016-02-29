package it.demo.table_diff_sync;

import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

public class TableDiffSyncTest extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
		initBookDatabase("schema-book.sql");
	}
	
	/**
	 * 测试从book向common同步
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	@Test
	public void testTableDiffSync() throws Exception {
		initBookDataset("book_before.xml");
		initCommonDataset("common_before.xml");

		getCssClient().createTask("test_table_sync.diff", new HashMap<String, String>() {
			{
				put("sync", "true");
			}
		});
		
		// 同步后，book库应该和common_before的一样
		IDataSet commonExpect = readDataSet("book_before.xml");
		IDataSet commonAfter = getCommonDBTester().getConnection().createDataSet();
		assertIDataSet(commonExpect, commonAfter);
	}
}
