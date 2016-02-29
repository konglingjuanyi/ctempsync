package com.cmread.cmu.css.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogbackSqlInvokeFilter extends Filter<ILoggingEvent> {
	
	private Boolean sqlInvokePass; 
	
	public void setSqlInvokePass(boolean sqlInvokePass) {
		this.sqlInvokePass = sqlInvokePass;
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		String sqlInvoke = event.getMDCPropertyMap().get("sql-invoke");
		
		if ((sqlInvokePass && (sqlInvoke == null)) || (!sqlInvokePass && (sqlInvoke != null))) {
			return FilterReply.DENY; //不满足条件，直接过滤掉；
		} 
		
		return FilterReply.NEUTRAL; //执行下一个filter
	}
}
