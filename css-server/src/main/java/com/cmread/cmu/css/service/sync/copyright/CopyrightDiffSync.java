package com.cmread.cmu.css.service.sync.copyright;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public class CopyrightDiffSync extends BaseCopyrightTaskHandler {

	@Override
	public void mapBookToCommonAction(String copyrightID, OneToOneJobBuilder subJob) {
		Action copyrightInfoAction = mapCopyrightInfo(copyrightID, subJob).diffSyncAction();
		
		subJob.next(copyrightInfoAction);
	}

}
