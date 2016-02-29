package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class RoleDiffSync extends BaseRoleTaskHandler {

	@Override
	public void mapCommonToBookAction(String roleID, OneToOneJobBuilder subJob) {
		Action userRoleAction = mapUserRoleData(roleID, subJob).diffSyncAction();
		Action roleAction = mapRoleData(roleID, subJob).diffSyncAction();
		
		subJob.next(userRoleAction).next(roleAction);
	}

}
