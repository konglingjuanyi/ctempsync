package com.cmread.cmu.css.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * JDK中的ThreadPoolExecutor在支持优先级队列的时候存在Bug
 * 具体就是在使用优先级队列线程池的submit的时候，一定会抛出classcast异常； 问题这里不详述了...
 * 
 * @author zhangtieying
 *
 */
public class ComparableThreadPoolExecutor extends ThreadPoolExecutor {

	public ComparableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public ComparableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public ComparableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public ComparableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new ComparableFutureTask<>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new ComparableFutureTask<>(callable);
	}

	protected class ComparableFutureTask<V> extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {
		private Object object;

		public ComparableFutureTask(Callable<V> callable) {
			super(callable);
			object = callable;
		}

		public ComparableFutureTask(Runnable runnable, V result) {
			super(runnable, result);
			object = runnable;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compareTo(ComparableFutureTask<V> o) {
			if (this == o) {
				return 0;
			}

			if (o == null) {
				return -1; // high priority
			}

			if (object != null && o.object != null) {
				if (object.getClass().equals(o.object.getClass())) {
					if (object instanceof Comparable) {
						return ((Comparable) object).compareTo(o.object);
					}
				}
			}

			return 0;
		}
	}
}