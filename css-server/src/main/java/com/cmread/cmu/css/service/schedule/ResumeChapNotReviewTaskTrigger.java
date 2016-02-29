/**
 * 
 */
package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

/**
 * @author caidq
 *
 * 2015-12-21
 * 取名字好难。。
 */
public class ResumeChapNotReviewTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(ResumeChapNotReviewTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor book = registry.getExecutor("book", null);
		
		try {
			long notReviewNum = book.queryForLong("select count(*) from bks_resumechapnotreviewoper").get();
			if (notReviewNum > 0) {
				clientHelper.moveResumeChapNotReviewOper();
			}
		} catch (Exception e) {
			logger.error("get notReviewNum failed.", e);
		} 
	}
}
