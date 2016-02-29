package com.cmread.cmu.css.service.sync.cartoon;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.service.sync.book.BaseDMHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class CartoonChapterDelete implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(CartoonChapterDelete.class);
	
	@Override
	public String getRelatedKey(SyncTask task) {
		try {
			return "cartoon-" + BaseTHHelper.getBookIDByChapterID(task, "cartoon");
		} catch (InterruptedException | ExecutionException e) {
			logger.error("get book id failed.", e);
			return "cartoon-null";
		}
	}

	@Override
	public TaskResult exec(SyncTask task) {
		final String bookID = BaseTHHelper.getBookID(task);
		final String chapterID = BaseTHHelper.getChapterID(task);

		return BaseTHHelper.execTemplate(task, "cartoon", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapCartoonToCommonAction(bookID, chapterID, job);
			}
		});
	}
	
	private void mapCartoonToCommonAction(String bookID, String chapterID, OneToOneJobBuilder job) {
		Action chapterAction   	= BaseDMHelper.mapBookChapterByChapterID(chapterID, job).diffSyncAction();
		
		Action cartoonThumenailAction = BaseDMHelper.mapCartoonThumenailByChapterID(chapterID, job).diffSyncAction();
		Action caricaturePriceAction = BaseDMHelper.mapCaricaturePriceByChapterID(chapterID, job).diffSyncAction();
		
		Action[] chapterEntityFileActions = BaseDMHelper.mapChapterEntityFile(bookID, job);
				
		job.next(chapterAction);
		job.next(cartoonThumenailAction);
		job.next(caricaturePriceAction);
		job.nextSequenceList(chapterEntityFileActions);
	}
	
}
