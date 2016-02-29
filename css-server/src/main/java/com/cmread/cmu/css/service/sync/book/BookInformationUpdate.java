package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/*
 * 用于以下同步：
 * 1. 图书库-内容校对：基本信息校对/修改 同步
 * 2. 质检-图书基本信息审核 - 简介审核通过：book.qc.introduction.audit-pass
 * 2. 质检-图书基本信息审核 - 关键字神审核通过：book.qc.keyword.audit-pass
 * 
 * 说明：质检的同步（包括2和3），按照冷东文档，仅需同步t_cmp_type_bookitem、con_auditbookinfo两张表，可以被内容校对同步覆盖，
 * 为简单起见，暂时复用原内容校对同步；
 */
public class BookInformationUpdate implements TaskHandler {

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
		Action contentAciton = BaseDMHelper.mapBookContent(bookID, job).diffSyncAction();
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
		Action itemAction = BaseDMHelper.mapBookItem(bookID, job).diffSyncAction();
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();
		
		//根据word需求文档，需要更新t_cmp_type_file表，但是因为t_cmp_type_file可以表示封面、版权等信息，
		//按理解可能是封面文件的更新  TODO
		Action[] coverFileAction = BaseDMHelper.mapCoverFile(bookID, job);
		
		Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, "bks_productinfohead").diffSyncAction();
		//内容系列表只在首次审核通过后又bks到common,后续均由common到bks
		//Action bookSeriesAction = BaseDMHelper.mapBookSeriesByBookid(bookID, job).diffSyncAction();
		
		job.next(contentAciton).next(ebookAction).next(itemAction).next(auditBookInfoAction);
		job.nextSequenceList(coverFileAction).next(bookProductinfoAction);
	}

}
