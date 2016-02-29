package it.demo.ownedplace;

import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

/**
 * 这个测试用例是用来测试ownedplace字段的设置的。
 * 
 * 
 * @author zhangtieying
 *
 */
@SuppressWarnings("serial")
public class TestOwnedPlaceSync extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
		initBookDatabase("schema-book.sql");
	}

	/*
	 * 用一个用例测试了集中情况：
	 * 1. 当bks和cms表中的记录都有ownedplace字段时，在Insert和update时会忽略bks中的字段值，cms的ownedplace字段默认都设置为1；
	 * 2. 仅当cms有ownedplace字段时，当触发Action动作时，会自动设置cms记录的ownedplace字段值为1；
	 */
	@Test
	public void testUserInsert() throws Exception {
		initCommonDataset("common_before.xml");
		initBookDataset("book_before.xml");

		getCssClient().createTask("ownedplace.test", new HashMap<String, String>() {
			{
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet expectCommon = readDataSet("common_after.xml");
		IDataSet commonActual = getCommonDBTester().getConnection().createDataSet();
		assertIDataSet(expectCommon, commonActual);
	}
}
