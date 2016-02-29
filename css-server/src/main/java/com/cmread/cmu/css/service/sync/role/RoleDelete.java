package com.cmread.cmu.css.service.sync.role;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * 角色删除同步。角色删除中除了删除角色表，还要删除操作员角色映射关系表
 * 
 * 方向：[common] -> [book]
 * 表：
 *   common.t_mbe_role[ROLEID] -> book.t_mbe_role[ROLEID] 
 *   common.t_bme_user_role -> book.t_bme_user_role
 *  
 * @author zhangtieying
 *
 */
public class RoleDelete extends BaseRoleTaskHandler {

	@Override
	public void mapCommonToBookAction(String roleID, OneToOneJobBuilder subJob) {
		Action userRoleAction = mapUserRoleData(roleID, subJob).diffSyncAction();
		Action roleAction = mapRoleData(roleID, subJob).deleteAction();
		
		subJob.next(userRoleAction).next(roleAction);		
	}

}
