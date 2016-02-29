package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class ClearCSyncTaskSuccessRecord extends BaseTaskTrigger {

	private static Logger logger = LoggerFactory.getLogger(ClearCSyncTaskSuccessRecord.class);
	
	private int reverseDays;
	
	public void setReserveDays(int days) {
		this.reverseDays = days;
	}
	
	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor common = registry.getExecutor("common", null);
		
		try {
			int num = common.executeUpdate("delete from csync_task_success where createtime < sysdate - ?", this.reverseDays).get();
			logger.info("schedule:clean csync_task_success table. return {}", num);
		} catch (Exception e) {
			logger.error("clear csync table success record failed.", e);
		} 
	}
}
