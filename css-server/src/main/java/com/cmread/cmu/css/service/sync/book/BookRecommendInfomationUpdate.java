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
 * 用于：
 * 1. 图书库-修改推荐信息/推荐语
 * 2. 质检-图书基本信息审核-推荐语审核通过
 * 
 * 说明：
 * 按照2016-02-01冷东给出的同步内容，质检部分的推荐语同步需要修改t_cmp_type_ebook、con_auditbookinfo两张表，
 * 而原图书库中推荐语修改修改了三张表，其中有一个表(ebook)不重复，暂时复用原推荐语同步，增加ebook表同步；
 * 后期有必要的时候再分开；
 */
public class BookRecommendInfomationUpdate implements TaskHandler {

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
				mapBootToCommonAction(bookID, job);
			}
		});
	}

	protected void mapBootToCommonAction(String bookID, OneToOneJobBuilder job) {
		Action auditBookInfoAction = BaseDMHelper.mapBookAuditBookInfo(bookID, job).diffSyncAction();	
		Action bookExtraAction = BaseDMHelper.mapBookExtraInformation(bookID, job).diffSyncAction();
		Action otherInfoAction = BaseDMHelper.mapBookOtherInfo(bookID, job).diffSyncAction();

		// 参考上面注释，在质检中的推荐语同步中有这个表，图书库的推荐语修改中没有这个表
		Action ebookAction = BaseDMHelper.mapBookEBook(bookID, job).diffSyncAction();
		
		job.next(auditBookInfoAction).next(bookExtraAction).next(otherInfoAction).next(ebookAction);

	}

}
