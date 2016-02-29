package com.cmread.cmu.css.service.sync.recommend;

import com.cmread.cmu.css.task.handler.builder.DataMapBuilder;
import com.cmread.cmu.css.task.handler.builder.OneToOneJobBuilder;

public abstract class BaseDMHelper {

	public static DataMapBuilder mapRecommendLabelByGroupID(String groupID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("con_recommend_clabel").primaryKey("labelid").cond("groupid", groupID);
		dataMap.to().tableName("bks_recommend_clabel").primaryKey("labelid").cond("groupid", groupID);

		return dataMap;
	}
	
	public static DataMapBuilder mapRecommendLabelByLableID(String clabelID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder();

		dataMap.from().tableName("con_recommend_clabel").primaryKey("labelid").cond("labelid", clabelID);
		dataMap.to().tableName("bks_recommend_clabel").primaryKey("labelid").cond("labelid", clabelID);

		return dataMap;
	}
	
	protected static DataMapBuilder mapRecommendLabelGroup(String groupID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_group").primaryKey("groupid").cond("groupID", groupID);
		dataMap.to().tableName("bks_recommend_clabelgroup").primaryKey("groupid").cond("groupID", groupID);

		return dataMap;
	}

	public static DataMapBuilder mapRecommendLabelSubBySubGroupID(String subGroupID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_sub").primaryKey("groupid", "labelid").cond("groupid", subGroupID);
		dataMap.to().tableName("bks_recommend_clabelsub").primaryKey("groupid", "labelid").cond("groupid", subGroupID);

		return dataMap;
	}

	public static DataMapBuilder mapRecommendLabelSubGroup(String subGroupID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_subgroup").primaryKey("groupid").cond("groupid", subGroupID);
		dataMap.to().tableName("bks_recommend_clabelsubgroup").primaryKey("groupid").cond("groupid", subGroupID);

		return dataMap;

	}

	public static DataMapBuilder mapRecommendLabelSubByLabelID(String clabelID, OneToOneJobBuilder job) {
		DataMapBuilder dataMap = job.createDataMapBuilder(); 
		
		dataMap.from().tableName("con_recommend_clabel_sub").primaryKey("groupid", "labelid").cond("labelid", clabelID);
		dataMap.to().tableName("bks_recommend_clabelsub").primaryKey("groupid", "labelid").cond("labelid", clabelID);

		return dataMap;
	}


}
