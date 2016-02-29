package com.cmread.cmu.css.service.schedule;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.http.client.RemoteTaskException;
import com.cmread.cmu.css.service.sync.book.BookOnShelfSuccess;

/**
 * 这个定时器，定时扫描common库中的con_on_off_shelfrecord表，取出其中的图书上下架记录，并触发同步。
 * 
 * 这里的扫描采用增量扫描，增量的值以表中的operatiottime为准（精确到秒的时间字段）；
 * 由于没有通过持久化手段保留上次扫描时间，当csync-server退出期间的上下架记录将不会触发同步，有可能会产生遗漏；
 * 另外，这个定时器正常运行需要能够保证老CMU的mrmp进程所在机器的时钟与csync的时钟尽量是保持一致的；
 * 
 * 这个定时器在共存期结束后将不需要再使用；共存期间，定时器触发和cms触发可能同时起作用，导致同一上下架动作会被触发两次同步，
 * 但这种情况仅会导致增加系统消耗，不会引起业务方面的问题；
 * 
 * @author zhangtieying
 *
 */
public class BookOnOffShelfTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(BookOnOffShelfTrigger.class);
	
	private Date lastStopTime;
	private DateFormat dateFormat;
	
	public BookOnOffShelfTrigger() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		if (lastStopTime == null) {
			// 将当前时间提前10秒作为上一次同步时间，减少由于时钟不同步可能导致的log丢失情况
			// 根据现网查询情况，MRMP和csync的服务器时钟基本一致，所以10秒时间应该足够；
			// 维优环境服务器时钟不太一致，这个定时任务可能有问题；
			this.lastStopTime = new Date(new Date().getTime() - 10*1000);
			logger.debug("lastDate is null. set init value is {}", format(lastStopTime));
			return;
		}

		SqlExecutor common = registry.getExecutor("common", null);
		SqlExecutor book = registry.getExecutor("book", null);
		
		// 取当前时间的前10秒作为结束时间，减少由于时钟不同步导致的记录丢失的情况；
		Date thisStopTime = new Date(new Date().getTime() - 10*1000);
		try {
			RowSet onOffShelfRecords = findOnOffBooksBetween(this.lastStopTime, thisStopTime, common);
			this.lastStopTime = thisStopTime;
			
			for (Row shelfBook : onOffShelfRecords.getRows()) {
				if (BookOnShelfSuccess.isBookInBookDB(getBookID(shelfBook), book)) {
					// 触发同步
					createOnOffShelfSyncTask(shelfBook);
				}
			}
		} catch (Exception e) {
			logger.error("on-off-shelf-book-trigger exec failed.", e);
		}
	}
	
	@SuppressWarnings("serial")
	private void createOnOffShelfSyncTask(final Row shelfBook) throws RemoteTaskException {
		String taskType = "book.onshelf.success.do";
		
		if (StringUtils.equals("16", getOperationType(shelfBook))) {
			taskType = "book.offshelf.success.do";
		}
		
		this.clientHelper.getClient().createTask(taskType, new HashMap<String, String>() {
			{
				put("bookid", getBookID(shelfBook));
			}
		});
	}

	private RowSet findOnOffBooksBetween(Date beginTime, Date endTime, SqlExecutor common)
			throws SQLException, InterruptedException, ExecutionException {
		String sql = String.format(
				"select * from con_on_off_shelfrecord where operatiottime >= '%s' and operatiottime < '%s'",
				format(beginTime), format(endTime));
		
		return common.queryForDataSet(sql).get();
	}
	
	private String format(Date date) {
		return dateFormat.format(date);
	}
	
	private String getBookID(Row shelfBook) {
		return StringUtils.defaultString(shelfBook.getFieldValue("bookid").toString(), "");
	}
	
	private String getOperationType(Row shelfBook) {
		return StringUtils.defaultString(shelfBook.getFieldValue("operationtype").toString(), "");
	}
	
}
