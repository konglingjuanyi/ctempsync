package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 角色内容更新同步
 * 
 * 方向：[common] -> [book]
 * 表：
 *   common.t_mbe_role[ROLEID] -> book.t_mbe_role[ROLEID] 
 *  
 * @author zhangtieying
 *
 */
public class RoleUpdate extends BaseRoleTaskHandler {

	@Override
	public void mapCommonToBookAction(String roleID, OneToOneJobBuilder subJob) {
		Action roleAction = mapRoleData(roleID, subJob).diffSyncAction();
		
		subJob.next(roleAction);
	}

}
