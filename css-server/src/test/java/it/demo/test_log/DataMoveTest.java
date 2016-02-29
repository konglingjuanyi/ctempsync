package it.demo.test_log;
import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

@SuppressWarnings("serial")
public class DataMoveTest extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
	}
	
	/**
	 * 测试move类型的任务；同时这个任务配置为“非持久化”；
	 */
	@Test
	public void testMove() throws Exception {
		initCommonDataset("move_common_before.xml");
		initCssDataset("css_task_empty.xml");
		
		getCssClient().createTask("test_log.move", new HashMap<String, String>() {
			{
				put("operator", "panglaoying");
				put("logtime", "2015-10-25 14:14:14");
				put("sync", "true");
			}
		});
		
		// 同步后，book库应该和common_before的一样
		IDataSet commonExpected = readDataSet("move_common_after.xml");
		IDataSet commonActual = getCommonDBTester().getConnection().createDataSet();
		assertIDataSet(commonExpected, commonActual);

		// 配置为非持久化的任务，在持久化表中无相应记录存在；
		assertCssSyncTask("css_task_empty.xml");
	}
}
