/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 根据bookid进行书籍全量信息的同步
 * 
 */
public class BookAllInfoDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = BaseTHHelper.getBookID(task);

		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapBookToCommonAction(bookID, job);
			}
		});
	}
	
	protected void mapBookToCommonAction(String bookID, OneToOneJobBuilder job) {
		Action chapterAction = BaseDMHelper.mapBookChapterByBookID(bookID, job).diffSyncAction();
		Action volumnAction = BaseDMHelper.mapBookVolumeByBookID(bookID, job).diffSyncAction();

		Action[] coverFileActions = BaseDMHelper.mapCoverFile(bookID, job);
		Action[] copyrightFileActions = BaseDMHelper.mapCopyrightFile(bookID, job);

		Action otherInfoAction = BaseDMHelper.mapBookOtherInfo(bookID, job).diffSyncAction();
		Action extraInfomationAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();
		Action chaperPublishMode = BaseDMHelper.mapBookChapterPublishMode(bookID, job).diffSyncAction();
		Action contentAction = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		// bks_seriesandbook

		Action bookItemAction = BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();

		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
		Action auditBookInfoCompAction = BaseDMHelper.mapBookAuditBookInfoCompBybookID(bookID, job)
				.diffSyncAction();
		Action auditBatchInfoAction = BaseDMHelper.mapBookAuditBatchInfoBybookID(bookID, job)
				.diffSyncAction();
		
		//书对应产品表记录也需要同步，其中包括图书的价格信息
		Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, "bks_productinfohead").diffSyncAction();

		job.next(chapterAction);
		job.next(volumnAction);

		job.next(otherInfoAction);
		job.next(extraInfomationAction);
		job.next(chaperPublishMode);
		job.next(contentAction);

		job.next(bookItemAction);
		job.next(ebookAction);

		job.nextSequenceList(coverFileActions);
		job.nextSequenceList(copyrightFileActions);

		job.next(auditBookInfoAction);
		job.next(auditBookInfoCompAction); // 有疑问
		job.next(auditBatchInfoAction);
		job.next(bookProductinfoAction);
		
	}


}