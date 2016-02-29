package com.cmread.cmu.css.service.sync.operator;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.DataMap.Filter;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.DataMapBuilder.Condation;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

public abstract class BaseOperatorTaskHandler implements TaskHandler {

	protected String getOperatorID(SyncTask task) {
		return task.getContent().getMap().get("operatorid");
	}

	@Override
	public String getRelatedKey(SyncTask task) {
		return "operator-" + getOperatorID(task);
	}

	@Override
	public TaskResult exec(SyncTask task) {
		// 实现作家同步，从这里开始 :)
		String operatorID = getOperatorID(task);
		String fromDB = "common";

		// 创建通用任务模板
		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		// 创建到图书库的一对一同步Job
		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();

		commonToBookJob.fromDB(fromDB);
		commonToBookJob.toDB("book");
		mapCommonToBookAction(operatorID, commonToBookJob, task);
		tb.addSubJob(commonToBookJob);
		
		// 创建到漫画的同步Job
		OneToOneJobBuilder commonToCartoonJob = tb.createOneToOneJob();

		commonToCartoonJob.fromDB(fromDB);
		commonToCartoonJob.toDB("cartoon");
		mapCommonToCartoonAction(operatorID, commonToCartoonJob, task);
		tb.addSubJob(commonToCartoonJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	public abstract void mapCommonToBookAction(String operatorID, OneToOneJobBuilder subJob, SyncTask task);
	public abstract void mapCommonToCartoonAction(String operatorID, OneToOneJobBuilder subJob, SyncTask task);

	/*
	 * 操作员表是由common的两个表合并而成为book的一个表
	 * 映射规则：
	 * 1. book的status字段固定为0;
	 * 2. 应该没有实际删除动作（common也不删除，仅修改标志位），对同步来说，相当于更新；
	 * 3. 按照映射规则，type字段的含义有区别，common中的type为2时等于book中的type0，所以需要做转换（按照string来转换） 
	 */
	public static DataMapBuilder mapOperator(String operatorID, final OneToOneJobBuilder subJob, final SyncTask task) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		//String sql = "select * from t_bme_operator t left join con_mcpadmin_info m on (t.operid=m.userid) where (t.operid=%s)";
		String sql = "select * from t_bme_operator t left join con_mcpadmin_info m on (t.operid=m.userid) left join sup_operater_extinfo s on t.name=s.opername  where (t.operid=%s)";
		dataMap.from().sql(String.format(sql, operatorID)).primaryKey("operid").addFilter(new Filter() {
			@Override
			public void filter(RowSet rowSet) {
				for (Row row : rowSet.getRows()) {
					row.setField("status", "0");
					
					Object type = row.getFieldValue("type");
					if (type != null) {
						if ("2".equals(type.toString())) {
							row.setField("type", "0");
						}
					}
					
					// 设置opername到task中？
					Object operName = row.getFieldValue("name");
					if (operName != null) {
						task.getContent().getMap().put("opername", operName.toString());
					}
				}
			}
		});
		dataMap.to().tableName("bks_operator").primaryKey("operid").cond("operid", operatorID);

		return dataMap;
	}
	
	// 映射规则：操作员扩展信息表
	// 备忘：
	// 操作员扩展信息表的主键是opername，不是operid！！！ （变态！！！）
	// 所以，这个map动作必须在operatorMap动作后面执行，否则拿不到opername！！！
	protected DataMapBuilder mapOperatorExtInfo(OneToOneJobBuilder subJob, final SyncTask task) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder(); 
		
		Condation cond = new Condation() {
			@Override
			public String getName() { return "opername"; }
			@Override
			public String getValue() {
				return task.getContent().getMap().get("opername");
			}
		};
		dataMap.from().tableName("sup_operater_extinfo").primaryKey("opername").cond(cond);
		dataMap.to().tableName("bks_sup_operater_extinfo").primaryKey("opername").cond(cond);

		return dataMap;
	}

	// 映射规则：操作员与角色映射关系表
	public static DataMapBuilder mapUserRole(String operatorID, OneToOneJobBuilder subJob) {
		DataMapBuilder userRoleMap = subJob.createDataMapBuilder();

		String roleTable = "t_bme_user_role";
		userRoleMap.from().tableName(roleTable).primaryKey("operid", "roleid").cond("operid", operatorID);
		userRoleMap.to().tableName(roleTable).primaryKey("operid", "roleid").cond("operid", operatorID);

		return userRoleMap;
	}
	
	/*
	 * 映射cms-ccs的操作员表；（两个表是一样的）
	 */
	public static DataMapBuilder mapCartoonOperator(String operatorID, final OneToOneJobBuilder subJob, final SyncTask task) {
		DataMapBuilder dataMap = subJob.createDataMapBuilder();

		String tableName = "t_bme_operator";
		dataMap.from().tableName(tableName).primaryKey("operid").cond("operid", operatorID);
		dataMap.to().tableName(tableName).primaryKey("operid").cond("operid", operatorID);

		return dataMap;
	}

}
