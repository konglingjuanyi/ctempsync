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
 * 2015-12-10
 * 内容系列同步（通用->图书）
 * 系列增删改。不涉及图书
 */
public class bookSeriesManage implements TaskHandler {

	@Override
	// 无需互斥排队,难得更新的配置直接过了
	public String getRelatedKey(SyncTask task) {
		return null;
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String seriesid = task.getContent().getMap().get("seriesid");

		return BaseTHHelper.execTemplate(task, "common", "book", new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {				
				Action seriesInfoAction = BaseDMHelper.mapSeriesInfoBySeriesID(seriesid, job).diffSyncAction();			
				//Action bookSeriesAction = BaseDMHelper.mapBookSeriesBySeriesID(seriesid, job).diffSyncAction();									
				job.next(seriesInfoAction);
			}
		});
	}
	
//	private static DataMapBuilder mapAuditBookInfo(String bookID, OneToOneJobBuilder job) {
//		DataMapBuilder dataMap = job.createDataMapBuilder();
//
//		String tableName = "con_auditbookinfo";
//		String sql = String.format("select bookid, bookseriesid, lastmodifytime from %s where bookid='%s'", tableName, bookID);
//		dataMap.from().sql(sql).primaryKey("bookid");
//		dataMap.to().sql(sql).tableName(tableName).primaryKey("bookid");
//
//		return dataMap;
//	}

}
