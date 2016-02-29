package it.basic;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;
import com.cmread.cmu.css.http.client.CSSClient;
import com.cmread.cmu.css.http.client.CSSClientHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-config.xml"})
public abstract class CSSBasicTestCase {

	@Autowired
	private DataSource bookDataSource;
	
	@Autowired
	private DataSource commonDataSource;
	
	@Autowired
	private SqlTaskExecutorServiceRegistry sqlExecutorManager;
	
	protected DataSource cssDataSource;
	
	private IDatabaseTester commonDBTester;
	private IDatabaseTester bookDBTester;
	private IDatabaseTester cssDBTester;
	
	@Autowired
	private CSSClient cssClient;
	@Autowired
	private CSSClientHelper cssClientHelper;
	
	private static List<String> initBeforeAllTestCase = new ArrayList<>();
	
	@Before
	public void setUp() throws Exception {
		this.cssDataSource = this.commonDataSource;
		
		System.out.println("now on setup");
		this.commonDBTester = setupDatabase(commonDataSource);
		this.bookDBTester = setupDatabase(bookDataSource);
		this.cssDBTester = setupDatabase(cssDataSource);
		
		// 每个子类测试用例的setUpBeforeAllTestCase仅能执行一次
		String name = this.getClass().getName();
		if (!CSSBasicTestCase.initBeforeAllTestCase.contains(name)) {
			setUpBeforeAllTestCase();
			CSSBasicTestCase.initBeforeAllTestCase.add(name);
		}
	}

	public void setUpBeforeAllTestCase() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		this.commonDBTester.onTearDown();
		this.bookDBTester.onTearDown();
		this.cssDBTester.onTearDown();
	}
	
	private IDatabaseTester setupDatabase(DataSource ds) throws Exception {
		IDatabaseTester databaseTester = new HsqlDataSourceDBTester(ds);

		return databaseTester;
	}

	public void initBookDatabase(String fileName) throws ScriptException, SQLException {
		this.initDatabase(bookDataSource, fileName);
	}

	public void initCommonDatabase(String fileName) throws ScriptException, SQLException {
		this.initDatabase(commonDataSource, fileName);
	}
	
	private void initDatabase(DataSource ds, String fileName) throws ScriptException, SQLException {
		InputStream is = this.getRelativeFileInputStream(fileName);

		ScriptUtils.executeSqlScript(ds.getConnection(), new InputStreamResource(is));
	}
	
	public void initBookDataset(String initDataSetFile) throws Exception {
		initDataset(this.bookDBTester, initDataSetFile);
	}
	
	public void initCommonDataset(String initDataSetFile) throws Exception {
		initDataset(this.commonDBTester, initDataSetFile);
	}
	
	public void initCssDataset(String initDataSetFile) throws Exception {
		initDataset(this.cssDBTester, initDataSetFile);
	}
	
	public void initDataset(IDatabaseTester dbTester, String initDataSetFile) throws Exception {
		IDataSet initDataSet = readDataSet(initDataSetFile);			
		dbTester.setDataSet(initDataSet);
		dbTester.onSetup();
	}
	
	public IDataSet readDataSet(String fileClassPath) throws DataSetException, IOException {
		InputStream is = this.getRelativeFileInputStream(fileClassPath);

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setDtdMetadata(false);
		
		return replaceDataSet(builder.build(is));
	}
	
	private InputStream getRelativeFileInputStream(String fileName) {
		String packageName = this.getClass().getPackage().getName();
		String relativePackagePath = packageName.replaceAll("\\.", "/") + "/";
		
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(relativePackagePath + fileName);
		return is;
	}
	
	public CSSClient getCssClient() {
		return this.cssClient;
	}
	
	public CSSClientHelper getCssClientHelper() {
		return this.cssClientHelper;
	}
	
	public IDatabaseTester getBookDBTester() {
		return this.bookDBTester;
	}
	
	public IDatabaseTester getCommonDBTester() {
		return this.commonDBTester;
	}
	
	public IDatabaseTester getCssDBTester() {
		return this.cssDBTester;
	}
	
	public SqlTaskExecutorServiceRegistry getSqlExecutorManager() {
		return this.sqlExecutorManager;
	}

	/**
	 * 检验数据集。
	 * 和DBUnit的Assertion.assertEquals的区别是：
	 * 这个方法会按照expect数据集的表和表里的字段来验证actual的；expect中没有包含的表，
	 * 或者expect中的记录没有写的字段不参与比较。
	 * 
	 * @param expected
	 * @param actual
	 * @param ignoreColumns
	 * @throws DatabaseUnitException
	 * @throws IOException 
	 */
	public void assertIDataSet(IDataSet expected, IDataSet actual) throws DatabaseUnitException, IOException {
		for (String tableName : expected.getTableNames()) {
			ITable expectedTable = expected.getTable(tableName);
			ITable actualTable = actual.getTable(tableName);
			
			ITable actualFiletedTable = DefaultColumnFilter.includedColumnsTable(actualTable,
					expectedTable.getTableMetaData().getColumns());
			
			try {
				Assertion.assertEquals(expectedTable, actualFiletedTable);				
			} catch (Throwable e) {
				e.printStackTrace();
				System.out.println("---> expected ---");
				FlatXmlDataSet.write(expected, System.out);
				System.out.println("---> actual ---);");
				FlatXmlDataSet.write(actual, System.out);
				
				throw e;
			}
		}
	}

	public void assertITable(ITable expectedTable, ITable actualTable) throws DatabaseUnitException, IOException {
		ITable actualFiletedTable = DefaultColumnFilter.includedColumnsTable(actualTable,
				expectedTable.getTableMetaData().getColumns());

		try {
			Assertion.assertEquals(expectedTable, actualFiletedTable);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	protected void assertCssSyncTask(String expectedFile) throws SQLException, Exception {
		IDataSet cssExpected = readDataSet(expectedFile);
		
		for (String tableName : cssExpected.getTableNames()) {
			ITable cssActual = getCssDBTester().getConnection().createTable(tableName);
			ITable cssExpectTable = cssExpected.getTable(tableName);
			assertITable(cssExpectTable, cssActual);
		}
	}
	
	/*
	 * 将DataSet文件中的[null]转换为空值
	 */
	private ReplacementDataSet replaceDataSet(IDataSet dataSet) {
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);

        // Configure the replacement dataset to replace '[NULL]' strings with null.
        replacementDataSet.addReplacementObject("[null]", null);

        return replacementDataSet;
    }
	
//	public void assertIDataSet(IDataSet expected, IDataSet actual, String[] ignoreColumns)
//			throws DatabaseUnitException {
//		for (String tableName : expected.getTableNames()) {
//			ITable expectedTable = expected.getTable(tableName);
//			ITable actualTable = actual.getTable(tableName);
//
//			Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, ignoreColumns);
//		}
//	}
}
