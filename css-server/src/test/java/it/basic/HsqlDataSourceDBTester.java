package it.basic;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;

public class HsqlDataSourceDBTester extends DataSourceDatabaseTester {

	public HsqlDataSourceDBTester(DataSource dataSource) {
		super(dataSource);
	}

	public IDatabaseConnection getConnection() throws Exception {
		IDatabaseConnection conn = super.getConnection();
		
		DatabaseConfig config = conn.getConfig();  
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());  
		config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
		
		return conn;
	}
}
