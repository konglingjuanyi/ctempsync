package com.cmread.cmu.css.service.sync.operator;

import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class OperatorDiffSync extends BaseOperatorTaskHandler {

	@Override
	public void mapCommonToBookAction(String operatorID, OneToOneJobBuilder subJob, SyncTask task) {
		Action userRoleAction = mapUserRole(operatorID, subJob).diffSyncAction();
		Action operatorAction = mapOperator(operatorID, subJob, task).diffSyncAction();
		//Action operatorExtInfoAction = mapOperatorExtInfo(subJob, task).diffSyncAction();
		
		subJob.next(userRoleAction).next(operatorAction);//.next(operatorExtInfoAction);
	}

	@Override
	public void mapCommonToCartoonAction(String operatorID, OneToOneJobBuilder subJob, SyncTask task) {
		Action cartoonOperatorAction = mapCartoonOperator(operatorID, subJob, task).diffSyncAction();

		subJob.next(cartoonOperatorAction);
	}

}
