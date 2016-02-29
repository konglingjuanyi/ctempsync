package com.cmread.cmu.css.service.sync.book;

import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.service.sync.book.BookOnShelfSuccess.DiffUpdateActionFilter;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 图书批次审核通过同步；
 * 
 * @author zhangtieying
 *
 */
public class BookAuditBatchPass implements TaskHandler {
	
	private static Logger logger = LoggerFactory.getLogger(BookAuditBatchPass.class);
	
	public void mapBookToCommonAction(String auditBatchID, String bookID, OneToOneJobBuilder job, SyncTask task) {
		Action chapterAction = BaseDMHelper.mapBookChapterByAuditBatchID(auditBatchID, job, task).diffSyncAction();
		Action chapterOrderAction = BaseDMHelper.mapBookChapterSequenceByBookID(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		
		// volumnAction必须在chapterAction之后执行，不能并行执行，否则会出错；自己想想为啥 :)
		// 引申一下，所有通过联查chapter表的情况的Action，都必须在chapterAction之后执行；
		Action volumnAction = BaseDMHelper.mapBookVolumeByAuditBatchID(auditBatchID, job).diffSyncAction();
		
		Action chapterDis = BaseDMHelper.mapBookChapterDisByAuditBatchID(auditBatchID, job).diffSyncAction();
		Action chapterPageInfo = BaseDMHelper.mapBookChapterPageInfoByAuditBatchID(auditBatchID, job).diffSyncAction();

		Action[] coverFileActions = BaseDMHelper.mapCoverFile(bookID, job);
		Action[] copyrightFileActions = BaseDMHelper.mapCopyrightFile(bookID, job);

		Action otherInfoAction = BaseDMHelper.mapBookOtherInfo(bookID, job).diffSyncAction();
		Action extraInfomationAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();
		//干掉抢先设置的表同步
		//Action chaperPublishMode = BaseDMHelper.mapBookChapterPublishMode(bookID, job).diffSyncAction();
		Action contentAction = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		// bks_seriesandbook
		//内容系列表只在首次审核通过后又bks到common,后续均由common到bks
		Action bookSeriesAction = BaseDMHelper.mapBookSeriesByBookid(bookID, job, "bks_seriesandbook").diffSyncAction();

		Action bookItemAction = BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();

		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
		Action auditBookInfoCompAction = BaseDMHelper.mapBookAuditBookInfoCompByAuditBatchID(bookID, auditBatchID, job)
				.diffSyncAction();
		Action auditBatchInfoAction = BaseDMHelper.mapBookAuditBatchInfoByAuditBatchID(auditBatchID, job)
				.diffSyncAction();
		
		//书对应产品表记录也需要同步，其中包括图书的价格信息
		Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, "bks_productinfohead").diffSyncAction();

		job.next(chapterAction);
		job.next(chapterOrderAction);
		
		job.next(volumnAction);
		job.next(chapterDis);
		job.next(chapterPageInfo);

		job.next(otherInfoAction);
		job.next(extraInfomationAction);
		job.next(contentAction);

		job.next(bookItemAction);
		job.next(ebookAction);
		
		job.nextSequenceList(coverFileActions);
		job.nextSequenceList(copyrightFileActions);
		
		job.next(auditBookInfoAction);
		job.next(auditBookInfoCompAction); // 有疑问
		job.next(auditBatchInfoAction);
		job.next(bookProductinfoAction);
		//首批需要同步系列表
		if(isFirstBatch(task)){
			job.next(bookSeriesAction);
		}
		
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		try {
			return "book-" + getBookID(task);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("get book id failed.", e);
			return "book-null"; //出错了
		}
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		try {
			final String auditBatchID = getAuditBatchID(task);
			final String bookID = getBookID(task); 
		
			if (bookID == null) {
				return TaskResult.FAILED;
			}
			
			return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {

				@Override
				public void mapAction(OneToOneJobBuilder job) {
					mapBookToCommonAction(auditBatchID, bookID, job, task);
				}
				
			});
		} catch (Exception e) {
			logger.error("", e);
			return TaskResult.FAILED;
		}
	}
	
	private String getAuditBatchID(SyncTask task) {
		return task.getContent().getMap().get("audit_batch_id");
	}
	
	/*
	 * 通过batchID获得bookid；
	 */
	private String getBookID(SyncTask task) throws InterruptedException, ExecutionException {
		String bookID = task.getContent().getMap().get("bookid");
		if (bookID != null) {
			return bookID;
		} else {
			String auditBatchID = getAuditBatchID(task);
			if ((auditBatchID != null) && (bookID == null)) {
				bookID = queryBookIDByAuditBatchID(auditBatchID, task);
				task.getContent().getMap().put("bookid", bookID);
				return bookID;
			}
			return bookID;
		}
	}
	
	private String queryBookIDByAuditBatchID(String auditBatchID, SyncTask task) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor("book");
		RowSet rows = sqlExec.queryForDataSet("select bookid from bks_auditbatchinfo where auditbatchid='" + auditBatchID + "'").get();
		if (rows.getRows().size() <= 0) {
			return null;
		} 
		
		Object bookID = rows.getRows().get(0).getFieldValue("BOOKID");
		if (bookID != null) {
			return bookID.toString();
		}
		return null;
	}

	public boolean isFirstBatch(SyncTask task) {
		String firstFlag = task.getContent().getMap().get("first_batch");
		return StringUtils.equals("true", firstFlag);
	}

}
