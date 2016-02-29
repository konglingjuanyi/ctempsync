/**
 * 
 */
package com.cmread.cmu.css.service.sync.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 2015-12-29
 * 
 * 系列和图书的关联关系管理，系列中增加图书，删除图书
 */
public class bookSeriesRelationManage implements TaskHandler {

	private static Logger logger = LoggerFactory
			.getLogger(bookSeriesRelationManage.class);

	@Override
	// 无需互斥排队,难得更新的配置直接过了
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String[] bookIDs = BaseUtils.getBookIDs(task);
		if (bookIDs.length == 0) {
			logger.warn("task params[bookids] is empty");
			return TaskResult.SUCCESS;
		}

		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {	
				mapActions(bookIDs, job);
			}
		});
	}
	
	// 将多个book映射为多个id
	public void mapActions(String[] bookIDs, OneToOneJobBuilder job) {
		for (String bookID : bookIDs) {
			Action bookSeriesAction = BaseDMHelper.mapBookSeriesByBookID(bookID, job).diffSyncAction();
			Action auditBookInfoAction=mapAuditBookInfo(bookID, job).updateAction();
			Action bookInfoAction=mapBookitem(bookID, job).updateAction();

			job.next(bookSeriesAction).next(auditBookInfoAction).next(bookInfoAction);
		}
	}
	
	//只更新系列字段，否则会引起数据乱窜，全字段同步可能会把bks侧最新记录替换掉
	private static DataMapBuilder mapAuditBookInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_auditbookinfo";
		String sql = String.format("select bookid, bookseriesid, lastmodifytime from %s where bookid='%s'", tableName, bookID);
		dataMap.from().sql(sql).primaryKey("bookid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("bookid");

		return dataMap;
	}
	
	//只更新系列字段，否则会引起数据乱窜，全字段同步可能会把bks侧最新记录替换掉
	private static DataMapBuilder mapBookitem(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_bookitem";
		String sql = String.format("select objectid, seriesid from %s where objectid='%s'", tableName, bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}	

}
