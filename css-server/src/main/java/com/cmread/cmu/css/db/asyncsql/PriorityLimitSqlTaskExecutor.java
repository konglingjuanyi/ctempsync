package com.cmread.cmu.css.db.asyncsql;

import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cmread.cmu.css.utils.NamingThreadFactory;

/** 
 * 这是一个支持SQL优先级的SQL限流执行器。
 * 
 * 这个执行器本身没有池化能力，主要实现两个功能：
 * 1. Sql任务的优先级排队；
 * 2. Sql任务的限流，限流算法类似漏桶算法，目前按照（sql数量/秒）的方式限流，并且在一秒内也是匀速的；
 * 
 * 
 * 收到的任务，放到优先级队列中；
 * 启动一个定时器，按照固定频率唤醒，每次唤醒从队列中取出一定数量的sql，交给后面的sql池去运行； 
 * 
 * @author zhangtieying
 *
 */
public class PriorityLimitSqlTaskExecutor implements SqlTaskExecutor {

	private PriorityQueue<DelayedFutureTask<?>> prioQueue;
	private ScheduledExecutorService trafficeGenerator;
	private int sqlNumberPerSecond = 10000; //默认设置为1000
	
	private SqlTaskExecutor proxyedSqlExecutor;
	
	public PriorityLimitSqlTaskExecutor() {
		prioQueue = new PriorityQueue<DelayedFutureTask<?>>();

		// 限流定时器
		this.resetSchedule();
	}

	// FIXME 这个地方很不严谨，需要全面考虑严谨方案；至少是设值的合法性检查等问题；后面放到一个独立的类中去吧
	private long getPeriod() {
		if (sqlNumberPerSecond <= 0) {
			return 1000000; //负数和0都当做1开看；
		}
		return 1000000/sqlNumberPerSecond;
	}

	public void resetSchedule() {
		if (this.trafficeGenerator != null) {
			this.trafficeGenerator.shutdown();
		}
		this.trafficeGenerator = Executors.newScheduledThreadPool(1,
				NamingThreadFactory.createThreadFactory("sqltask-limit-%d"));
		this.trafficeGenerator.scheduleAtFixedRate(new TrafficeGeneratorTask(), 0, getPeriod(), TimeUnit.MICROSECONDS);
	}
	
	/*
	 * 直接将收到的sql任务放到优先级队列中；等待限流定时器取出执行； 
	 */
	@Override
	public <T> Future<T> execute(SqlTask<T> sqlTask) {
		DelayedFutureTask<T> delayedFautre = new DelayedFutureTask<T>(sqlTask);
		synchronized (this) {
			this.prioQueue.add(delayedFautre);
		}
		return delayedFautre;
	}
	
	// 限流定时器任务的主流程
	public void runTask() {
		DelayedFutureTask<?> delayedFautre;
		synchronized (this) {
			delayedFautre = this.prioQueue.poll(); //非阻塞取队头操作，队列空时返回null
		}
		
		if (delayedFautre != null) {
			try {
				Future<?> realFuture = this.proxyedSqlExecutor.execute(delayedFautre.getSqlTask());
				delayedFautre.setRealFuture(realFuture);
			} catch (Exception e) {
				e.printStackTrace();
				delayedFautre.setException(e);
			}
			
		}
	}
	
	/*
	 * 限流定时器任务，运行runTask
	 */
	private class TrafficeGeneratorTask implements Runnable {
		@Override
		public void run() {
			runTask();
		}
	}
	
	public void setSqlNumberPerSecond(int num) {
		this.sqlNumberPerSecond = num;
		this.resetSchedule();
	}
	
	public void setProxyedSqlExecutor(SqlTaskExecutor sqlExecutor) {
		this.proxyedSqlExecutor = sqlExecutor;
	}

	/*
	 * Future的代理
	 * 因为此方法返回时，实际的SqlTask的Future还没有创建（需要下一级SqlTaskExecutor才能创建）
	 * 
	 * 因此，这个Future代表一个延时的Future代理，等实际Future返回时，再转为实际的Future；
	 */
	@SuppressWarnings("rawtypes")
	private class DelayedFutureTask<T> implements Future<T>, Comparable<DelayedFutureTask> {

		private SqlTask<T> sqlTask;
		private Future<?> realFuture; 
		private CountDownLatch realFutureSetted;
		
		private Exception sqlTaskSubmitException; //如果sqlTask还未提交成功即抛出异常，则设置到这里；
		
		public DelayedFutureTask(SqlTask<T> sqlTask) {
			this.sqlTask = sqlTask;
			realFutureSetted = new CountDownLatch(1);
		}
		
		// 在提交任务执行时发生异常，所以不需要等待realFuture了，可以直接抛出异常了
		public void setException(Exception e) {
			sqlTaskSubmitException = e;
			realFutureSetted.countDown();
		}

		public void setRealFuture(Future<?> realFuture2) {
			this.realFuture = realFuture2;
			realFutureSetted.countDown();
		}

		public SqlTask<T> getSqlTask() {
			return this.sqlTask;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			try {
				waitForRealFuture();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return realFuture.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			try {
				waitForRealFuture();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return realFuture.isCancelled();
		}

		@Override
		public boolean isDone() {
			try {
				waitForRealFuture();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return realFuture.isDone();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() throws InterruptedException, ExecutionException {
			waitForRealFuture();
			return (T)realFuture.get();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			waitForRealFuture();
			return (T)realFuture.get(timeout, unit);
		}

		@Override
		public int compareTo(DelayedFutureTask o) {
			return this.sqlTask.compareTo(o.getSqlTask());
		}

		private void waitForRealFuture() throws InterruptedException {
			realFutureSetted.await();
			if (this.sqlTaskSubmitException != null) {
				throw new RuntimeException(this.sqlTaskSubmitException); //这里不对，需要统一设置taskexec异常；
			}
		}

	}

	public void close() {
		this.trafficeGenerator.shutdown();
	}

}
