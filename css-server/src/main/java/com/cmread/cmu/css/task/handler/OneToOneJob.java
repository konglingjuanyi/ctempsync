package com.cmread.cmu.css.task.handler;

import java.util.List;

public interface OneToOneJob {

	String getFromDB();

	String getToDB();

	//List<DataMap> getDataMaps();
	
	List<ExecSequence> getExecSequence();
	
}