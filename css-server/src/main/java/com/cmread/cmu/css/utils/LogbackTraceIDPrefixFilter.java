package com.cmread.cmu.css.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * logback的filter的定制类。
 * 这个类主要实现将一个多行日志中的后续行（第二行开始）都加上[traceID]这样的前缀，这样就可以直接使用
 * # grep <traceid> logfile
 * 这样的命令直接抓出一个同步任务的完整的日志。
 * 
 * 目前看，log4jdbc的数据库操作日志中的restultset的表格输出存在这种情况；
 * 
 * 另，由于ILoggingEvent是不能修改消息体的，所以，将格式化修改后的结果放在MDC中，注意在log的配置中要
 * 使用%X{traceMsg}来替代%msg。
 *
 * 这个类被废弃掉，暂时放在这里，备份后清理；
 * 
 * @author zhangtieying
 *
 */
public class LogbackTraceIDPrefixFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		String traceID = event.getMDCPropertyMap().get("traceID");
		String formatedMsg = event.getFormattedMessage();
		System.out.println("-----------------");
		System.out.println(formatedMsg);
		System.out.println("-----------------");
		
		if (traceID != null) {
			String ls = System.lineSeparator();
			String replaceStr = ls + "[" + traceID + "] ";

			String traceMsg = formatedMsg.replace(ls, replaceStr);
			event.getMDCPropertyMap().put("traceMsg", traceMsg);
		} else {
			event.getMDCPropertyMap().put("traceMsg", formatedMsg);
		}
		
		return FilterReply.ACCEPT;
	}
}