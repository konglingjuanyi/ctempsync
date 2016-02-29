package com.cmread.cmu.css.task.handler;

import java.util.List;

import com.cmread.cmu.css.db.utils.ActionFilter;
import com.cmread.cmu.css.db.utils.RowActioin;

public class RowActionList implements Action {

	private RowActioin[] actions;
	
	public RowActionList(List<RowActioin> actionList) {
		this.actions = actionList.toArray(new RowActioin[actionList.size()]);
	}
	
	@Override
	public DataMap getDataMap() {
		return null;
	}

	@Override
	public Type getType() {
		return Type.ActionList;
	}

	@Override
	public ActionFilter getActionFilter() {
		return null;
	}

	@Override
	public RowActioin[] getSubActions() {
		return this.actions;
	}

}
