package com.cmread.cmu.css.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * logback的filter定制类
 * 
 * 这个类主要用来过滤是否是task的日志，
 * 当taskPass为true，并且日志是task日志时（truaceID不为null），允许日志写入，否则拒绝；
 * 当taskPass为false, 并且日志不是task日志时，允许日志写入，否则拒绝；
 * 
 * 这个filter主要用于将task日志与其他日志分开，配合logback的appender配置，可以将task日志与其他日志写入到不同的日志文件中；
 * 
 * @author zhangtieying
 *
 */
public class LogbackTaskLogFilter extends Filter<ILoggingEvent> {

	private Boolean taskPass; 
	
	public void setTaskPass(boolean taskPass) {
		this.taskPass = taskPass;
	}
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		String traceID = event.getMDCPropertyMap().get("traceID");
		
		if ((taskPass && (traceID == null)) || (!taskPass && (traceID != null))) {
			return FilterReply.DENY; //不满足条件，直接过滤掉；
		} 
		
		return FilterReply.NEUTRAL; //执行下一个filter
	}
}
