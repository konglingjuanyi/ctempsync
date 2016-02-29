/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.OneToOneJobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 分类信息同步（通用->图书）
 */

/*
 * 新增、修改、删除内容分类需要触发【分类信息同步】,共用算了
 */ 
public class BookClassManager implements TaskHandler {

	@Override
	// 无需互斥排队,难得更新的配置直接过了
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String classcode = task.getContent().getMap().get("classcode");

		JobAction action = new JobAction() {
			public void mapAction(OneToOneJobBuilder job) throws InterruptedException, ExecutionException {
				Action classAction = BaseDMHelper.mapClassByclassCode(classcode, job).diffSyncAction();
				job.next(classAction);
			}
		};
		
		return BaseTHHelper.execOneToManyTemplate(task, "common", 
				new OneToOneJobAction("book", action), new OneToOneJobAction("cartoon", action));
	}

}
