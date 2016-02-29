package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 质检-图书基本信息审核-封面审核通过
 * 
 * @author zhangtieying
 *
 */
public class BookQcCoverFileAuditPass implements TaskHandler {

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
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();

		// 更新封面相关文件
		// t_cmp_type_coverfile、t_cmp_ref_ebookcoverfile
		//根据word需求文档，需要更新t_cmp_type_file表，但是因为t_cmp_type_file可以表示封面、版权等信息，
		//按理解可能是封面文件的更新  TODO
		Action[] coverFileActions = BaseDMHelper.mapCoverFile(bookID, job);
		
		job.next(auditBookInfoAction).nextSequenceList(coverFileActions);
	}
}
