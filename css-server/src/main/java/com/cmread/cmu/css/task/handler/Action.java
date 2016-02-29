package com.cmread.cmu.css.task.handler;

import com.cmread.cmu.css.db.utils.ActionFilter;
import com.cmread.cmu.css.db.utils.RowActioin;

public interface Action {

	public enum Type {
		Insert, Update, Delete, DiffSync, ActionList;
		
		public static Type toType(String value) {
			switch (value) {
			case "insert":
				return Insert;
			case "update":
				return Update;
			case "delete":
				return Delete;
			case "diff-sync" :
				return DiffSync;
			case "action-list" :
				return ActionList;
			}
			return null;
		}
	}
	
	DataMap getDataMap();
	
	Type getType();

	ActionFilter getActionFilter();
	
	RowActioin[] getSubActions();
}
