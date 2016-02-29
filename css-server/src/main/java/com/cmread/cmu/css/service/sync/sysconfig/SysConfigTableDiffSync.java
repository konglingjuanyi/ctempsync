package com.cmread.cmu.css.service.sync.sysconfig;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class SysConfigTableDiffSync extends BaseSysConfigTaskHandler {

	@Override
	public void mapCommonToBookAction(String key, OneToOneJobBuilder subJob) {
		Action sysConfigAction = mapSysConfigTable(subJob).diffSyncAction();

		subJob.next(sysConfigAction);
	}

	@Override
	public void mapCommonToCartoonAction(String key, OneToOneJobBuilder subJob) {
		mapCommonToBookAction(key, subJob);
	}

}
