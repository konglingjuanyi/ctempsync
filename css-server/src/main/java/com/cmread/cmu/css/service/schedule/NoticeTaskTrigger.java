package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class NoticeTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(NoticeTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor book = registry.getExecutor("book", null);
		
		try {
			// 仅找出状态为03（打包成功）的触发打包同步
			long noticeNum = book.queryForLong("select count(*) from bks_notice").get();
			if (noticeNum > 0) {
				clientHelper.moveNotice();
			}
		} catch (Exception e) {
			logger.error("get notice number failed.", e);
		} 
	}
}
