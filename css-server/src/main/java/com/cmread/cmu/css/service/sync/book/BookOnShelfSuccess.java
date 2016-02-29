package com.cmread.cmu.css.service.sync.book;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.ActionFilter;
import com.cmread.cmu.css.db.utils.RowActioin;
import com.cmread.cmu.css.db.utils.RowUpdateAction;
import com.cmread.cmu.css.http.client.CSSClient;
import com.cmread.cmu.css.http.client.RemoteTaskException;
import com.cmread.cmu.css.service.sync.book.BaseTHHelper.JobAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 上下架同步：上架同步
 * 
 * 
 * T_CMP_TYPE_BOOKITEM.linkCount T_CMP_TYPE_BOOKITEM.onlineTime
 * T_CMP_TYPE_BOOKITEM.lastStatus T_CMP_TYPE_BOOKITEM.onShelfChapterCount
 * 
 * T_CMP_TYPE_EBOOK.showWordCount T_CMP_TYPE_EBOOK.onShelfWordCount
 * T_CMP_TYPE_EBOOK.onShelfDuration
 * 
 * con_auditbookinfo.bookstatus con_auditbookinfo.lastOnShelfChapterName
 * con_auditbookinfo.lastOnShelfChapterTime con_auditbookinfo.onShelfWordCount
 * con_auditbookinfo.waitOnShelfDuration con_auditbookinfo.onShelfDuration
 * 
 * t_cmp_type_content.status
 * 
 * t_cmp_type_chapter.status t_cmp_type_chapter.isHide
 * t_cmp_type_chapter.lastUpdateDate
 * 
 * t_cmp_type_volume.status t_cmp_type_volume.lastUpdateDate
 * 
 * 
 * @author zhangtieying
 *
 */
public class BookOnShelfSuccess implements TaskHandler {
	
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(BookOffShelfSuccess.class);
	
	public static class Checker implements TaskHandler {

		private CSSClient client;
		
		public void setCSSClient(CSSClient client) {
			this.client = client;
		}
		
		@Override
		public String getRelatedKey(SyncTask task) {
			return null;
		}

		@SuppressWarnings("serial")
		@Override
		public TaskResult exec(SyncTask task) {
			final String bookID = BaseTHHelper.getBookID(task);

			try {
				final String toDB = getToDBFromBookItem(bookID, task);
				if (!ArrayUtils.contains(new String[] {"book", "cartoon"}, toDB)) {
					logger.warn("unsupported book type. book id {} type {}", bookID, toDB);
					return TaskResult.FAILED;
				}
				
				if (!isBookInSubDB(bookID, task, toDB)) {
					// 该bookid不在bks数据库中
					logger.info("bookid {} not in {} db. skip sync.", bookID, toDB);
				} else {
					this.client.createTask(task.getTaskType() + ".do", new HashMap<String, String>() {
						{
							put("bookid", bookID);
							put("toDB", toDB);
						}
					});
				}

				return TaskResult.SUCCESS;
			} catch (InterruptedException | ExecutionException | RemoteTaskException e) {
				logger.error("exec task failed", e);
				return TaskResult.FAILED;
			} 
		}
	}
	
	@Override
	public String getRelatedKey(SyncTask task) {
		return BaseTHHelper.getRelatedKeyUsingBookID(task);
	}

	@Override
	public TaskResult exec(final SyncTask task) {
		final String bookID = BaseTHHelper.getBookID(task);
		final String toDB = task.getContent().getMap().get("toDB");

		return BaseTHHelper.execTemplate(task, "common", toDB, new JobAction() {
			@Override
			public void mapAction(OneToOneJobBuilder job) {
				mapCommonToBookAction(bookID, job, toDB);
			}
		});
	}

	public static String getToDBFromBookItem(String bookID, SyncTask task) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor("common");
		RowSet rows = sqlExec.queryForDataSet("select itemtype from t_cmp_type_bookitem where objectid='" + bookID + "'").get();
		if (rows.getRows().size() <= 0) {
			return "";
		}

		String itemType = rows.getRows().get(0).getFieldValue("itemtype").toString();
		switch (itemType) {
		case "1":
			return "book";	// 图书
		case "2":
			return "cartoon"; //漫画
		case "3":
			return "magazine"; // 杂志
		case "5":
			return "audio"; //听书
		case "6":
			return "padmagazine"; //平板杂志？
		case "7":
			return "newspaper"; //报纸？
		default :
			return "";
		}
	}
	
	public static boolean isBookInSubDB(String bookID, SyncTask task, String toDB) throws InterruptedException, ExecutionException {
		SqlExecutor sqlExec = task.getSqlExecutor(toDB);
		return isBookInBookDB(bookID, sqlExec);
	}
	
	public static boolean isBookInBookDB(String bookID, SqlExecutor book) throws InterruptedException, ExecutionException {
		RowSet rows = book.queryForDataSet("select bookid,bookname,mcpid from con_auditbookinfo where bookid='" + bookID + "'").get();
		if (rows.getRows().size() <= 0) {
			return false;
		}

		return true;
	}
	
	protected void mapCommonToBookAction(String bookID, OneToOneJobBuilder job, String toDB) {
		Action bookChapter = mapBookChapter(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		Action bookContent = mapBookContent(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		Action bookVolume = mapBookVolume(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		Action bookItem = mapBookItem(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		Action bookEbook = mapBookEBook(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		Action bookAuditBookInfo = mapBookAuditBookInfo(bookID, job).diffSyncAction(new DiffUpdateActionFilter());
		
		Action bookRecomendOnOffHis = null;
		if (StringUtils.equals("book", toDB)) {
			// 合并上下线日志时间表表至此
			bookRecomendOnOffHis = mapRecomendOnOffHis(bookID, job).diffSyncAction();
		}
		
		job.next(bookChapter).next(bookContent).next(bookVolume).next(bookItem).next(bookEbook).next(bookAuditBookInfo);
		
		if (bookRecomendOnOffHis != null) {
			job.next(bookRecomendOnOffHis);
		}
	}

	/*
	 * 章节信息更新
	 * 
	 * // 注意：下面非全表查询，不能做diff_sync；
	 */
	private static DataMapBuilder mapBookChapter(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_chapter";
		String sql = String.format("select objectid, status, ishide, lastupdatedate from %s where ebookid='%s'",
				tableName, bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}

	/*
	 * 
	 */
	private static DataMapBuilder mapBookContent(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_content";
		String sql = String.format("select objectid, status from %s where objectid='%s'", tableName, bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}

	private static DataMapBuilder mapBookAuditBookInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_auditbookinfo";
		String sql = String.format(
				"select bookid, bookstatus, lastOnShelfChapterName, lastOnShelfChapterTime, onShelfWordCount,waitOnShelfDuration, waitOnShelfDuration from %s where bookid='%s'",
				tableName, bookID);
		dataMap.from().sql(sql).primaryKey("bookid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("bookid");

		return dataMap;
	}

	public static DataMapBuilder mapBookEBook(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_ebook";
		String sql = String.format(
				"select objectid, showWordCount, onShelfWordCount, onShelfDuration from %s where objectid='%s'",
				tableName, bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}

	public static DataMapBuilder mapBookItem(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_bookitem";
		String sql = String.format(
				"select objectid,linkCount,onlineTime,lastStatus,onShelfChapterCount from %s where objectid='%s'",
				tableName, bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}

	/*
	 * 图书卷信息 t_cmp_type_volume
	 * 
	 * 主键：objectid 图书字段: ebookid
	 * 
	 * 说明：需要同步的卷信息需要根据要同步的章节中的volumeid来确定
	 */
	public static DataMapBuilder mapBookVolume(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_volume";
		String sql = String.format("select objectid, status, lastUpdateDate from %s where ebookid='%s'", tableName,
				bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}
	/*
	 * 推荐：图书子系统 推荐库管理 列表中需展示 图书最新上架时间 注意：con_recomend_on_off_his表为日志表，不全量同步，
	 * 根据bookid查询是否为bks侧数据，然后以bookid为主键更新bks_recomend_on_off_his表中对应记录，
	 * 以此方式，bks中该记录表的数量级与book主表等同，而非日志级别
	 * 
	 */

	public DataMapBuilder mapRecomendOnOffHis(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String commonsql = "select bookid, on_shelf_time from (select * from con_recomend_on_off_his where bookid ='"
				+ bookID + "' and on_shelf_time is not null order by on_shelf_time desc) where rownum<2";
		dataMap.from().sql(commonsql).primaryKey("bookid");
		String booksql = "select bookid, on_shelf_time from bks_recomend_on_off_his where bookid='" + bookID + "'";
		dataMap.to().sql(booksql).tableName("bks_recomend_on_off_his").primaryKey("bookid");

		return dataMap;
	}

	public static class DiffUpdateActionFilter implements ActionFilter {

		@Override
		public RowActioin filter(RowActioin action) throws InterruptedException, ExecutionException, SQLException {
			if (action instanceof RowUpdateAction) {
				try {
					// Insert失败暂时忽略，可能是前面原因引起的，暂仅记录；
					int num = action.execute().get();
					logger.debug("exec sql return {}", num);
					return action;
				} catch (Exception e) {
					logger.error("update failed. ignore it now. FIXIT", e);
					return action;
				}
			} else {
				logger.debug("ignore Action that is not RowUpdateAction");
				return null;
			} 
		}
		
	}
}
