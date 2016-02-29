package com.cmread.cmu.css.service.sync.cartoon;

import com.cmread.cmu.css.service.sync.book.BaseDMHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class CartoonInformationUpdate implements TaskHandler {
	@Override
	public String getRelatedKey(SyncTask task) {
		return "cartoon-" + BaseTHHelper.getBookID(task);
	}

	@Override
	public TaskResult exec(SyncTask task) {
		final String bookID = BaseTHHelper.getBookID(task);

		return BaseTHHelper.execTemplate(task, "cartoon", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapCartoonToCommonAction(bookID, job);
			}
		});
	}
	
	private void mapCartoonToCommonAction(String bookID, OneToOneJobBuilder job) {
		Action chapterAction   	= BaseDMHelper.mapBookChapterByBookID(bookID, job).diffSyncAction();
		Action caricaturePriceAction = BaseDMHelper.mapCaricaturePriceByBookID(bookID, job).diffSyncAction();
		
		Action[] coverFileActions 		= BaseDMHelper.mapCoverFile(bookID, job);
		Action[] copyrightFileActions 	= BaseDMHelper.mapCopyrightFile(bookID, job);
		Action[] ebookEntityFileActions = BaseDMHelper.mapEbookEntityFile(bookID, job);
				
		Action extraInfomationAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();
		Action contentAction 	= BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		Action bookItemAction 	= BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
		Action ebookAction 		= BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
		Action auditBookAction  = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
		
		Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, null).diffSyncAction();

		job.next(chapterAction);
		job.next(caricaturePriceAction);
		
		job.next(ebookAction);
		
		job.nextSequenceList(coverFileActions);
		job.nextSequenceList(copyrightFileActions);
		job.nextSequenceList(ebookEntityFileActions);
		
		job.next(extraInfomationAction);
		job.next(contentAction);
		job.next(bookItemAction);
		
		job.next(auditBookAction);
		job.next(bookProductinfoAction);
	}
	
}
