package com.cmread.cmu.css.service.sync.book;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/**
 * 
 * @author zhangtieying
 *
 */
public abstract class BaseTHHelper {

	private static Logger logger = LoggerFactory.getLogger(BaseTHHelper.class);
	
	public static String getBookID(SyncTask task) {
		return task.getContent().getMap().get("bookid");
	}

	public static String getChapterID(SyncTask task) {
		return task.getContent().getMap().get("chapterid");
	}
	
	public static String getRelatedKeyUsingBookID(SyncTask task) {
		return "book-" + getBookID(task);
	}
	
	public static String getBookIDByChapterID(SyncTask task, String dbName) throws InterruptedException, ExecutionException {
		String bookID = task.getContent().getMap().get("bookid");
		if (bookID != null) {
			return bookID;
		} else {
			String chapterID = getChapterID(task);
			if ((chapterID != null) && (bookID == null)) {
				bookID = queryBookIDByChapterID(chapterID, task, dbName);
				task.getContent().getMap().put("bookid", bookID);
				return bookID;
			}
			return bookID;
		}
	}
	
	private static String queryBookIDByChapterID(String chapterID, SyncTask task, String dbName) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor(dbName);
		RowSet rows = sqlExec.queryForDataSet("select ebookid from t_cmp_type_chapter where objectid=" + chapterID).get();
		if (rows.getRows().size() <= 0) {
			return null;
		} 
		
		return rows.getRows().get(0).getFieldValue("ebookid").toString();
	}
	
	public static TaskResult execTemplate(SyncTask task, String fromDB, String toDB, JobAction jobAction) {
		try {
			// 创建通用任务模板
			TaskHandlerBuilder tb = new TaskHandlerBuilder();

			// 创建到图书库的一对一同步Job
			OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

			commonToBookJob.fromDB(fromDB);
			commonToBookJob.toDB(toDB);
			
			jobAction.mapAction(commonToBookJob);

			tb.addSubJob(commonToBookJob);

			AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
			autoMapHandler.setJobs(tb.getSubJobs());
			autoMapHandler.setSyncTask(task);

			return autoMapHandler.exec();
		} catch (Exception e) {
			logger.error("exec task failed", e);
			return TaskResult.FAILED;
		}
	}
	
	public static interface JobAction {
		void mapAction(OneToOneJobBuilder job) throws InterruptedException, ExecutionException;
	}
	
	public static TaskResult execOneToManyTemplate(SyncTask task, String fromDB, OneToOneJobAction... jobActions) {
		try {
			// 创建通用任务模板
			TaskHandlerBuilder tb = new TaskHandlerBuilder();

			for (OneToOneJobAction jobAction : jobActions) {
				// 创建到图书库的一对一同步Job
				OneToOneJobBuilder jobBuilder = tb.createOneToOneJob();

				jobBuilder.fromDB(fromDB);
				jobBuilder.toDB(jobAction.getToDB());
				
				jobAction.mapAction(jobBuilder);

				tb.addSubJob(jobBuilder);
			}

			AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
			autoMapHandler.setJobs(tb.getSubJobs());
			autoMapHandler.setSyncTask(task);

			return autoMapHandler.exec();
		} catch (Exception e) {
			logger.error("exec task failed", e);
			return TaskResult.FAILED;
		}
	}
	
	public static class OneToOneJobAction {
		private String toDB;
		private JobAction action;
		
		public OneToOneJobAction(String toDB) {
			this(toDB, null);
		}
		
		public OneToOneJobAction(String toDB, JobAction action) {
			this.toDB = toDB;
			this.action = action;
		}
		
		public String getToDB() {
			return this.toDB;
		}
		
		public void mapAction(OneToOneJobBuilder job) throws InterruptedException, ExecutionException {
			if (action != null) {
				action.mapAction(job);
			}
		}
	}

}
