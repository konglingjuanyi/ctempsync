package it.demo.diffsync;

import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

@SuppressWarnings("serial")
public class TestSimpleRoleDiffSync extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
		initBookDatabase("schema-book.sql");
	}

	@Test
	public void testUserInsert() throws Exception {
		initCommonDataset("diffsync-common-before.xml");
		initBookDataset("diffsync-book-before.xml");

		getCssClient().createTask("simplerole.diff-sync", new HashMap<String, String>() {
			{
				put("roleid", "121");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet commonBefore = readDataSet("diffsync-common-before.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(commonBefore, bookAfter);
	}
}