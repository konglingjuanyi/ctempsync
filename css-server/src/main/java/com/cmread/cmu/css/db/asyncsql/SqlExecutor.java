package com.cmread.cmu.css.db.asyncsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Future;

import org.springframework.dao.DataAccessException;

import com.cmread.cmu.css.db.RowSet;

/**
 * 异步sql执行器。
 * 
 * 本来应该叫AsyncSqlExecutor更合适，但是名字太长... 
 * 
 * 所有通过SQL执行器执行的SQL，实际上都是转换为SqlTask在后台异步执行，根据SqlTaskExecutor
 * 的配置，执行的SQL的执行顺序受到SQL优先级、限流流速和执行器池大小的限制。
 * 
 * 注意：
 * 1. 这里的sql执行器不是作为单例使用，基本上，按照同步模块的结构，应该每个同步任务的每个库都创建一个sql执行器实例；
 * 2. 类里面的方法不能覆盖常见的sql操作，目前基本上是遇到缺少的就补充一个；
 * 
 * TODO 这个类里面的方法名和类结构还需要进一步梳理，目前看还有些乱；
 * TODO 这个类也可以提取为接口，看起来会更加清晰一些
 * 
 * @author zhangtieying
 *
 */
public class SqlExecutor {
	
	private SqlTaskExecutorService sqlTaskService;
	
	private Comparable<Object> initPriority;
	
	private boolean disableNextTaskResultsetLog;

	public SqlExecutor(SqlTaskExecutorService source, Comparable<Object> priority) {
		this.sqlTaskService = source;
		this.initPriority = priority;
		this.disableNextTaskResultsetLog = false;
	}
	
	public SqlExecutor disableNextTaskResultsetLog() {
		this.disableNextTaskResultsetLog = true;
		return this;
	}

	private <T> Future<T> runAsSqlTask(SqlTask.Callable<T> sql) {
		SqlTask<T> sqlTask = initSqlTask();
		sqlTask.setSql(sql);
		return this.sqlTaskService.execute(sqlTask);
	}
	
	public Future<Integer> executeUpdate(final String sql, final Object... args) {
		return runAsSqlTask(new SqlTask.Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				try (Connection conn = this.getDataSource().getConnection();
						PreparedStatement stat = conn.prepareStatement(sql);) {
					if (args.length > 0) {
						for (int i = 1; i <= args.length; ++i) {
							stat.setObject(i, args[i - 1]);
						}
					}
					return stat.executeUpdate();
				}
			}
		});
	}
	
	public Future<Integer> executeUpdate(SqlTask.Callable<Integer> sql) {
		return runAsSqlTask(sql);
	}

	public <T> Future<T> executeQuery(final String querySql, final ResultSetConvert<T> convert) {
		return this.executeQuery(querySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, convert);
	}
	
	public <T> Future<T> executeQuery(final String querySql, final int rsType, final int rsCon, final ResultSetConvert<T> convert) {
		return this.runAsSqlTask(new SqlTask.Callable<T>() {
			@Override
			public T call()  throws Exception{
				Connection conn = this.getDataSource().getConnection(); 
				Statement stat = conn.createStatement(rsType, rsCon);
				
				try (ResultSet rs = stat.executeQuery(querySql);) {
					return convert.convert(rs);
				}
			}
		});
	}
	
	// 返回result总是有危险的，最好能够直接返回sql查询内容，就不需要关闭了
	public Future<RowSet> queryForDataSet(final String sql)  {
		return this.query(sql, new Object[0], new ResultSetToRowSet());
	}
	
	// 返回result总是有危险的，最好能够直接返回sql查询内容，就不需要关闭了
	public Future<RowSet> queryForDataSet(final String sql, final Object[] parameters)  {
		return this.query(sql, parameters, new ResultSetToRowSet());
	}
	
	public Future<Long> queryForLong(final String sql) throws DataAccessException {
		return this.runAsSqlTask(new SqlTask.Callable<Long>() {
			@Override
			public Long call() throws Exception {
				try (Connection conn = this.getDataSource().getConnection();
						Statement stat = conn.createStatement();) {
					try (ResultSet rs = stat.executeQuery(sql);) {
						while (rs.next()) {
							return rs.getLong(1); //不严谨，so简单实现
						}
						return null; //有问题！！！
					}
				} 
			}
		});
	}
	
	public <T> Future<T> query(final String sql, final Object[] parameters, final ResultSetConvert<T> convert) {
		return this.runAsSqlTask(new SqlTask.Callable<T>() {
			@Override
			public T call() throws Exception {
				try (Connection conn = this.getDataSource().getConnection();
						PreparedStatement stat = conn.prepareStatement(sql);) {
					if (parameters.length > 0) {
						for (int i=1; i<=parameters.length; ++i) {
							stat.setObject(i, parameters[i-1]);
						}
					}
					
					try (ResultSet rs = stat.executeQuery();) {
						return convert.convert(rs);
					}
				} 
			}
		});
	}
	
	private <T> SqlTask<T> initSqlTask() {
		SqlTask<T> sqlTask = new SqlTask<T>();
		sqlTask.setPriority(this.initPriority);
		sqlTask.setDataSource(sqlTaskService.getDataSource());
		sqlTask.setDBName(sqlTaskService.getDbName());
		sqlTask.setDisableResultSetLog(this.disableNextTaskResultsetLog);
		this.disableNextTaskResultsetLog = false; //重置，所以这个标志位仅起一次作用；
		
		return sqlTask;
	}

	// 做一个批量并发SQL执行支持，可以先创建一个PatchConcurrentSql batchSqls ;
	// 然后batch Sqls.execupte();
	// 最后，batchSqls.list()，然后依次get检查结果是否正确；
	
	public Connection getConnection() throws SQLException {
		return this.sqlTaskService.getDataSource().getConnection();
	}
	
	public String getDBName() {
		return this.sqlTaskService.getDbName();
	}
	
	// 说明：下面一个类和两个方法，主要是将ResultSet直接从sqlExecutor返回时使用，这种方法需要考虑到ResultSet的关闭问题，
	// 所以不建议使用，将之注释掉；但是不保证以后可能会有使用场景，所以暂未删除；注意:如果使用这两个方法，必须使用createNestedCloseResultSetProxy；
	//
	// 大部分情况下，可以使用对等的public <T> Future<T> executeQuery(final String querySql, final ResultSetConvert<T> convert)
	// 等回调模式的同名方法，相对更加安全；
	
	/*
	 * 一般不要用这个方法，除非返回集特别大；
	 * 
	 * 使用这个接口，需要注意必须自己关闭返回的ResutSet，否则会造成Connection泄漏；
	 * 另，仅需要关闭ResutSet即可，其关联的stat和connection会自动关闭；
	 */
	/*
	public Future<ResultSet> executeQuery(final String querySql) {
		return this.executeQuery(querySql, new ResultSetConvert<ResultSet>() {
			@Override
			public ResultSet convert(ResultSet rs) throws SQLException {
				return createNestedCloseResultSetProxy(rs);
			}
		});
	}
	*/
	
	/*
	 * 一般不要用这个方法，除非返回集特别大；
	 * 
	 * 使用这个接口，需要注意必须自己关闭返回的ResutSet，否则会造成Connection泄漏；
	 * 另，仅需要关闭ResutSet即可，其关联的stat和connection会自动关闭；
	 */
	/*
	public Future<ResultSet> executeQuery(final String querySql, final int rsType, final int rsCon) {
		return this.runAsSqlTask(new SqlTask.Callable<ResultSet>() {
			@Override
			public ResultSet call()  throws Exception{
				Connection conn = this.getDataSource().getConnection(); 
				Statement stat = conn.createStatement(rsType, rsCon);
				
				ResultSet rs = stat.executeQuery(querySql);
				return createNestedCloseResultSetProxy(rs);
			}
		});
	}
	*/
	
	// 创建一个ResultSet的动态代理，当关闭rs的代理对象时，自动关闭将rs关联的Statment和Connection都关闭
	/*
	private ResultSet createNestedCloseResultSetProxy(final ResultSet rs) {
		return (ResultSet) Proxy.newProxyInstance(rs.getClass().getClassLoader(), rs.getClass().getInterfaces(),
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("close".equals(method.getName())) {
							nestedClose();
							return null;
						} else {
							return method.invoke(rs, args);
						}
					}
					private void nestedClose() throws SQLException {
						Statement stat = rs.getStatement();
						Connection conn = stat.getConnection();

						rs.close();
						stat.close();
						conn.close();
					}
				});
	}
	*/

}
