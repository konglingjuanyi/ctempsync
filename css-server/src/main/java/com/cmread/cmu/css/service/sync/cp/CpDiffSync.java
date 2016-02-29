package com.cmread.cmu.css.service.sync.cp;

import com.cmread.cmu.css.service.sync.operator.BaseOperatorTaskHandler;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class CpDiffSync extends BaseCpTaskHandler {

	@Override
	public void mapCommonToBookAction(String cpID, OneToOneJobBuilder subJob, SyncTask task) {
		Action userRoleActioin = BaseOperatorTaskHandler.mapUserRole(cpID, subJob).diffSyncAction();
		Action operatorAction = BaseOperatorTaskHandler.mapOperator(cpID, subJob, task).diffSyncAction();
		Action cpExtInfoAction = this.mapCpInfoExt(cpID, subJob).diffSyncAction();
		Action cpInfoAction = this.mapCpInfo(cpID, subJob).diffSyncAction();

		subJob.next(userRoleActioin, cpExtInfoAction).next(operatorAction).next(cpInfoAction);
	}

	@Override
	public void mapCommonToCartoonAction(String cpID, OneToOneJobBuilder subJob, SyncTask task) {
		Action operatorAction = BaseOperatorTaskHandler.mapOperator(cpID, subJob, task).diffSyncAction();
		Action cpExtInfoAction = this.mapCpInfoExt(cpID, subJob).diffSyncAction();
		Action cpInfoAction = this.mapCpInfo(cpID, subJob).diffSyncAction();

		subJob.next(cpExtInfoAction).next(operatorAction).next(cpInfoAction);
	}

}
