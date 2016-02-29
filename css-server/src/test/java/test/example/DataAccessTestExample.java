package test.example;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * 这个测试用例主要是为了看一下spring的jdbc模板类的用法，无实际意义
 * @author zhangtieying
 *
 */
public class DataAccessTestExample {
	
	private EmbeddedDatabase db;

	@Before
	public void setUp() throws Exception {
		db = new EmbeddedDatabaseBuilder()
				.generateUniqueName(true)
				.addScript("css-schema.sql")
				.build();
	}

	@After
	public void tearDown() throws Exception {
		db.shutdown();
	}

	@Test
	public void testDataAccess() {
		JdbcTemplate template = new JdbcTemplate(db);
		template.execute("select * from csync_task");
	}

}
