/**
 * 
 */
package com.cmread.cmu.css.service.sync.publisher;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

/**
 * @author caidq
 *
 * 2015-12-9
 */
public class PublisherDiffSync extends BasePublisherTaskHandler {

	@Override
	public void mapBookToCommonAction(String publisherID,
			OneToOneJobBuilder subJob) {
		Action publisherAction = mapPublisher(publisherID, subJob).diffSyncAction();
		
		subJob.next(publisherAction);
		
	}

}
