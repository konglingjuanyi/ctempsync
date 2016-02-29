package com.cmread.cmu.css.service.sync.book;

import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 图书打包结束同步；
 * 
 * @author caidq
 * 
 */
public class BookPackDone implements TaskHandler{
	
	private static Logger logger = LoggerFactory.getLogger(BookPackDone.class);

	@Override
	public String getRelatedKey(SyncTask task) {
		try {
			return "book-" + getBookID(task);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("get book id failed.", e);
			return "book-null"; //出错了
		}
	}
	
	private String getPackageID(SyncTask task) {
		return task.getContent().getMap().get("packageid");
	}
	
	/*
	 * 通过打包队列pkid获得bookid；
	 */
	private String getBookID(SyncTask task) throws InterruptedException, ExecutionException {
		String bookID = task.getContent().getMap().get("bookid");
		if (bookID != null) {
			return bookID;
		} else {
			String packageID = getPackageID(task);
			if ((packageID != null) && (bookID == null)) {
				bookID = queryBookIDByPackageID(packageID, task);
				task.getContent().getMap().put("bookid", bookID);
				return bookID;
			}
			return bookID;
		}
	}
	
	private String queryBookIDByPackageID(String packageID, SyncTask task) throws InterruptedException, ExecutionException {
		Row row = queryPackageRowByPackageID(packageID, task, "book");
		
		if (row != null) {
			Object bookID = row.getFieldValue("BOOKID");
			if (bookID != null) {
				return bookID.toString();
			}
		}
		return null;
	}
	
	private Row queryPackageRowByPackageID(String packageID, SyncTask task, String dbName) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor(dbName);
		RowSet rows = sqlExec.queryForDataSet("select * from con_package_queue where pkid='" + packageID + "'").get();
		if (rows.getRows().size() <= 0) {
			return null;
		} 
		
		return rows.getRows().get(0);
	}
	
	private RowSet queryBatchIDByPackageID(String packageID, SyncTask task, String dbName) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor(dbName);
		return sqlExec.queryForDataSet("select * from con_package_batch where pkid='" + packageID + "'").get();
	}
	
	private int deleteBookPackageQueueItem(String packageID, SyncTask task) throws InterruptedException, ExecutionException {
		SqlExecutor bookSql = task.getSqlExecutor("book");
		String sql = String.format("delete from con_package_queue where pkid='%s'", packageID);
		return bookSql.executeUpdate(sql).get();
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		try {
			final String packageID = getPackageID(task);
			final String bookID = getBookID(task); 			
		
			if (bookID == null) {
				// 本地记录已经被删除，有两种可能性：
				// 1. 这个同步记录已经被处理，并被删除；
				// 2. 这个pkid是错误的；
				// 前一种应该算是成功；后一种应该算是失败；但这里很难分清楚，所以总体算是成功吧；
				return TaskResult.SUCCESS;
			}

			Row commonRow = queryPackageRowByPackageID(packageID, task, "common");
			if (commonRow != null) {
				//表示已经同步过去了，不需要同步了，而且可以删除本地的记录了；
				this.deleteBookPackageQueueItem(packageID, task);
				return TaskResult.SUCCESS;
			}
			
			TaskResult result =  BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {

				@Override
				public void mapAction(OneToOneJobBuilder job) throws InterruptedException, ExecutionException {
					mapBookToCommonAction(packageID, bookID, job, task);
				}
				
			});
			
			if (result == TaskResult.SUCCESS) {
				this.deleteBookPackageQueueItem(packageID, task);
			}
			
			return result;
		} catch (Exception e) {
			logger.error("", e);
			return TaskResult.FAILED;
		}
	}	

	/*
	 * 策略：
	 * 1. 当状态为03的记录可以同步到common中；同步成功后book侧可以删除book这一条记录；
	 * 2. 同步开始时，如果common中已经有相同的pkid的记录，则删除本地记录，不再同步；
	 * 
	 */
	public void mapBookToCommonAction(String packageID, String bookID, OneToOneJobBuilder job, SyncTask task) throws InterruptedException, ExecutionException {
		// 暂定为根据pkid来同步
		Action packageQueueAction = BaseDMHelper.mapBookPackageQueue(packageID, job).diffSyncAction();
		Action packageBatchAction = BaseDMHelper.mapBookPackageBatch(packageID, job).diffSyncAction();

		for (Row row : queryBatchIDByPackageID(packageID, task, "book").getRows()) {
			Object batchid = row.getFieldValue("batchid");
			if (batchid != null) {
				String batchID = batchid.toString();
				if (!StringUtils.strip(batchID).isEmpty()) {
					Action chapterDis = BaseDMHelper.mapBookChapterDisByAuditBatchID(batchid.toString(), job)
							.diffSyncAction();
					Action chapterPageInfo = BaseDMHelper.mapBookChapterPageInfoByAuditBatchID(batchid.toString(), job)
							.diffSyncAction();

					job.next(chapterDis);
					job.next(chapterPageInfo);
				}
			}
		}

		Action[] mebfileActions = BaseDMHelper.mapMebFile(bookID, job);

		job.nextSequenceList(mebfileActions);

		job.next(packageBatchAction);
		job.next(packageQueueAction);
	}

}
