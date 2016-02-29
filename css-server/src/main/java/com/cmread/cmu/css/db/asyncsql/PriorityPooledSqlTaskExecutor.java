package com.cmread.cmu.css.db.asyncsql;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cmread.cmu.css.utils.ComparableThreadPoolExecutor;
import com.cmread.cmu.css.utils.NamingThreadFactory;

/**
 * 带优先级的sql执行器池（通过线程池来实现）
 * 
 * 这里仍然做成优先级队列（暂不考虑性能问题），这样可以在不需要限流的时候跳过限流器直接使用这个执行器
 * 限流部分就可以通过配置跳过限流策略了；
 * 
 * 这个池的线程数量实际应该和数据库连接池大小配成一样大，或者稍大一点，相当于并发执行sql数量；
 * 
 * 这个池的特性包括；
 * 1. 支持sql优先级排队
 * 2. 无限长队列，固定线程数量
 * 
 * @author zhangtieying
 *
 */
public class PriorityPooledSqlTaskExecutor implements SqlTaskExecutor {

	private int threadPoolSize; 
	
	private ThreadPoolExecutor sqlExecutorPool;
	
	public void setThreadPoolSize(int size) {
		this.threadPoolSize = size;
		this.sqlExecutorPool.setCorePoolSize(size);   
		this.sqlExecutorPool.setMaximumPoolSize(size);
	}

	public PriorityPooledSqlTaskExecutor() {
		this.threadPoolSize = 10; //初始化为10

		//注意：优先级队列是无限长队列！！！
		BlockingQueue<Runnable> priorityQueue = new PriorityBlockingQueue<Runnable>(100); 

		this.sqlExecutorPool = new ComparableThreadPoolExecutor(threadPoolSize, threadPoolSize, 100L, TimeUnit.MILLISECONDS,
				priorityQueue, NamingThreadFactory.createThreadFactory("sqltask-pool-%d"));
	}

	@Override
	public <T> Future<T> execute(SqlTask<T> sqlTask) {
		try {
			return sqlExecutorPool.submit(sqlTask);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void close() {
		this.sqlExecutorPool.shutdown();
	}

}
