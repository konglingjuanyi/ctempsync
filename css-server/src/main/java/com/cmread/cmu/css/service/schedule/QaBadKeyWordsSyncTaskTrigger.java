package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 质检词库的定时同步（仅在共存期内有效）
 */
public class QaBadKeyWordsSyncTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(QaBadKeyWordsSyncTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		try {
			clientHelper.qaBadKeyWordsTableSync();
		} catch (Exception e) {
			logger.error("qa-badkeyword table sync trigger failed.", e);
		} 
	}
}