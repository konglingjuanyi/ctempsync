package com.cmread.cmu.css.service.sync.book;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.ActionFilter;
import com.cmread.cmu.css.db.utils.DiffSyncFilter;
import com.cmread.cmu.css.db.utils.RowActioin;
import com.cmread.cmu.css.db.utils.RowDeleteAction;
import com.cmread.cmu.css.db.utils.RowInsertAction;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.DataMap;
import com.cmread.cmu.css.task.handler.DataMap.Filter;
import com.cmread.cmu.css.task.handler.RowActionList;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/*
 * 图书相关表的数据映射关系太多，为方便管理，从基类中抽离出来，统一放在这个类里整理
 * 
 * 后面的同志辛苦了，不是我不想整理清楚，实在是敌人太狡猾！！！
 */
public abstract class BaseDMHelper {

	private static Logger logger = LoggerFactory.getLogger(BaseDMHelper.class);

	private static class SpecialStatusFilter implements DiffSyncFilter {

		private String[] statsValues = new String[] { "12", "13", "14", "16", "17" };

		@Override
		public void sameRowFilter(Row from, Row to) {
			Object fromStatus = from.getFieldValue("status");
			Object toStatus = to.getFieldValue("status");
			if (((fromStatus != null) && (toStatus != null)) && (fromStatus.toString() != toStatus.toString())) {
				for (String filterdStatus : statsValues) {
					if (toStatus.toString().equals(filterdStatus)) {
						// 不比较status，将其从row中删除
						from.removeField("status");
						to.removeField("status");
						break;
						// TODO 以上方法性能优化
						// TODO 需要确认除了status外，还要过滤哪几个属性
					}
				}
			}
		}
	}

	/*
	 * 映射：章节信息表 t_cmp_type_chapter
	 * 
	 * 主键 objectid 图书字段 ebookid 批次id auditbatchid
	 * 
	 * 说明： 需要同步的章节要根据批次id查询，而不是图书ID
	 * 
	 * 需要注意的是： 1. TODO 如果两侧的STATUS状态不同，而接收端的STATUS状态为[12, 13, 14, 16,
	 * 17]，则不更新common的status状态； 这种情况必须使用diff-sync； 2.
	 * 接收端表结构中有[OWNEDPLACE]字段，则此字段自动设置为1；
	 */
	public static DataMapBuilder mapBookChapterByAuditBatchID(String auditBatchID, OneToOneJobBuilder subJob,
			final SyncTask task) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		final String tableName = "t_cmp_type_chapter";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("auditbatchid", auditBatchID)
				.orderBy("ordernum asc").addFilter(new DataMap.Filter() {

					@Override
					public void filter(RowSet rowSet) throws InterruptedException, ExecutionException {
						// 这个filter是为了提取参数，去更新当前批次的上一批次最后一个章节的下一章节字段
						// 也就是当前批次最前面一个章节的在前面章节的下一章节字段 o(╯□╰)o
						if (rowSet.size() > 0) {
							Row row = rowSet.getRows().get(0);
							String preChapter = row.getFieldValue("prechapterid").toString();
							String firstChapter = row.getFieldValue("objectid").toString();

							logger.info("find firstchapter of this batch. objectid={} prechapterid={}", firstChapter,
									preChapter);
							
							if (!"0".equals(preChapter)) {
								//非首批首章
								SqlExecutor common = task.getSqlExecutor("common");
								String updateSql = String.format("update %s set nextchapterid=%s where objectid=%s and nextchapterid=0", tableName, firstChapter, preChapter); 
								
								int num = common.executeUpdate(updateSql).get();
								logger.info("exec update return " + num);
							}
						}
					}

				});
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("auditbatchid", auditBatchID);
		dataMap.addDiffSyncFilter(new SpecialStatusFilter());

		return dataMap;
	}
	
	/*
	 * 相当于是对图书的t_cmp_type_chapter中的所有记录的ordernum，nextchapterid和prechapterid进行同步；
	 * 之所以增加这个是因为在编辑乱序审核批次的时候，对编审这边如何处理这三个字段的策略目前还没有完全搞清楚，可能会存在遗漏，
	 * 为数据准确性，这里在批次同步中加入对所有章节的这三个字段的同步，后期搞清楚以后可以考虑是否去掉这个同步；
	 * 理论上，如果上面策略正确，这个同步应该不会触发update操作的，后期可以从日志中确认这一点；
	 */
	public static DataMapBuilder mapBookChapterSequenceByBookID(String bookID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String sql = String.format(
				"select objectid, ordernum, prechapterid, nextchapterid from t_cmp_type_chapter where ebookid=%s order by ordernum asc",
				bookID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().tableName("t_cmp_type_chapter").sql(sql).primaryKey("objectid");

		return dataMap;
	}

	/*
	 * 图书卷信息 t_cmp_type_volume
	 * 
	 * 主键：objectid 图书字段: ebookid
	 * 
	 * 说明：需要同步的卷信息需要根据要同步的章节中的volumeid来确定
	 */
	public static DataMapBuilder mapBookVolumeByAuditBatchID(String auditBatchID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_volume";
		String sql = String.format(
				"select * from %s where objectid in (select volumeid from t_cmp_type_chapter where auditbatchid='%s')",
				tableName, auditBatchID);
		dataMap.from().sql(sql).primaryKey("objectid");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("objectid");

		return dataMap;
	}
	
	/*
	 * 此同步用于批次同步，记录章节拆分信息；
	 * 加入原因：割接cp的新增图书在手机客户端无法看到，查找后确定为缺少此表；
	 * 
	 * 问题：此表较大，约为上亿级别（章节数*n的量级），可能需要优化
	 * 
	 * add by 2016-02-02 zhangtieying
	 */
	public static DataMapBuilder mapBookChapterDisByAuditBatchID(String auditBatchID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_bookchapter_dis";
		
		// 注意这个to_char啊，加不加性能差很多很多！！！
		String sql = String.format(
				"select * from %s where chapterid in (select to_char(objectid) from t_cmp_type_chapter where auditbatchid='%s')",
				tableName, auditBatchID);
		dataMap.from().sql(sql).primaryKey("chapterid", "sort");
		dataMap.to().sql(sql).tableName(tableName).primaryKey("chapterid", "sort");

		return dataMap;
	}
	
	/*
	 * 此同步用于批次同步，记录章节分页信息；
	 * 加入原因：割接cp的新增图书在手机客户端无法看到，查找后确定为缺少此表；（和上面一样）
	 * 
	 * 问题：此表较大，约为5千多万级别（章节数*n的量级），可能需要优化
	 * 
	 * add by 2016-02-02 zhangtieying
	 */
	public static DataMapBuilder mapBookChapterPageInfoByAuditBatchID(String auditBatchID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		// 注意这个to_char啊，加不加性能差很多很多！！！
		String sqlFormat = "select * from %s where chapterid in (select objectid from t_cmp_type_chapter where auditbatchid=%s)";
		String commonSql = String.format(sqlFormat, "con_chapter_pageinfo", auditBatchID);
		String bookSql = String.format(sqlFormat, "bks_chapter_pageinfo", auditBatchID);
		dataMap.from().sql(bookSql).primaryKey("chapterid", "pageorder");
		dataMap.to().sql(commonSql).tableName("con_chapter_pageinfo").primaryKey("chapterid", "pageorder");

		return dataMap;
	}

	/*
	 * 图书附加信息（1）
	 * 
	 * ：）终于有一个可以一次搞定的 ，兄弟，表都像你这样简单多好啊！！！
	 */
	public static DataMapBuilder mapBookOtherInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_book_otherinfo").primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName("con_book_otherinfo").primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 图书附件信息（2）
	 */
	public static DataMapBuilder mapBookExtraInformation(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_bookextrainformation";
		dataMap.from().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 图书附件信息（3）
	 */
	public static DataMapBuilder mapBookChapterPublishMode(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_chaperpublishmode";
		dataMap.from().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 图书附件信息（4）
	 * 
	 * 这个表的objectid应该就是bookid :book_content => { :from =>
	 * 'book.t_cmp_type_content', :to => 'common.t_cmp_type_content', :key =>
	 * ['OBJECTID'], :reverse_conf => { "STATUS" => [12, 13, 14, 16, 17] } },
	 * 状态位处理
	 */
	public static DataMapBuilder mapBookContent(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_content";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);

		return dataMap;
	}

	// FIXME 这里需要对表进行同步分析和确认 con_seriesandbook!!!! 目前还没有配置同步！！！

	/*
	 * 图书基本信息（1） 无特殊处理
	 */

	public static DataMapBuilder mapBookItem(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_bookitem";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);

		return dataMap;
	}

	/*
	 * 图书基本信息（2）
	 * 
	 * :book_ebook => { :from => 'book.t_cmp_type_ebook', :to =>
	 * 'common.t_cmp_type_ebook', :key => ['OBJECTID'], :ignore => ['AUDITIDEA',
	 * 'TESTNEWCOLUMN', 'BOOKLEVEL'], :convert_to_string => ['ISSUPPORTPIC']
	 * (待测，不转又会怎样？） },
	 */
	public static DataMapBuilder mapBookEBook(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_ebook";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("objectid", bookID);

		return dataMap;
	}

	/*
	 * 图书编审信息（1）
	 * 
	 * # 这个同步涉及到反向状态更新！！！ :book_auditbookinfo => { :from =>
	 * 'book.con_auditbookinfo', :to => 'common.con_auditbookinfo', :key =>
	 * ['BOOKID'], :reverse_conf => { "BOOKSTATUS" => [12, 13, 14, 16, 17] （待处理）
	 * } },
	 */
	public static DataMapBuilder mapBookAuditBookInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_auditbookinfo";
		dataMap.from().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * con_auditbookinfocomp表中，(bookid、auditbatchid）是联合索引，所以查询条件为两个，否则会比较慢；
	 */
	public static DataMapBuilder mapBookAuditBookInfoCompByAuditBatchID(String bookID, String auditBatchID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_auditbookinfocomp").primaryKey("auditbatchid").cond("bookid", bookID).cond("auditbatchid", auditBatchID);
		dataMap.to().tableName("con_auditbookinfocomp").primaryKey("auditbatchid").cond("bookid", bookID).cond("auditbatchid", auditBatchID);

		return dataMap;
	}

	/*
	 * 
	 * 这个表用来干嘛的啊，真是乱！ 修改关联作家信息怎么要改这个表？
	 */
	public static DataMapBuilder mapBookAuditBookInfoCompBybookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_auditbookinfocomp").primaryKey("auditbatchid").cond("bookid", bookID);
		dataMap.to().tableName("con_auditbookinfocomp").primaryKey("auditbatchid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 
	 * :book_auditbatchinfo => { :from => 'book.bks_auditbatchinfo', :to =>
	 * 'common.con_auditbatchinfo', :key => ['AUDITBATCHID'], :ignore =>
	 * ['PROCESSNAME'], :reverse_conf => { "AUDITBATCHSTATUS" => [12, 13, 14,
	 * 16, 17] # 注意状态也需要处理一下 } }, TODO 这个有问题：批次和图书的关系，哪个是同步的起点？
	 */
	public static DataMapBuilder mapBookAuditBatchInfoByAuditBatchID(String auditBatchID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_auditbatchinfo").primaryKey("auditbatchid").cond("auditbatchid", auditBatchID);
		dataMap.to().tableName("con_auditbatchinfo").primaryKey("auditbatchid").cond("auditbatchid", auditBatchID);

		return dataMap;
	}

	/*
	 * 打包队列表（3） FIXME 较复杂！！！
	 * 
	 * :book_package_queue => { :from => 'book.con_package_queue', :to =>
	 * 'common.con_package_queue', :key => ['PKID'], :reverse_conf => { #
	 * 打包状态---01,待打包;02,打包中;03,打包成功;04,打包失败;05,处理打包成功记录异常;06,待删除;07,重复记录不处理;10,
	 * 放弃重试 # 原表中没有08和09，为保险起见，把09也加上了 :) "PKSTATUS" => ['05', '06', '07', '08',
	 * '09', '10'], }, :reverse_action => :ignore, :delete_conf => { "PKSTATUS"
	 * => ['08'], }, # 删除行为先写死 },
	 */
	public static DataMapBuilder mapBookPackageQueue(String packageID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_package_queue";
		dataMap.from().tableName(tableName).primaryKey("pkid").cond("pkid", packageID).addFilter(new Filter() {

			@Override
			public void filter(RowSet rowSet) throws InterruptedException, ExecutionException {
				for (Row r : rowSet.getRows()) {
					r.setField("CONVERTSTATUS", null);
				}
			}
			
		});
		dataMap.to().tableName(tableName).primaryKey("pkid").cond("pkid", packageID);
		dataMap.addDiffSyncFilter(new DiffSyncFilter() {

			private String[] statsValues = new String[] { "05", "06", "07", "08", "09", "10" };

			@Override
			public void sameRowFilter(Row from, Row to) {
				String toStatus = (String) to.getFieldValue("PKSTATUS");
				for (String filterStatus : statsValues) {
					if (StringUtils.equals(filterStatus, toStatus)) {
						from.removeField("pkstatus");
						to.removeField("pkstatus");
						break;
					}
				}
			}
		});

		return dataMap;
	}

	/*
	 * 批次队列
	 * 
	 * :book_package_batch => { :from => 'book.con_package_batch', :to =>
	 * 'common.con_package_batch', :key => ['PKID', 'BATCHID'] },
	 * 
	 */
	public static DataMapBuilder mapBookPackageBatch(String packageID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_package_batch";
		dataMap.from().tableName(tableName).primaryKey("pkid", "batchid").cond("pkid", packageID);
		dataMap.to().tableName(tableName).primaryKey("pkid", "batchid").cond("pkid", packageID);

		return dataMap;
	}

	/*
	 * 系统分册策略同步 { :from => 'book.bks_systemtactic', :to =>
	 * 'common.con_systemtactic', :key => ['TACTICID'] },
	 */
	public static DataMapBuilder mapBookSystemtactic(String tacticID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("con_systemtactic").primaryKey("tacticid").cond("tacticid", tacticID);
		dataMap.to().tableName("bks_system_tactic").primaryKey("tacticid").cond("tacticid", tacticID);

		return dataMap;
	}

	/*
	 * 分册信息同步（1） :book_fascicule_info => { :from => 'book.con_fascicule_info',
	 * :to => 'common.con_fascicule_info', :key => ['FASCICULEID'] }, # TODO
	 * 没数据，未测试 表结构一致
	 * 
	 */
	public static DataMapBuilder mapBookFasciculeInfo(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();
		// book端怎么bks_fascicule_info和con_fascicule_info都有？到底用哪张，需确认？
		String tableName = "con_fascicule_info";
		dataMap.from().tableName(tableName).primaryKey("fasciculeid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("fasciculeid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 分册信息同步（2）
	 * 
	 * :book_fascicule => { :from => 'book.bks_book_fascicule', :to =>
	 * 'common.con_book_fascicule', :key => ['BOOKID'], # TODO 待确认 }, # TODO
	 * 没数据，未测试
	 * 
	 */
	public static DataMapBuilder mapBookFascicule(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_book_fascicule").primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName("con_book_fascicule").primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 
	 * 更新章节信息 t_cmp_type_chapter 此处根据章节id来同步,多处可用到
	 */
	public static DataMapBuilder mapBookChapterByChapterID(String chapterID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String tableName = "t_cmp_type_chapter";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("objectid", chapterID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("objectid", chapterID);
		dataMap.addDiffSyncFilter(new SpecialStatusFilter());

		return dataMap;
	}

	/*
	 * 
	 * 更新章节信息 t_cmp_type_chapter 此处根据bookid来同步,多处可用到
	 */
	public static DataMapBuilder mapBookChapterByBookID(String bookID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String tableName = "t_cmp_type_chapter";
		dataMap.from().tableName(tableName).primaryKey("objectid").cond("ebookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("ebookid", bookID);
		dataMap.addDiffSyncFilter(new SpecialStatusFilter());

		return dataMap;
	}

	/*
	 * 精装图书关联表同步 beautybook_relate{ :from => 'common.con_beautybook_relate', :to
	 * => 'book.bks_beautybook_relate', :key => ['BEAUTYBOOKID'] },
	 */
	public static DataMapBuilder mapBeautybook(String beautybookID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("con_beautybook_relate").primaryKey("beautybookid").cond("beautybookid", beautybookID);
		dataMap.to().tableName("bks_beautybook_relate").primaryKey("beautybookid").cond("beautybookid", beautybookID);

		return dataMap;
	}

	// --------------------------------内容校验------------------------------------

	/*
	 * 记录内容校验信息表 content_collate{ :from => 'book.bks_content_collate', :to =>
	 * 'commmon.con_content_collate', :key => ['RECORDID'] },
	 * 
	 * collateID为修改的卷或者章节的id
	 */
	public static DataMapBuilder mapContentCollate(String collateID, OneToOneJobBuilder subJob) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		dataMap.from().tableName("bks_content_collate").primaryKey("recordid").cond("collateid", collateID);
		dataMap.to().tableName("con_content_collate").primaryKey("recordid").cond("collateid", collateID);

		return dataMap;
	}

	/*
	 * 卷校验
	 * 
	 * 图书卷信息 t_cmp_type_volume
	 * 
	 * 主键：objectid 此处根据卷id来更新卷信息
	 */
	public static DataMapBuilder mapBookVolumeByVolumeID(String volumeID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_volume";

		dataMap.from().tableName(tableName).primaryKey("objectid").cond("objectid", volumeID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("objectid", volumeID);

		return dataMap;
	}

	/*
	 * 卷校验
	 * 
	 * 图书卷信息 t_cmp_type_volume
	 * 
	 * 主键：objectid 此处根据书id来更新卷信息
	 */
	public static DataMapBuilder mapBookVolumeByBookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "t_cmp_type_volume";

		dataMap.from().tableName(tableName).primaryKey("objectid").cond("ebookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("objectid").cond("ebookid", bookID);

		return dataMap;
	}

	/*
	 * 
	 * :book_auditbatchinfo => { :from => 'book.bks_auditbatchinfo', :to =>
	 * 'common.con_auditbatchinfo', :key => ['AUDITBATCHID'],
	 * 
	 * 根据bookid来同步，多处会用到
	 */
	public static DataMapBuilder mapBookAuditBatchInfoBybookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("bks_auditbatchinfo").primaryKey("auditbatchid").cond("bookid", bookID);
		dataMap.to().tableName("con_auditbatchinfo").primaryKey("auditbatchid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * 产品信息表 产品记录现在由book端写入图书相关的产品信息，包括计算好价格，然后同步到common端 :productinfohead => {
	 * :from => 'book.bks_productinfohead', :to => 'common.con_productinfohead',
	 * :key => ['PRODUCTID'],
	 * 
	 * 根据bookid来同步，多处会用到
	 */
	public static DataMapBuilder mapBookProductinfoByBookid(String bookID, OneToOneJobBuilder job, String fromTableName) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		if (fromTableName == null) {
			fromTableName = "con_productinfohead";
		}
		dataMap.from().tableName(fromTableName).primaryKey("productid").cond("contentid", bookID);
		dataMap.to().tableName("con_productinfohead").primaryKey("productid").cond("contentid", bookID);

		return dataMap;
	}

	public static DataMapBuilder mapBookSeriesByBookid(String bookID, OneToOneJobBuilder job, String fromTable) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		if (fromTable == null) {
			fromTable = "con_seriesandbook";
		}
		dataMap.from().tableName(fromTable).primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName("con_seriesandbook").primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}

	/*
	 * begin------------------------图书分类管理和图书系列管理
	 * common->bks-------------------------------
	 */

	/*
	 * 分类信息同步
	 */
	public static DataMapBuilder mapClassByclassCode(String classcode, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();
		String tableName = "sup_classmanage";

		dataMap.from().tableName(tableName).primaryKey("classcode").cond("classcode", classcode);
		dataMap.to().tableName(tableName).primaryKey("classcode").cond("classcode", classcode);

		return dataMap;
	}

	/*
	 * 系列表 这边是从common往book同步，以此为准？
	 * 
	 */
	public static DataMapBuilder mapSeriesInfoBySeriesID(String seriesID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("con_content_series").primaryKey("seriesid").cond("seriesid", seriesID);
		dataMap.to().tableName("bks_content_series").primaryKey("seriesid").cond("seriesid", seriesID);

		return dataMap;
	}

	/*
	 * 图书系列关联表2 这边是从common往book同步，考虑后续单向，以此为准？
	 * 
	 */
//	public static DataMapBuilder mapBookSeriesBySeriesID(String seriesID, OneToOneJobBuilder job) {
//		DataMapBuilder dataMap = job.createDataMapBuilder();
//
//		dataMap.from().tableName("con_seriesandbook").primaryKey("bookid").cond("seriesid", seriesID);
//		dataMap.to().tableName("bks_seriesandbook").primaryKey("bookid").cond("seriesid", seriesID);
//
//		return dataMap;
//	}

	public static DataMapBuilder mapBookSeriesByBookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("con_seriesandbook").primaryKey("bookid").cond("bookid", bookID);
		dataMap.to().tableName("bks_seriesandbook").primaryKey("bookid").cond("bookid", bookID);

		return dataMap;
	}
	
	public static Action[] mapMebFile(String bookID, OneToOneJobBuilder job) {
		return mapFile(bookID, job, "t_cmp_ref_ebookmebfile","t_cmp_type_mebfile");
	}
	
	public static Action[] mapCoverFile(String bookID, OneToOneJobBuilder job) {
		return mapFile(bookID, job, "t_cmp_ref_ebookcoverfile","t_cmp_type_coverfile");
	}
	
	public static Action[] mapCopyrightFile(String bookID, OneToOneJobBuilder job) {
		return mapFile(bookID, job, "t_cmp_ref_ebookcopyrightfile","t_cmp_type_copyrightfile");
	}
	
	public static Action[] mapEbookEntityFile(String bookID, OneToOneJobBuilder job) {
		return mapFile(bookID, job, "t_cmp_ref_ebookentityfile","t_cmp_type_entityfile");
	}
	
	public static Action[] mapChapterEntityFile(String bookID, OneToOneJobBuilder job) {
		return mapFile(bookID, job, "t_cmp_ref_chapterentityfile","t_cmp_type_entityfile");
	}
	
	private static Action[] mapFile(String bookID, OneToOneJobBuilder job, String refTable, String xxxFileTable) {
		FileRelatedDataMapper fileMapper = new FileRelatedDataMapper(bookID, job);
		fileMapper.setTypeRefXXXFileTableName(refTable);
		fileMapper.setTypeXXXFileTableName(xxxFileTable);

		return fileMapper.generateActions();
	}

	public static class FileRelatedDataMapper {
		
		private String bookID;
		private OneToOneJobBuilder job;
		
		private String typeFileTableName = "t_cmp_type_file";
		private String typeXXXFileTableName;
		private String typeRefXXXFileTableName;
		
		public FileRelatedDataMapper(String bookID, OneToOneJobBuilder job) {
			this.bookID = bookID;
			this.job = job;
		}
		
		public void setTypeXXXFileTableName(String tableName) {
			this.typeXXXFileTableName = tableName;
		}
		
		public void setTypeRefXXXFileTableName(String tableName) {
			this.typeRefXXXFileTableName = tableName;
		}

		private String generateRelatedTableQueryString(String tableName) {
			String fileterSql = String.format("select target_id from %s where source_id='%s'", 
					this.typeRefXXXFileTableName, this.bookID);
			return String.format("select * from %s where objectid in (%s)", tableName, fileterSql);
		}

		private DataMapBuilder mapTypeFile() {
			DataMapBuilder dataMap = job.createDataMapBuilder();

			String sql = generateRelatedTableQueryString(this.typeFileTableName);
			dataMap.from().sql(sql).primaryKey("objectid");
			dataMap.to().sql(sql).tableName(this.typeFileTableName).primaryKey("objectid");

			return dataMap;
		}
		
		private DataMapBuilder mapTypeXXXFile() {
			DataMapBuilder dataMap = job.createDataMapBuilder();

			String sql = generateRelatedTableQueryString(this.typeXXXFileTableName);
			dataMap.from().sql(sql).primaryKey("objectid");
			dataMap.to().sql(sql).tableName(this.typeXXXFileTableName).primaryKey("objectid");

			return dataMap;
		}
		
		private DataMapBuilder mapTypeRefXXXFile() {
			DataMapBuilder dataMap = job.createDataMapBuilder();

			dataMap.from().tableName(this.typeRefXXXFileTableName).primaryKey("source_id", "target_id").cond("source_id", bookID);
			dataMap.to().tableName(this.typeRefXXXFileTableName).primaryKey("source_id", "target_id").cond("source_id", bookID);

			return dataMap;
		}
		
		/**
		 * 返回4个Action；
		 * @return
		 */
		public Action[] generateActions() {
			ArrayList<Action> actionList = new ArrayList<Action>(4);
			
			FileSpecialFilter filter = new FileSpecialFilter();
			
			Action typeFileAction = this.mapTypeFile().diffSyncAction(filter);
			Action typeXXXFileAction = this.mapTypeXXXFile().diffSyncAction(filter);
			Action typeRefXXXFileAction =this.mapTypeRefXXXFile().diffSyncAction();
			
			actionList.add(typeFileAction);
			actionList.add(typeXXXFileAction);
			actionList.add(typeRefXXXFileAction);
			actionList.add(new RowActionList(filter.getDeleteActionList()));
			
			return actionList.toArray(new Action[actionList.size()]);
		}
		
		private class FileSpecialFilter implements ActionFilter {

			private List<RowActioin> deleteActionList;
			
			public FileSpecialFilter() {
				this.deleteActionList = new ArrayList<>();
			}
			
			public List<RowActioin> getDeleteActionList() {
				return this.deleteActionList;
			}
			
			@Override
			public RowActioin filter(RowActioin action) throws InterruptedException, ExecutionException, SQLException {
				if (action instanceof RowDeleteAction) {
					// 忽略delete操作，放到ref后面去做
					this.deleteActionList.add(action);
					return null;
				} else if (action instanceof RowInsertAction) {
					try {
						// Insert失败暂时忽略，可能是前面原因引起的，暂仅记录；
						int num = action.execute().get();
						logger.debug("exec sql return {}", num);
						return action;
					} catch (Exception e) {
						logger.error("abnormal insert failed. ignore it now. FIXIT", e);
						return action;
					}
				} else {
					// 操作失败会导致任务失败
					int num = action.execute().get();
					logger.debug("exec sql return {}", num);
					return action;
				}
			}
		}

	}
	
	
	// 以下是漫画新增的映射项
	
	public static DataMapBuilder mapCartoonThumenailByBookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_cartoon_thumbnail";
		dataMap.from().tableName(tableName).primaryKey("bookid", "chapterid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("bookid", "chapterid").cond("bookid", bookID);

		return dataMap;
	}
	
	public static DataMapBuilder mapCartoonThumenailByChapterID(String chapterID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_cartoon_thumbnail";
		dataMap.from().tableName(tableName).primaryKey("bookid", "chapterid").cond("chapterid", chapterID);
		dataMap.to().tableName(tableName).primaryKey("bookid", "chapterid").cond("chapterid", chapterID);

		return dataMap;
	}
	
	public static DataMapBuilder mapCaricaturePriceByBookID(String bookID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_caricatureprice";
		dataMap.from().tableName(tableName).primaryKey("bookid", "chapterid").cond("bookid", bookID);
		dataMap.to().tableName(tableName).primaryKey("bookid", "chapterid").cond("bookid", bookID);

		return dataMap;
	}
	
	public static DataMapBuilder mapCaricaturePriceByChapterID(String chapterID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		String tableName = "con_caricatureprice";
		dataMap.from().tableName(tableName).primaryKey("bookid", "chapterid").cond("chapterid", chapterID);
		dataMap.to().tableName(tableName).primaryKey("bookid", "chapterid").cond("chapterid", chapterID);

		return dataMap;
	}
}
