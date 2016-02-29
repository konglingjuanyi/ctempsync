package com.cmread.cmu.css.service.schedule;

import com.cmread.cmu.css.db.asyncsql.SqlTaskExecutorServiceRegistry;
import com.cmread.cmu.css.http.client.CSSClientHelper;

public class BaseTaskTrigger {
	
	protected SqlTaskExecutorServiceRegistry registry;
	protected CSSClientHelper clientHelper;
	protected boolean notSkip = true;
	
	public void setSqlRegistry(SqlTaskExecutorServiceRegistry registry) {
		this.registry = registry;
	}
	
	public void setCssClientHelper(CSSClientHelper clientHelper) {
		this.clientHelper = clientHelper;
	}
	
	public void setNotSkip(boolean notSkip) {
		this.notSkip = notSkip;
	}
	
}
