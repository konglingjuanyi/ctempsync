package com.cmread.cmu.css.service.sync.usergroup;

import java.util.concurrent.ExecutionException;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowSet;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;

public class BaseRecommendGroupHelper {

	public static void deleteRecommendGroupRule(String groupID, String targetType, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String queryRuleSql = String.format("select r.ruleid as ruleid, r.prioritylevel as prioritylevel "
				+ "from bks_recommend_rule r, bks_usergroup_info u, bks_usergroup_info unit, mcp_authorinformation author "
				+ "where r.targetgroupid = u.groupid  and r.targetunitid=unit.groupid and r.authorid=author.auid "
				+ "and r.targettype = %s and r.targetgroupid = %s", targetType, groupID);
		
		RowSet rs = sqlExec.queryForDataSet(queryRuleSql).get();
		for (Row row : rs.getRows()) {
			String ruleID = row.getFieldValue("ruleid").toString();
			String priorityLevel = row.getFieldValue("prioritylevel").toString();
			
			deleteRecommendRule(ruleID, sqlExec);
			deleteRecommedRuleParameter(ruleID, sqlExec);
			deleteRecommedRuleCondition(ruleID, sqlExec);
			updateRecommedRule(ruleID, priorityLevel, sqlExec);
		}
	}

	public static void deleteRecommendUnitRule(String userGroupID, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String queryRuleSql = String.format("select r.ruleid as ruleid, r.prioritylevel as prioritylevel "
				+ "from bks_recommend_rule r, bks_usergroup_info u, bks_usergroup_info unit, mcp_authorinformation author "
				+ "where r.targetgroupid = u.groupid  and r.targetunitid=unit.groupid and r.authorid=author.auid "
				+ "and r.targetunitid = %s", userGroupID);
		
		RowSet rs = sqlExec.queryForDataSet(queryRuleSql).get();
		for (Row row : rs.getRows()) {
			String ruleID = row.getFieldValue("ruleid").toString();
			String priorityLevel = row.getFieldValue("prioritylevel").toString();
			
			deleteRecommendRule(ruleID, sqlExec);
			deleteRecommedRuleParameter(ruleID, sqlExec);
			deleteRecommedRuleCondition(ruleID, sqlExec);
			updateRecommedRule(ruleID, priorityLevel, sqlExec);
		}
	}
	
	public static void deleteRecommendRule(String ruleID, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String sql = String.format("delete bks_recommend_rule where ruleid=%s", ruleID);
		sqlExec.executeUpdate(sql).get();
	}
	
	public static void deleteRecommedRuleParameter(String ruleID, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String sql = String.format(
				"delete from bks_recommend_parameter where conditionid in (select conditionid from bks_recommend_condition where ruleid=%s)",
				ruleID);
		sqlExec.executeUpdate(sql).get();
	}
	
	public static void deleteRecommedRuleCondition(String ruleID, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String sql = String.format("delete from bks_recommend_condition where ruleid = %s", ruleID);
		
		sqlExec.executeUpdate(sql).get();
	}

	public static void updateRecommedRule(String ruleID, String priorityLevel, SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String sql = String.format("update bks_recommend_rule set prioritylevel=%s where ruleid = %s", priorityLevel, ruleID);
		
		sqlExec.executeUpdate(sql).get();
	}

	// 删除推荐总编状态表中不再使用的推荐总编
	public static void deleteUnusedRecommendAuditor(SqlExecutor sqlExec) throws InterruptedException, ExecutionException {
		String sql = "delete bks_rec_chiefeditor_statue con where not exists "
				+ "(select sup.recommendauditor from bks_usergroup_info sup where sup.recommendauditor = con.name)";

		sqlExec.executeUpdate(sql).get();
	}

}
