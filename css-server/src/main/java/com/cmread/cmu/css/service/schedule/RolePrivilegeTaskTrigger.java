package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 权限和角色权限映射关系表的定时同步（触发两个全表同步）；
 * 
 * @author zhangtieying
 *
 */
public class RolePrivilegeTaskTrigger extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(RolePrivilegeTaskTrigger.class);
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		try {
			clientHelper.privilegeTableSync();
			clientHelper.rolePrivilegeTableSync();
		} catch (Exception e) {
			logger.error("role-privilege and privilege table sync trigger failed.", e);
		} 
	}
}
