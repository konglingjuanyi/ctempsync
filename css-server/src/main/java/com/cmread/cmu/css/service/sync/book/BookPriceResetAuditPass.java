package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 重定价审核通过同步
 * 
 * 需要将重定价和“价格修改”分开
 * 
 * @author zhangtieying
 *
 */
public class BookPriceResetAuditPass implements TaskHandler {

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
				Action bookExtraAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();

				//根据需求文档还需要同步产品信息表，
				Action bookProductinfoAction = BaseDMHelper.mapBookProductinfoByBookid(bookID, job, "bks_productinfohead").diffSyncAction();
				
				job.next(bookExtraAction).next(bookProductinfoAction);
			}
		});
	}

}
