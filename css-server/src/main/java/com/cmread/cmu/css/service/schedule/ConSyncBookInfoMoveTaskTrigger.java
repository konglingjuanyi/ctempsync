package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class ConSyncBookInfoMoveTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(ConSyncBookInfoMoveTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor book = registry.getExecutor("book", null);
		
		try {
			long countNumber = book.queryForLong("select count(*) from con_sync_book_info").get();
			if (countNumber > 0) {
				clientHelper.moveConSyncBookInfo();
			}
		} catch (Exception e) {
			logger.error("get con_sync_book_info countNumber failed.", e);
		} 
	}
}
