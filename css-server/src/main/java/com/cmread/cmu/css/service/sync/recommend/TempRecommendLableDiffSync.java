package com.cmread.cmu.css.service.sync.recommend;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.TaskHandler;
import com.cmread.cmu.css.task.TaskResult;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.AutoMapTaskHandler;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;
import com.cmread.cmu.css.task.handler.builder.TaskHandlerBuilder;

/*
 * 推荐标签类同步（共存期）
 * 
 * 说明：推荐标签类同步是共存期暂时使用的同步任务，所以为简单，将几个类型的同步任务合并在一个里面实现；通过类型字段区分；
 */
public class TempRecommendLableDiffSync implements TaskHandler {

	@Override
	public String getRelatedKey(SyncTask task) {
		return "recommend-label-diff-sync";
	}

	@Override
	public TaskResult exec(SyncTask task) {
		try {
			String groupName = task.getContent().getMap().get("clabel_group_name");
			String subGroupName = task.getContent().getMap().get("clabel_subgroup_name");

			SqlExecutor sqlExec = task.getSqlExecutor("common");
			if (groupName != null) {
				// 如果同步触发是通用标签组的groupName，则找出对应的groupID再触发同步任务
				String sql = "select groupid from CON_RECOMMEND_CLABEL_GROUP where groupname=?";
				RowSet rs = sqlExec.queryForDataSet(sql, new Object[] { groupName }).get();
				for (Row row : rs.getRows()) {
					Object id = row.getFieldValue("groupid");

					if (id != null) {
						task.getContent().getMap().put("clabel_group_id", id.toString());
						TaskResult tr = execInternal(task);
						if (tr != TaskResult.SUCCESS) {
							return tr;
						}
					}
				}
				return TaskResult.SUCCESS;
			} else if (subGroupName != null) {
				String sql = "select groupid from CON_RECOMMEND_CLABEL_SUBGROUP where groupname=?";
				RowSet rs = sqlExec.queryForDataSet(sql, new Object[] { subGroupName }).get();
				for (Row row : rs.getRows()) {
					Object id = row.getFieldValue("groupid");

					if (id != null) {
						task.getContent().getMap().put("clabel_subgroup_id", id.toString());
						TaskResult tr = execInternal(task);
						if (tr != TaskResult.SUCCESS) {
							return tr;
						}
					}
				}
				return TaskResult.SUCCESS;
			} else {
				return execInternal(task);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TaskResult execInternal(SyncTask task) {
		String type = task.getContent().getMap().get("type");

		TaskHandlerBuilder tb = new TaskHandlerBuilder();

		OneToOneJobBuilder commonToBookJob = tb.createOneToOneJob();
		commonToBookJob.fromDB("common");
		commonToBookJob.toDB("book");

		switch (type) {
		case "clabel-group-diff-sync":
			doCLabelGroupDiffSync(task, commonToBookJob);
			break;
		case "clabel-diff-sync":
			doCLabelDiffSync(task, commonToBookJob);
			break;
		case "clabel-subgroup-diff-sync":
			doCLabelSubGroupDiffSync(task, commonToBookJob);
			break;
		case "clabel-sub-diff-sync":
			doCLabelSubDiffSync(task, commonToBookJob);
			break;
		}

		tb.addSubJob(commonToBookJob);

		AutoMapTaskHandler autoMapHandler = new AutoMapTaskHandler();
		autoMapHandler.setJobs(tb.getSubJobs());
		autoMapHandler.setSyncTask(task);

		return autoMapHandler.exec();
	}

	private void doCLabelGroupDiffSync(SyncTask task, OneToOneJobBuilder commonToBookJob) {
		String groupID = task.getContent().getMap().get("clabel_group_id");
		Action labelAction = BaseDMHelper.mapRecommendLabelByGroupID(groupID, commonToBookJob).diffSyncAction();
		Action labelGroupAction = BaseDMHelper.mapRecommendLabelGroup(groupID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(labelAction).next(labelGroupAction);
	}
	
	private void doCLabelDiffSync(SyncTask task, OneToOneJobBuilder commonToBookJob) {
		String clabelID = task.getContent().getMap().get("clabel_id");
		Action labelAction = BaseDMHelper.mapRecommendLabelByLableID(clabelID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(labelAction);
	}
	
	private void doCLabelSubGroupDiffSync(SyncTask task, OneToOneJobBuilder commonToBookJob) {
		String groupID = task.getContent().getMap().get("clabel_subgroup_id");
		Action labelAction = BaseDMHelper.mapRecommendLabelSubBySubGroupID(groupID, commonToBookJob).diffSyncAction();
		Action labelGroupAction = BaseDMHelper.mapRecommendLabelSubGroup(groupID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(labelAction).next(labelGroupAction);
	}
	
	private void doCLabelSubDiffSync(SyncTask task, OneToOneJobBuilder commonToBookJob) {
		String clabelID = task.getContent().getMap().get("clabel_sub_id");
		Action labelAction = BaseDMHelper.mapRecommendLabelSubByLabelID(clabelID, commonToBookJob).diffSyncAction();
		commonToBookJob.next(labelAction);
	}

	/*
	 * Action authorMcpMapAction = mapAuthorMcpData(authorID,
	 * subJob).diffSyncAction(); Action authorClassMapAction =
	 * mapAuthorClassData(authorID, subJob).diffSyncAction(); Action
	 * authorMapAction = mapAuthorData(authorID, subJob, task).diffSyncAction();
	 * //Action authorAccountMapAction = mapAuthorAccountData(authorID,
	 * subJob).diffSyncAction();
	 * 
	 * // 设置多个数据类型的同步执行顺序； subJob.next(authorMcpMapAction,
	 * authorClassMapAction).next(authorMapAction);
	 */

}
