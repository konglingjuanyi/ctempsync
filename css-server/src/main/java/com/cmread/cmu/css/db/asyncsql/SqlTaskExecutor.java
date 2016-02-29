package com.cmread.cmu.css.db.asyncsql;

import java.util.concurrent.Future;

/**
 * 先用实体类，后面抽象出接口来；
 * 可以用代理类模式，一个类实现排队，另一个类实现执行
 * 
 * Sql封装
 */
public interface SqlTaskExecutor {

	<T> Future<T> execute(SqlTask<T> sqlTask);
	
}
