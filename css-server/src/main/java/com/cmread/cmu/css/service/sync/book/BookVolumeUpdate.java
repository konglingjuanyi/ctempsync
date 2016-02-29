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
 * 内容校对：书籍卷名校验
 */
public class BookVolumeUpdate implements TaskHandler {


	@Override
	//暂时把就更新章节信息的单独拉出来，省去查找bookid的过程
	public String getRelatedKey(SyncTask task) {
		return "volume-" + task.getContent().getMap().get("volumeid");

	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String volumeID = task.getContent().getMap().get("volumeid");

		return BaseTHHelper.execTemplate(task, "book", "common", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapBootToCommonAction(volumeID, job);

			}
		});
	}
	
	protected void mapBootToCommonAction(String volumeID, OneToOneJobBuilder job) {
		Action contentCollateAction = BaseDMHelper.mapContentCollate(volumeID, job).diffSyncAction();
		Action volumeUpdateAction = BaseDMHelper.mapBookVolumeByVolumeID(volumeID, job).diffSyncAction();
		
		job.next(volumeUpdateAction).next(contentCollateAction);
	}



}

