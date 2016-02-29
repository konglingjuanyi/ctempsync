package com.cmread.cmu.css.task.handler;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import com.cmread.cmu.css.db.utils.RowSetDiff;
import com.cmread.cmu.css.db.utils.RowSetDiffExecutor;
import com.cmread.cmu.css.db.utils.RowUpdateAction;
import com.cmread.cmu.css.db.utils.SqlBuilder;
import com.cmread.cmu.css.db.utils.SqlBuilder.SelectSqlBuilder;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.DataMap.TableData;

public class AutoMapTaskHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AutoMapTaskHandler.class);
	
	private SyncTask task;
	private List<OneToOneJob> jobs;
	
	public void setSyncTask(SyncTask task) {
		this.task = task;
	}
	
	public void setJobs(List<OneToOneJob> jobs) {
		this.jobs = jobs;
	}
	
	// 在这里实际执行每个job
	public TaskResult exec() {
		try {
			for (OneToOneJob job : this.jobs) {
				execJob(job);
			}
			return TaskResult.SUCCESS;
		} catch (SQLException | InterruptedException | ExecutionException e) {
			logger.error("task exec failed.", e);
			return TaskResult.FAILED;
		}
	}
	
	private void execJob(OneToOneJob job) throws SQLException, InterruptedException, ExecutionException {
		SqlExecutor from = getFromSqlExecutor(job);
		SqlExecutor to = getToSqlExecutor(job);
		
		List<ExecSequence> execSteps = job.getExecSequence();
		
		for (ExecSequence execStep : execSteps) {
			
			// 同一step下的action应该是可以并发执行的，这里先顺序执行；
			List<Action> actions = execStep.getActions();
			 
			for (Action action : actions) {
				switch (action.getType()) {
				case Delete:
					doDeleteAction(action.getDataMap(), from, to);
					break;
				case Insert:
					doInsertAction(action.getDataMap(), from, to);
					break;
				case Update:
					doUpdateAction(action.getDataMap(), from, to);
					break;
				case DiffSync:
					doDiffSyncAction(action.getDataMap(), from, to, action.getActionFilter());
					break;
				case ActionList:
					doActionList(action);
				}
			}
		}
	}

	private void doActionList(Action action) throws InterruptedException, ExecutionException, SQLException {
		RowActioin[] rowActions = action.getSubActions();
		for (RowActioin rowAction : rowActions) {
			int num = rowAction.execute().get();
			logger.debug("exec sql return {}", num);
		}
	}

	private void doDiffSyncAction(DataMap dataMap, SqlExecutor from, SqlExecutor to, ActionFilter filter) throws SQLException, InterruptedException, ExecutionException {
		//TODO 这里可以改为并发的
		RowSet fromDataSet = queryFromDataSet(dataMap, from);
		RowSet toDataSet = queryToDataSet(dataMap, to);
		
		List<DiffSyncFilter> dsFilters = dataMap.getDiffSyncFilters();
		RowSetDiff diff = RowSetDiff.diff(fromDataSet, toDataSet, dataMap.getFromData().getPrimaryKey(), dsFilters);
		
		RowSetDiffExecutor diffExecutor = new RowSetDiffExecutor(to);
		diffExecutor.execute(diff, dataMap.getToData().getTableName(), filter);
	}

	private void doUpdateAction(DataMap dm, SqlExecutor from, SqlExecutor to)
			throws SQLException, InterruptedException, ExecutionException {
		RowSet ds = queryFromDataSet(dm, from);
		
		for (Row item : ds.getRows()) {
			RowUpdateAction action = new RowUpdateAction(item, dm.getToData().getTableName(), to, dm.getToData().getPrimaryKey());
			
			int num = action.execute().get();
			logger.debug("exec update return {}", num);
		}
	}

	private void doInsertAction(DataMap dm, SqlExecutor from, SqlExecutor to)
			throws SQLException, InterruptedException, ExecutionException {
		// 构建执行sql，并执行？
		//取出原始数据，保存在一个离线的DataSet中
		RowSet ds = queryFromDataSet(dm, from);

		//针对这个dataset，转换为to中的insert语句；
		for (Row item : ds.getRows()) {
			Future<Integer> result = new RowInsertAction(item, dm.getToData().getTableName(), to).execute();
			
			int num = result.get();
			logger.debug("exec insert return {}", num);
		}
	}

	private RowSet queryFromDataSet(DataMap dm, SqlExecutor from)
			throws SQLException, InterruptedException, ExecutionException {
		return queryDataSet(dm.getFromData(), from);
	}

	private RowSet queryToDataSet(DataMap dataMap, SqlExecutor to) throws InterruptedException, ExecutionException {
		return queryDataSet(dataMap.getToData(), to);
	}

	private RowSet queryDataSet(TableData tableData, SqlExecutor sqlExec) throws InterruptedException, ExecutionException  {
		SelectSqlBuilder select = SqlBuilder.select();
		
		RowSet ds = null;
		if (tableData.getSql() != null) {
			//按照sql来查询，在给出的基本sql串的基础上，增加条件
			// 直接使用这个sql
			//String sql = StringELUtil.replaceWithMap(tableData.getSql(), this.task.getContent().getMap());
			Future<RowSet> result = sqlExec.queryForDataSet(tableData.getSql());
			ds = result.get();
		} else if (tableData.getTableName() != null) {
			select.table(tableData.getTableName());
			
			String[] condNames = tableData.getCondNames();
			String[] condValues = tableData.getCondValues();
			
			for (int i=0; i< condNames.length ; ++i) {
				select.addCondition(condNames[i], condValues[i]);
			}
			
			select.orderBy(tableData.getOrderBy());
			
			Future<RowSet> result = sqlExec.queryForDataSet(select.getSqlString(), select.getParameters());
			ds = result.get();
		} 
		
		for (DataMap.Filter filter : tableData.getFilters()) {
			filter.filter(ds);
		}
	
		return ds;
	}

	private void doDeleteAction(DataMap dm, SqlExecutor from, SqlExecutor to)
			throws SQLException, InterruptedException, ExecutionException {
		TableData toTable = dm.getToData();

		RowDeleteAction action = new RowDeleteAction(toTable.getCondNames(), toTable.getCondValues(),
				toTable.getTableName(), to);

		int num = action.execute().get();
		logger.debug("exec delete return {}", num);
	}
	
	private SqlExecutor getFromSqlExecutor(OneToOneJob job) {
		String fromDB = job.getFromDB();
		return this.task.getSqlExectorManager().getExecutor(fromDB, task);
	}

	private SqlExecutor getToSqlExecutor(OneToOneJob job) {
		String toDB = job.getToDB();
		return this.task.getSqlExectorManager().getExecutor(toDB, task);
	}


}
