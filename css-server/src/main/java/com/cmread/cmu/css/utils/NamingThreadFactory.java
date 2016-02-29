package com.cmread.cmu.css.utils;

import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可命名的线程工厂，主要用于程序其他需要使用线程池的地方，可命令相对友好的线程名，方便故障跟踪和调测。
 * 
 * @author zhangtieying
 *
 */
public abstract class NamingThreadFactory {

	private static Logger logger = LoggerFactory.getLogger(NamingThreadFactory.class);
	
	/*
	 * @param namingPattern 比如 "mythread-%d"
	 */
	public static ThreadFactory createThreadFactory(String namingPattern) {
		 BasicThreadFactory factory = new BasicThreadFactory.Builder()
			     .namingPattern(namingPattern)
			     .uncaughtExceptionHandler(new CssThreadUncaughtExceptionHandler())
			     .build();
		 return factory;
	}
	
	/*
	 * 线程异常捕捉器
	 */
	public static class CssThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("receive uncaughtException", e);
		}
		
	}
}
