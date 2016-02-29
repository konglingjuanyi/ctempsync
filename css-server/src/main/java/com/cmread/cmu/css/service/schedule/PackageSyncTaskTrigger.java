package com.cmread.cmu.css.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class PackageSyncTaskTrigger extends BaseTaskTrigger {
	
	private static Logger logger = LoggerFactory.getLogger(PackageSyncTaskTrigger.class);

	public void check() {
		if (!notSkip) {
			return;
		}
		
		SqlExecutor book = registry.getExecutor("book", null);
		
		try {
			// 仅找出状态为03（打包成功）的触发打包同步
			RowSet pkItems = book.queryForDataSet("select * from con_package_queue where pkstatus='03' and csync is null").get();
			for (Row row : pkItems.getRows()) {
				String pkID = row.getFieldValue("PKID").toString();
				//把csync标志位置为1，标示已经被css扫描过并被同步，后续同步成功之后整条记录在bks会被删除，失败之后不会被重复扫描				
				book.executeUpdate("update con_package_queue set csync ='1' where pkid='" + pkID
						+ "'").get();
				clientHelper.bookPackageComplete(pkID);
			}
		} catch (Exception e) {
			logger.error("package queue check failed.", e);
		} 
	}
}
