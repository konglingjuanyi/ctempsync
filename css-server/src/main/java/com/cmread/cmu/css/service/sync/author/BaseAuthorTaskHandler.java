package com.cmread.cmu.css.service.sync.author;

import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.DiffSyncFilter;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseAuthorTaskHandler implements TaskHandler {

	private static Logger logger = LoggerFactory.getLogger(BaseAuthorTaskHandler.class);
	
	protected String getAuthorID(SyncTask task) {
		return task.getContent().getMap().get("authorid");
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		return "author-" + getAuthorID(task);
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 实现作家同步，从这里开始 :)
		String authorID = getAuthorID(task);
		String fromDB = "common";

		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 一对多同步必须拆分为多个job，需要逐一定义

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB(fromDB);
		commonToBookJob.toDB("book");
		mapCommonToBookAction(authorID, commonToBookJob, task);

		tb.addSubJob(commonToBookJob);
		
		// common -> cartoon
		OneToOneJobBuilder commonToCartoonJob = tb.createOneToOneJob();

		commonToCartoonJob.fromDB(fromDB);
		commonToCartoonJob.toDB("cartoon");
		mapCommonToCartoonAction(authorID, commonToCartoonJob, task);

		tb.addSubJob(commonToCartoonJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		TaskResult result = autoMapHandler.exec();
		if (result == TaskResult.SUCCESS) {
			String penName = task.getContent().getMap().get("penname");
			if (penName != null) {
				// 在这里更新图书表中相关信息
				try {
					updateBookAuthorPenInfo(authorID, penName, task.getSqlExecutor("book"));
					return result;
				} catch (InterruptedException | ExecutionException e) {
					logger.error("update book penname field when author'penname changed failed.", e);
					return TaskResult.FAILED;
				}
			}
		}
		return result;
	}

	private void updateBookAuthorPenInfo(String authorID, String penName, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String updateAuditBookInfo = "update con_auditbookinfo set penname=?, lastmodifyTime=sysdate where authorid=?";
		String updateEBook = "update t_cmp_type_ebook set authorname=? where authorid=?";
		String updateAuditBookInfoComp = "update bks_auditbookinfocomp set PENNAME = ? where authorid=?";
		
		//2016-01-14 bks库中没有这个表，经查，bks中暂没有使用这个表，因此将此同步部分注释掉；
		//String updateAubitBookInfoBack = "UPDATE con_auditbookinfoback SET PENNAME = ? where authorid=?";
		//sqlExec.executeUpdate(updateAubitBookInfoBack, penName, authorID).get();
		
		sqlExec.executeUpdate(updateAuditBookInfo, penName, authorID).get();
		sqlExec.executeUpdate(updateEBook, penName, authorID).get();
		sqlExec.executeUpdate(updateAuditBookInfoComp, penName, authorID).get();
	}

	public abstract void mapCommonToBookAction(String authorID, OneToOneJobBuilder subJob, SyncTask task);
	public abstract void mapCommonToCartoonAction(String authorID, OneToOneJobBuilder subJob, SyncTask task);

	// 映射规则：作家和mcp映射关系表
	public DataMapBuilder mapAuthorMcpData(String authorID, OneToOneJobBuilder subJob, String destTableName) {
		DataMapBuilder authorMcpMap = subJob.createDataMapBuilder();

		if (destTableName == null) {
			destTableName = "mcp_authorandmcp";
		}
		authorMcpMap.from().tableName("mcp_authorandmcp").primaryKey("auid", "mcpid").cond("auid", authorID);
		authorMcpMap.to().tableName(destTableName).primaryKey("auid", "mcpid").cond("auid", authorID);

		return authorMcpMap;
	}

	// 映射规则：作家类别表
	public DataMapBuilder mapAuthorClassData(String authorID, OneToOneJobBuilder subJob, String destTableName) {
		DataMapBuilder authorClassMap = subJob.createDataMapBuilder();

		if (destTableName == null) {
			destTableName = "mcp_auclassinfo";
		}
		authorClassMap.from().tableName("mcp_auclassinfo").primaryKey("auid").cond("auid", authorID);
		authorClassMap.to().tableName(destTableName).primaryKey("auid").cond("auid", authorID);

		return authorClassMap;
	}

	// 映射规则：作家表
	public DataMapBuilder mapAuthorData(String authorID, OneToOneJobBuilder subJob, final SyncTask task) {
		DataMapBuilder authorMap = subJob.createDataMapBuilder();

		String authorTableName = "mcp_authorinformation";
		authorMap.from().tableName(authorTableName).primaryKey("auid").cond("auid", authorID);
		authorMap.to().tableName(authorTableName).primaryKey("auid").cond("auid", authorID);
		
		authorMap.addDiffSyncFilter(new DiffSyncFilter() {
			@Override
			public void sameRowFilter(Row from, Row to) {
				String penField = "AUPENNAME";
				String fromPenName = from.getFieldValue(penField).toString();
				String toPenName = to.getFieldValue(penField).toString();
				if (!StringUtils.equals(fromPenName, toPenName)) {
					//笔名修改，需要触发几个动作,这里记录一下penName;
					task.getContent().getMap().put("penname", fromPenName);
				}
			}
		});

		return authorMap;
	}

}
