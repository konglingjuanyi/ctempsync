package com.cmread.cmu.css.http.client;

import java.util.HashMap;
import java.util.Map;

import com.cmread.cmu.css.http.SyncTaskResponse;

/*
 * 内容使用检查服务
 * 
 * 主要为业务侧提供跨库的内容使用情况检查，用于需要删除被同步内容的情况，比如作家等；
 * 
 */
@SuppressWarnings("serial")
public class ContentUseChecker {
	
	private CSSClient client;
	
	public void setCSSClient(CSSClient client) {
		this.client = client;
	}
	
	/*
	 * 判断作家是否可以删除；
	 * 
	 * 返回值
	 * key : authorBookExist     : 检测项名称
	 * value : 'true' or 'false' : true表示检测项存在，即表示不能删除 ； false 表示检测项没有数据，可以删；
	 * 
	 * authorBookExist : select count(*) from con_auditbookinfo where bookstatus not in ('89','98','99') and authorid=$(authorid)
	 */
	public Map<String, String> authorDeleteCheck(final String authorID) throws RemoteTaskException {
		SyncTaskResponse response = client.createTask("usecheck.author", new HashMap<String, String>() {
			{
				put("authorid", authorID);
				put("sync", "true");
			}
		});
		
		return checkAction(response);
	}
	
	/*
	 * recommendBookExist : select count(*) from bks_recommend t where t.groupid=$(groupID)
	 * recommendEBookExist : select count(*) from t_cmp_type_ebook where objectid in (select bookid from bks_recommend where groupid=$(groupID))
	 * recommendRuleExist : select count(*) count from bks_recommend_rule t  where t.targetgroupid = $(groupID) and t.targettype in (0,2)
	 */
	public Map<String, String> recommendGroupDeleteCheck(final String groupID) throws RemoteTaskException {
		SyncTaskResponse response = client.createTask("usecheck.recommend-group-delete", new HashMap<String, String>() {
			{
				put("groupID", groupID);
				put("sync", "true");
			}
		});
		
		return checkAction(response);
	}
	
	/*
	 * recommendBookExist : select count(*) from bks_recommend t where t.unitid=$(groupID)
	 * recommendEBookExist : select count(*) from t_cmp_type_ebook where objectid in (select bookid from bks_recommend where unitid=$(groupID))
	 * recommendRuleExist : select count(*) count from bks_recommend_rule t where t.targetunitid = $(groupID)
	 * 
	 */
	public Map<String, String> recommendUnitDeleteCheck(final String groupID) throws RemoteTaskException {
		SyncTaskResponse response = client.createTask("usecheck.recommend-unit-delete", new HashMap<String, String>() {
			{
				put("groupID", groupID);
				put("sync", "true");
			}
		});
		
		return checkAction(response);
	}
	
	/*
	 * recommendBookExist : select count(*) from bks_recommend t, con_auditbookinfo b where b.bookid = t.bookid and b.bookstatus in ('11','12','13','14','16') and t.groupid=$(groupID) and t.auditorid=$(memberID)
	 */
	public Map<String, String> recommendMemberDeleteCheck(final String groupID, final String memberID) throws RemoteTaskException {
		SyncTaskResponse response = client.createTask("usecheck.recommend-unit-delete", new HashMap<String, String>() {
			{
				put("groupID", groupID);
				put("memberID", memberID);
				put("sync", "true");
			}
		});
		
		return checkAction(response);
	}
	
	private Map<String, String> checkAction(SyncTaskResponse response) throws RemoteTaskException {
		Map<String, String> result = response.getResult();

		if (result.isEmpty()) {
			throw new RemoteTaskException("task exec failed.");
		}

		return result;
	}
}
