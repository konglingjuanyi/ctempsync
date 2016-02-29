package com.cmread.cmu.css.task.handler.builder;

import java.util.LinkedList;
import java.util.List;

import com.cmread.cmu.css.task.handler.OneToOneJob;

public class TaskHandlerBuilder {
	
	private List<OneToOneJob> subJobs;
	
	public TaskHandlerBuilder() {
		this.subJobs = new LinkedList<>();
	}

	public OneToOneJobBuilder createOneToOneJob() {
		return new OneToOneJobBuilder();
	}

	public TaskHandlerBuilder addSubJob(OneToOneJob subJob) {
		this.subJobs.add(subJob);
		return this;
	}

	public List<OneToOneJob> getSubJobs() {
		return this.subJobs;
	}
	
//	public void updateTaskConfig(TaskConfig config) {
//		config.setJobs(subJobs.toArray(new OneToOneJob[subJobs.size()]));
//	}
}
