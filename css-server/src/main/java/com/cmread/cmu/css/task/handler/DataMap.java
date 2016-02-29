package com.cmread.cmu.css.task.handler;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.utils.DiffSyncFilter;

public interface DataMap {

	TableData getFromData();
	TableData getToData();
	
	List<DiffSyncFilter> getDiffSyncFilters();
	
	public static interface TableData {
		// 二选一
		String getTableName();
		String getSql();  
		
		String[] getPrimaryKey();
		String[] getOrderBy();
		
		String[] getCondNames();
		String[] getCondValues();
		
		/*
		 * 用于对获取的datamap进行过滤
		 */
		List<Filter> getFilters();
	}
	
	public static interface Filter {
		void filter(RowSet rowSet) throws InterruptedException, ExecutionException ;
	}

}
