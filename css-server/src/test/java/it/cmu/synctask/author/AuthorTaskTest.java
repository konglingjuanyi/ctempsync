package it.cmu.synctask.author;

import java.util.HashMap;

import org.dbunit.dataset.IDataSet;
import org.junit.Test;

import it.basic.CSSBasicTestCase;

@SuppressWarnings("serial")
public class AuthorTaskTest extends CSSBasicTestCase {

	public void setUpBeforeAllTestCase() throws Exception {
		initCommonDatabase("schema-common.sql");
		initBookDatabase("schema-book.sql");
	}

	/** 
	 * 测试作家审核通过同步任务
	 * 
	 * @throws Exception
	 */
	
	@Test
	public void testAuthorAuditPass() throws Exception {
		initCommonDataset("common_author_1001017113.xml");
		initBookDataset("book_noauthor.xml");

		getCssClient().createTask("author.audit-pass", new HashMap<String, String>() {
			{
				put("authorid", "1001017113");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet bookExpect = readDataSet("book_author_1001017113.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(bookExpect, bookAfter);
	}

	/** 
	 * 测试作家删除同步任务
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAuthorDelete() throws Exception {
		System.out.println("is here");
		initCommonDataset("common_noauthor.xml");
		initBookDataset("book_author_1001017113.xml");

		getCssClient().createTask("author.delete", new HashMap<String, String>() {
			{
				put("authorid", "1001017113");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet bookExpect = readDataSet("book_noauthor.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(bookExpect, bookAfter);
	}
	
	/** 
	 * 测试作家修改同步任务；
	 * 这里的修改可能是作家基本信息修改、作家认证审核通过等；
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAuthorUpdate() throws Exception {
		initCommonDataset("common_author_1001017113_update1.xml");
		initBookDataset("book_author_1001017113.xml");

		getCssClient().createTask("author.update", new HashMap<String, String>() {
			{
				put("authorid", "1001017113");
				put("sync", "true");
			}
		});

		// 同步后，book库应该和common_before的一样
		IDataSet bookExpect = readDataSet("book_author_1001017113_update1.xml");
		IDataSet bookAfter = getBookDBTester().getConnection().createDataSet();
		assertIDataSet(bookExpect, bookAfter);
	}
}
