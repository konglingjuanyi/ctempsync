package com.cmread.cmu.css.service.sync.book;

import com.cmread.cmu.css.task.SyncTask;

public abstract class BaseUtils {
	
	public static String[] getBookIDs(SyncTask task) {
		String bookIDsStr = task.getContent().getMap().get("bookids");
		if (bookIDsStr != null) {
			return bookIDsStr.split(",");
		}
		return new String[0];
	}
	
}
