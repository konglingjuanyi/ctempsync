package com.cmread.cmu.css.utils;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

/**
 * logback的encoder的定制类。
 * 
 * 这个类主要使用定制的Layout类，将每条日志中的所有行前面都加上[traceID]这样的前缀，这样就可以直接使用 # grep
 * <traceid> logfile 这样的命令直接抓出一个同步任务的完整的日志。
 * 
 * 这主要是为解决某个日志可能有多行输出，后续输出行无法用前面grep命令抓取，只能用vi逐个查找的问题。
 * 目前看，log4jdbc的数据库操作日志中的restultset的表格输出、异常栈输出等存在这种情况；
 * 
 * 这个方案是较为彻底，但不算灵活的增加前缀的方式，还可以通过filter的扩展来实现更灵活的layout，但是filter的扩展
 * 无法支持exception前面加前缀，所有采用目前方案来实现。
 * 
 * @author zhangtieying
 *
 */
public class LogbackTraceIDPrefixEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

	@Override
	public void start() {
		PatternLayout patternLayout = new TraceIDPrefixBeforeLineLayout();
		patternLayout.setContext(context);
		patternLayout.setPattern(getPattern());
		patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
		patternLayout.start();
		this.layout = patternLayout;
		super.start();
	}
	
	public class TraceIDPrefixBeforeLineLayout extends PatternLayout {
		
		public String doLayout(ILoggingEvent event) {
			if (!isStarted()) {
				return CoreConstants.EMPTY_STRING;
			}
		
			String ss = writeLoopOnConverters(event);
			return addTraceIDPrefixBeforeLine(ss, event);
		}
		
		private String addTraceIDPrefixBeforeLine(String out, ILoggingEvent event) {
			String traceID = event.getMDCPropertyMap().get("traceID");

			String ls = System.lineSeparator();
			String replaceStr = null; 
			if (traceID == null) {
				replaceStr = "[--------------------------------------] ";
			} else {
				replaceStr = "[t:" + traceID + "] ";
			}
			
			String replaceStrWithLn = ls + replaceStr;
			StringBuilder sb = new StringBuilder();
			sb.append(replaceStr);
			if (out.endsWith(ls)) {
				out = out.substring(0, out.length() - ls.length());
				sb.append(out.replace(ls, replaceStrWithLn));
				sb.append(ls);
			} else {
				sb.append(out.replace(ls, replaceStrWithLn));
			}
			
			return sb.toString();
		}
	}

}
