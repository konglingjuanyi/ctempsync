/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.OneToOneJobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 2015-12-26
 */
public class ChaperPublishModeDiffSync implements TaskHandler {

	@Override
	// 无需互斥排队,难得更新的配置直接过了
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = task.getContent().getMap().get("bookid");

		JobAction action = new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {		
				//此表又双向同步了，昏倒，不管了暂时 TODO
				Action chaperPublishMode = BaseDMHelper.mapBookChapterPublishMode(bookID, job).diffSyncAction();							
				Action auditBookInfoAction=mapAuditBookInfo(bookID, job).updateAction();
				job.next(chaperPublishMode);
				job.next(auditBookInfoAction);
			}
		};
		
		return BaseTHHelper.execOneToManyTemplate(task, "common", 
				new OneToOneJobAction("book", action), new OneToOneJobAction("cartoon", action));
	}
	
	//只更新部分字段，否则会引起数据乱窜，全字段同步可能会把bks侧最新记录替换掉
	private static DataMapBuilder mapAuditBookInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_auditbookinfo";
		String sql = String.format("select bookid, isfirst, priority, lastmodifytime from %s where bookid='%s'", tableName, bookID);
		dataMap.from().sql(sql).primaryKey("bookid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("bookid");

		return dataMap;
	}

}
