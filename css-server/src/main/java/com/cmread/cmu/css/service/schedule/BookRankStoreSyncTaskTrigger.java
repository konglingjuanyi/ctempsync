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
 * 2015-12-26
 * 图书销量排行榜同步
 */
public class BookRankStoreSyncTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(BookRankStoreSyncTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor book = registry.getExecutor("book", null);
		
		try {
			long rankStoreNum = book.queryForLong("select count(*) from con_rank_bookstore").get();
			if (rankStoreNum > 0) {
				clientHelper.moveBookRankStores();
			}
		} catch (Exception e) {
			logger.error("get rankStoreNum failed.", e);
		} 
	}
}
