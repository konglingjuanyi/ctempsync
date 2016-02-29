package com.cmread.cmu.css.service.sync.usergroup;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class UserGroupDiffSync extends BaseUserGroupTaskHandler {

	@Override
	public void mapCommonToBookAction(String userGroupID, OneToOneJobBuilder subJob) {
		Action userPrivilegesAction = mapUserPrivileges(userGroupID, subJob).diffSyncAction();
		Action userGroupMemberAction = mapUserGroupMember(userGroupID, subJob).diffSyncAction();
		Action userGroupAction = mapUserGroup(userGroupID, subJob).diffSyncAction();
		
		subJob.next(userPrivilegesAction).next(userGroupMemberAction).next(userGroupAction);
	}

}
