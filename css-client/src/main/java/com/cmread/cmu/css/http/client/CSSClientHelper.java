package com.cmread.cmu.css.http.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 同步客户端辅助方法类
 * 主要为了简化客户端调用，同时也避免由于参数格式错误等浪费调测时间。
 * 
 * 原则上，需要同步的所有内容（图书、作家、CP、操作员等）如果涉及评审的，评审通过后的修改应触发同步（至少需要逐一分析）；
 * 不涉及评审的，每个增删改的地方都可能需要触发同步（同样需要进行逐一分析）
 * 
 * 以下所有删除动作都假设同步发起方已经做了“完整性验证”；
 * 
 * @author zhangtieying
 *
 */
@SuppressWarnings("serial")
public class CSSClientHelper {

	private CSSClient client;
	
	// 同步任务返回模式，true 表示为同步模式， false 表示为异步模式；
	// 默认为异步返回模式；
	private boolean sync; 
		
	public CSSClientHelper() {
		this.sync = false;
	}
	
	public void setCSSClient(CSSClient client) {
		this.client = client;
	}
	
	public void setCssClient(CSSClient client) {
		this.client = client;
	}
	
	public CSSClient getClient() {
		return this.client;
	}
	
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	
	public CSSClientHelper sync() {
		CSSClientHelper syncHelper = new CSSClientHelper();
		syncHelper.setCSSClient(this.client);
		syncHelper.setSync(true);
		return syncHelper;
	}
	
	private Map<String, String> createMap(String... mapKVList) {
		Map<String, String> map = new HashMap<String, String>();
		
		if ((mapKVList != null) && (mapKVList.length > 0)) {
			int mapSize = mapKVList.length / 2;
			for (int i = 0 ; i < mapSize ; ++i) {
				map.put(mapKVList[i*2], mapKVList[i*2+1]); 
			}
		}

		if (!map.containsKey("sync")) {
			map.put("sync", sync ? "true" : "fasle");
		}
		return map;
	}

	/*
	 * ######################################################
	 *                      角色同步
	 * ######################################################
	 */
	
	/*
	 * 新增角色
	 */
	public void roleAdd(final String roleId) throws RemoteTaskException {
		this.client.createTask("role.add", createMap("roleid", roleId));
	}
	
	/*
	 * 修改角色信息
	 */
	public void roleUpdate(final String roleId) throws RemoteTaskException {
		this.client.createTask("role.update", createMap("roleid", roleId));
	}
	
	/*
	 * 删除角色。
	 * 删除角色时，同时删除该角色与操作员的映射关系
	 */
	public void roleDelete(final String roleId) throws RemoteTaskException {
		this.client.createTask("role.delete", createMap("roleid", roleId));
	}
	
	public void roleDiffSync(final String roleId) throws RemoteTaskException {
		this.client.createTask("role.diff-sync", createMap("roleid", roleId));
	}
	
	/*
	 * 基本权限表-全表同步
	 */
	public void privilegeTableSync() throws RemoteTaskException {
		this.client.createTask("refactor-privilege.table-diff-sync", createMap());
	}
	
	/*
	 * 角色权限映射关系表-全表同步
	 */
	public void rolePrivilegeTableSync() throws RemoteTaskException {
		this.client.createTask("refactor-role-privilege.table-diff-sync", createMap());
	}
	
	/*
	 * ######################################################
	 *                      管理员账号相关信息同步
	 * ######################################################
	 */
	
	/*
	 * 新增管理员（管理员审核通过后同步）
	 * -- 会同步管理员相关表（两表合并）和管理员角色映射关系表
	 */
	public void operatorAdd(final String operatorID) throws RemoteTaskException {
		this.client.createTask("operator.add", createMap("operatorid", operatorID));
	}

	/*
	 * 修改管理员信息。
	 * 以下修改都调用此命令
	 * - 修改管理员基本信息
	 * - 修改管理员与角色映射管理
	 * - 删除管理员（修改状态的删除）
	 */
	public void operatorUpdate(final String operatorID) throws RemoteTaskException {
		this.client.createTask("operator.update", createMap("operatorid", operatorID));
	}
	
	/*
	 * ######################################################
	 *                     用户组同步
	 * ######################################################
	 */

	/*
	 * 新增用户组
	 */
	public void userGroupAdd(final String userGroupID) throws RemoteTaskException {
		this.client.createTask("usergroup.add", createMap("usergroupid", userGroupID));
	}

	/*
	 * 更新用户组信息
	 * 
	 * 说明：
	 * 因为用户组管理小修改较多，统一使用此命令，将用户组更改信息全部同步。
	 * 可能更改包括：用户组信息修改，增加成员等
	 */
	public void userGroupUpdate(final String userGroupID) throws RemoteTaskException {
		this.client.createTask("usergroup.update", createMap("usergroupid", userGroupID));		
	}
	
	/*
	 * 删除用户组
	 */
	public void userGroupDelete(final String userGroupID) throws RemoteTaskException {
		this.client.createTask("usergroup.delete", createMap("usergroupid", userGroupID));
	}
	
	/*
	 * 删除推荐组
	 */
	public void recommendGroupDelete(final String userGroupID) throws RemoteTaskException {
		this.client.createTask("usergroup.recommend.delete", createMap("usergroupid", userGroupID));
	}
	
	/*
	 * 删除推荐单元
	 */
	public void recommendUnitDelete(final String userGroupID) throws RemoteTaskException {
		this.client.createTask("usergroup.recommend-unit.delete", createMap("usergroupid", userGroupID));
	}
	
	/*
	 * 删除推荐组编辑成员，暂时跳过！
	 */
//	public void recommendUnitDelete(final String userGroupID, final String memberID) throws RemoteTaskException {
//		this.client.createTask("usergroup.recommend-member.delete", new HashMap<String, String>() {
//			{
//				put("usergroupid", userGroupID);
//				put("memberid", memberID);
//			}
//		});
//	}
	
	/*
	 * ######################################################
	 *                     CP信息同步
	 * ######################################################
	 */
	
	/*
	 * CP终审通过（CP新增）
	 * 同步CP账号、CP基本信息、CP角色映射等表
	 */
	public void cpAuditPass(final String cpID) throws RemoteTaskException {
		this.client.createTask("cp.audit-pass", createMap("cpid", cpID));
	}

	/*
	 * CP信息更新。
	 * 以下更新均使用此更新：
	 * 1. CP终审后的状态变化
	 * 2. CP基本信息
	 */
	public void cpUpdate(final String cpID) throws RemoteTaskException {
		this.client.createTask("cp.update", createMap("cpid", cpID));
	}
	
	// CP似乎没有删除动作
	
	/*
	 * ######################################################
	 *                     作家同步
	 * ######################################################
	 */
	
	/*
	 * 作家审核通过同步
	 */
	public void authorAuditPaas(final String authorID) throws RemoteTaskException {
		this.client.createTask("author.audit-pass", createMap("authorid", authorID));
	}

	/*
	 * 删除作家（审核后的作家删除）
	 */
	public void authorDelete(final String authorID) throws RemoteTaskException {
		this.client.createTask("author.delete", createMap("authorid", authorID));	
	}
	
	/*
	 * 作家信息更新（也是审核后）???
	 * 包括：
	 * 1. 作家基本信息变更；（TODO 涉及到图书的待定)
	 * 2. 作家与MCP映射关系变化；
	 */
	public void authorUpdate(final String authorID) throws RemoteTaskException {
		this.client.createTask("author.update", createMap("authorid", authorID));	
	}
	
	/*
	 * 这个更新除了同步author外，还需要更新图书相关信息，确认和authorUpdate分开；
	 */
	public void authorUpdateAuditPass(final String authorID) throws RemoteTaskException {
		this.client.createTask("author.update-audit-pass", createMap("authorid", authorID));	
	}
	
	/*
	 * ######################################################
	 *                     版权信息同步
	 * ######################################################
	 */
	
	/*
	 * 版权审核通过同步（第一次同步）
	 */
	public void copyrightAuditPass(final String copyrightID) throws RemoteTaskException {
		this.client.createTask("copyright.audit-pass", createMap("copyrightid", copyrightID));	
	}
	
	/*
	 * 版权信息修改同步
	 */
	public void copyrightUpdate(final String copyrightID) throws RemoteTaskException  {
		this.client.createTask("copyright.update", createMap("copyrightid", copyrightID));	
	}
	
	/*
	 * 版权删除同步
	 */
	public void copyrightDelete(final String copyrightID) throws RemoteTaskException  {
		this.client.createTask("copyright.delete", createMap("copyrightid", copyrightID));	
	}
	
	/*
	 * 版权冻结、解冻调用同一方法，（涉及的图书上下架信息另外调用servlet通知cms,并由cms反向掉同步子系统更新图书信息）
	 */
	public void copyrightFreezeUpdate(final String copyrightID) throws RemoteTaskException  {
		this.client.createTask("copyright.freeze-update", createMap("copyrightid", copyrightID));	
	}

	/*
	 * ######################################################
	 *                     系统配置信息同步
	 * ######################################################
	 */
	
	/*
	 * 目前似乎没有新增和删除系统配置信息的界面，所以这部分仅实现两种，一种是更新系统配置
	 * 条目；另一个是全表差异同步，用于升级和核查时使用； 
	 */
	
	/*
	 * 更新系统配置信息(sup_sys_config)条目
	 */
	public void systemConfigUpdate(final String key) throws RemoteTaskException {
		this.client.createTask("sysconfig.update", createMap("key", key));	
	}
	
	/*
	 * sys_config表的全表差异同步
	 */
	public void systemConfigTableDiffSync() throws RemoteTaskException {
		this.client.createTask("sysconfig.table-diff-sync", createMap());	
	}
		
	/*
	 * ######################################################
	 *                     图书信息同步
	 * ######################################################
	 */
	
	/**
	 * 图书编审审核通过。首批通过和后续通过都调用此方法同步，通过第二个参数区分；
	 * 
	 * 首批和非首批的区别可能在同步内容范围上有区别；
	 * 
	 * @param auditBatchID 审批通过的批次ID
	 * @param firstBatch 是否是首批审核， true 首批 false 非首批
	 * @throws RemoteTaskException
	 */
	public void bookAuditBatchPass(final String auditBatchID, final boolean firstBatch) throws RemoteTaskException {
		this.client.createTask("book.auditbatch.audit-pass",
				createMap("audit_batch_id", auditBatchID, 
						"first_batch", Boolean.toString(firstBatch)));
	}
	
	public void bookAuditBatchPassSync(final String auditBatchID, final boolean firstBatch) throws RemoteTaskException {
		this.client.createTask("book.auditbatch.audit-pass",
				createMap("audit_batch_id", auditBatchID, 
						"first_batch", Boolean.toString(firstBatch), 
						"sync", "true"));
	}
	
	/**
	 * 打包结束同步；
	 * 当打包完成后（应该是成功结束后）触发此同步
	 * 	// 打包完成的时候（可能需要根据状态03 04 ）；仅同步成功的；
	 * 
	 * @param pkid 打包队列id
	 * @throws RemoteTaskException
	 */
	public void bookPackageComplete(final String pkid) throws RemoteTaskException {
		this.client.createTask("book-package.complete", createMap("packageid", pkid));
	}

	/**
	 * 分册打包结束；
	 * 当分册打包结束后（应该是成功结束后）触发此同步；
	 * 
	 * @param pkid 分册打包
	 * @throws RemoteTaskException
	 */
	public void bookFasciculePackageComplete(final String pkid) throws RemoteTaskException {
		this.client.createTask("book-fascicule-package.complete", createMap("packageid", pkid));			
	}

	/*
	 * 系统分册策略同步：
	 * 
	 * 因为系统分册策略较为简单，这里对策略的修改（增删改）都用一个方法；
	 * 
	 * 并存时：从cms同步到bks，bks中的系统分册策略隐藏修改；
	 * 并存后：从bks同步到cms（待确认，看cms是否需要）
	 */
	public void bookSystemTacticUpdate(final String tacticID) throws RemoteTaskException {
		this.client.createTask("book-system-tactic.update", createMap("tacticid", tacticID));
	}

	/*
	 * 共用可以吗？
	 * 1、图书分册管理（增删改、调整、启停图书分册）触发同步流程 修改图书分册策略；同步图书分册相关所有表？
	 * 2、自动分册定时任务完成自动分册后触发
	 */
	public void bookFasciculeUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book-fascicule.update", createMap("bookid", bookID));			
	}	
	
	/*
	 * 
	 * 图书分册审核通过同步
	 * 暂定通过bookid同步全部分册信息
	 */
	public void bookFasciculeAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book-fascicule.audit-pass", createMap("bookid", bookID));				
	}
	
	/* 
	 * 章节隐藏：（章节id，批量）: t_cmp_type_chapter 
	 * 批量的话，可以给一个数组类型的方法？ 
	 */
	public void bookChapterHide(final String chapterID) throws RemoteTaskException {
		this.client.createTask("book-chapter.hide", createMap("chapterid", chapterID));						
	}
	
	/*
	 * 重定价审核通过触发同步
	 */
	public void bookPriceReSetAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.price-reset-audit-pass", createMap("bookid", bookID));				
	}
	
	/*
	 * 精装图书关联同步
	 * 
	 * 精装图书目前在cms中实现，但是精装图书与图书的关联关系需要同步到bks上
	 * 参数是一个还是两个待定，估计一个beautyBookID应该就可以了
	 * 
	 * 同步方向：common -> book
	 */
	public void bookBeautyBookRelate(final String beautyBookID) throws RemoteTaskException {
		this.client.createTask("book-beautybook.relate", createMap("beautybookid", beautyBookID));				
	}
	
	/*
	 * ######################################################
	 *                     图书库管理
	 * ######################################################
	 */
	
	/*
	 * 
	 * 删除下架书籍
	 */
	public void bookDeleteOffShelfUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book-delete-off-shelf.update", createMap("bookid", bookID));				
	}
	
	/*
	 * 
	 * 批量修改质检结果，由于重构一期未实现质检，此处待后续添加 TODO
	 */
	
	 /*
	 * ######## 说明：以下内容校对，基本都按照update来处理 ###################
	 */
	
	/*
	 * 内容校对：书籍卷名校验
	 * 
	 * 说明
	 */
	public void bookVolumeNameUpdate(final String volumeID) throws RemoteTaskException {
		this.client.createTask("book-volume.update", createMap("volumeid", volumeID));				
	}
	
	/*
	 * 内容校对：章节名校验
	 */
	public void bookChapterNameUpdate(final String chapterID) throws RemoteTaskException {
		this.client.createTask("book-chapter.name-update", createMap("chapterid", chapterID));		
	}
	
	/*
	 * 共用
	 * 1、内容校对：章节内容校验
	 * 2、单章导入（单章替换）
	 */
	public void bookChapterContentUpdate(final String chapterID) throws RemoteTaskException {
		this.client.createTask("book-chapter.content-update", createMap("chapterid", chapterID));		
	}
	
	/*
	 * 内容校对：基本信息校验
	 */
	public void bookInformationUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.information-update", createMap("bookid", bookID));		
	}

	/*
	 * 内容屏蔽/解除屏蔽
	 */
	public void bookContentShieldUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.content-shield-update", createMap("bookid", bookID));		
	}
	
	// TODO 修改图书专区上架信息（暂时确认不同同步）

	/*
	 * 批量修改图书分类（批量我先忽略了）
	 */
	public void bookClassModifyUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.class-update", createMap("bookid", bookID));
	}
	
	/*
	 * 图书未完本设置
	 */
	public void bookNotCompleteUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.not-complete-update", createMap("bookid", bookID));
	}
	
	/*
	 * 在www门户发布更新同步
	 */
	public void bookWWWSetUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.www-set-update", createMap("bookid", bookID));
	}
	
	/*
	 * 修改价格
	 * 价格信息同步 
	 * book>common
	 * 最终确定修改价格只在book端触发，同步到common端
	 * 
	 */
	public void bookPriceModifyUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.price-modify-update", createMap("bookid", bookID));
	}
	
	/*
	 *  修改推荐信息/推荐语
	 *  book -> common
	 */
	public void bookRecommendInfomationUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.recommend-info-update", createMap("bookid", bookID));
	}
	
	/*
	 * 图书关联作家
	 * book -> common
	 */
	public void bookAuthorRelatedUpdate(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.author-related-update", createMap("bookid", bookID));
	}
	
	/*
	 * 图书上架
	 */	
	public void bookOnShelfSuccess(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.onshelf.success", createMap("bookid", bookID));
	}
	/*
	 * 图书上线，批量上架
	 */	
	public void bookBatchOnShelfSuccess(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("book-batch.onshelf.success",createMap("bookids", join(bookIDs))); 
	}
	
	/*
	 * 图书下架
	 */	
	public void bookOffShelfSuccess(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.offshelf.success", createMap("bookid", bookID));
	}
	
	/*
	 * 图书下线，批量下架
	 */	
	public void bookBatchOffShelfSuccess(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("book-batch.offshelf.success", createMap("bookids", join(bookIDs))); 
	}
	
	/*
	 * 禁止上下架书单同步（CMS->BKS)
	 */
	public void bookForbidOnOffShelfUpdate(final String bookID, final String forbitType) throws RemoteTaskException {
		this.client.createTask("book.forbid-onoffshelf.update", 
				createMap("bookid", bookID, "forbidType", forbitType));
	}

	/*
	 * ######################################################
	 * 质检相关同步：基本也是图书相关表和内容，但是由质检侧功能触发
	 * ######################################################
	 */
	
	/**
	 * 质检终审通过同步；
	 * 
	 */
	public void bookQcAuditPass(final String bookID)  throws RemoteTaskException {
		this.client.createTask("book.qc.audit-pass", createMap("bookid", bookID));
	}
	
	/**
	 * 质检-图书基本信息审核-推荐语审核通过
	 * @param bookID
	 * @throws RemoteTaskException
	 */
	public void bookQcRecommendInfoAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.qc.recommend-info.audit-pass", createMap("bookid", bookID));
	}
	
	/**
	 * 质检-图书基本信息审核-简介审核通过
	 * @param bookID
	 * @throws RemoteTaskException
	 */
	public void bookQcIntroductionAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.qc.introduction.audit-pass", createMap("bookid", bookID));
	}
	
	/**
	 * 质检-图书基本信息审核-关键字审核通过
	 * @param bookID
	 * @throws RemoteTaskException
	 */
	public void bookQcKeyWordAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.qc.keyword.audit-pass", createMap("bookid", bookID));
	}
	
	/**
	 * 质检-图书基本信息审核-封面审核通过
	 * @param bookID
	 * @throws RemoteTaskException
	 */
	public void bookQcCoverFileAuditPass(final String bookID) throws RemoteTaskException {
		this.client.createTask("book.qc.coverfile.audit-pass", createMap("bookid", bookID));
	}
	
	/**
	 * 质检词库全表同步
	 */
	public void qaBadKeyWordsTableSync() throws RemoteTaskException {
		this.client.createTask("qa-badkeyword.table-diff-sync", createMap());	
	}
	
	/*
	 * ######################################################
	 * 推荐标签组同步：CMS->BKS
	 * ######################################################
	 */

	/*
	 * 通用标签组同步：同步通用标签组和组内标签；
	 */
	public void recommendLabelGroupUpdate(final String labelGroupID) throws RemoteTaskException {
		this.client.createTask("temp.recommand-lable.diff-sync",
				createMap("type", "clabel-group-diff-sync", "clabel_group_id", labelGroupID));
	}
	
	/*
	 * 通用标签同步：仅同步单个通用标签；
	 */
	public void recommendLabelUpdate(final String labelID) throws RemoteTaskException {
		this.client.createTask("temp.recommand-lable.diff-sync",
				createMap("type", "clabel-diff-sync", "clabel_id", labelID));
	}
	
	/*
	 * 编辑标签组同步：同步编辑标签组和组内标签；
	 */
	public void recommendSubLabelGroupUpdate(final String subGroupID) throws RemoteTaskException {
		this.client.createTask("temp.recommand-lable.diff-sync",
				createMap("type", "clabel-subgroup-diff-sync", "clabel_subgroup_id", subGroupID));
	}
	
	/*
	 * 通用标签同步：仅同步单个通用标签；
	 */
	public void recommendSubLabelUpdate(final String subLabelID) throws RemoteTaskException {
		this.client.createTask("temp.recommand-lable.diff-sync",
				createMap("type", "clabel-sub-diff-sync", "clabel_sub_id", subLabelID));
	}
	
	/*
	 * ######################################################
	 * 首批免审管理包括：出版社管理与入库白名单管理
	 * ######################################################
	 */
	/*
	 * 出版社：新增
	 */
	public void publisherAdd(final String publicsherID) throws RemoteTaskException {
		this.client.createTask("publisher.add", createMap("publisherid", publicsherID));	
	}
	
	/*
	 * 出版社：更新
	 */
	public void publisherUpdate(final String publicsherID) throws RemoteTaskException {
		this.client.createTask("publisher.update", createMap("publisherid", publicsherID));	
	}
	
	// 出版社：删除
	public void publisherDelete(final String publicsherID) throws RemoteTaskException {
		this.client.createTask("publisher.delete", createMap("publisherid", publicsherID));	
	}
	
/*
 * 入库白名单似乎只在book这边需要用到，应该不需要往common这边同步吧
 * 
 * 入库白名单管理栏目中：
 * 1、出版社设置：触发【关联出版社同步】	
 * 2、新增、修改入库白名单：触发【入库白名单同步】
 * 3、删除入库白名单：触发【入库白名单同步】、【关联出版社同步】
 * 
 */
//	 //TODO 【入库白名单同步】:暂定不同步
//	public void whiteListTodo(final String publicsherID) throws RemoteTaskException {
//		this.client.createTask("publisher.delete", new HashMap<String, String>() {
//			{
//				put("publisherid", publicsherID);
//			}
//		});	
//	}
//	
//	// TODO 【关联出版社同步】：暂定不同步
//	public void whiteListPublisherRelateUpdate() throws RemoteTaskException {
//		this.client.createTask("publisher.delete", new HashMap<String, String>() {
//			{
//				//put("publisherid", publicsherID);
//			}
//		});			
//	}
	
	/*
	 * 推荐：图书分级审核通过
	 */
	public void recommendBookLevelAuditPass(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("recommend.booklevel.audit-pass", createMap("bookids", join(bookIDs))); 
	}
	
	/* 
	 * 推荐：评分同步（默认：异步调用）
	 */
	public void recommendBookGradeUpdate(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("recommend.bookgrade.update", createMap("bookids", join(bookIDs)));
	}
	
	/* 
	 * 推荐：评分同步（同步调用）
	 */
	public void recommendBookGradeUpdateSync(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("recommend.bookgrade.update", createMap("bookids", join(bookIDs), "sync", "true")); 
	}
	
	/* 
	 * 推荐：图书子系统  推荐库管理 列表中需展示  图书最新上架时间
	 * 注意：con_recomend_on_off_his表为日志表，不全量同步，
	 * 根据bookid查询是否为bks侧数据，然后以bookid为主键更新bks_recomend_on_off_his表中对应记录，以此方式，bks中该记录表的数量级与book主表等同，而非日志级别
	 */
	public void recomendOnOffHisSync(final String bookID) throws RemoteTaskException {
		this.client.createTask("recommend.on-off-his.update", createMap("bookid", bookID));
	}
	
	/*
	 * 我的消息/提醒的同步；move类型同步
	 */
	public void moveNotice() throws RemoteTaskException {
		this.client.createTask("notice.move", createMap());
	}
	
	/*
	 * 操作日志
	 */
	public void operateLog(final String operateID, final String operateName, final String roleID,
			final String roleName, final String stepID, final String stepdes, final Date operateTime,
			final String operateObject, final String workNum) throws RemoteTaskException {
		this.client.createTask("operator.log", new HashMap<String, String>() {
			{
				put("operateID", operateID);
				put("operateName", operateName);
				put("roleID", roleID);
				put("roleName", roleName);
				put("stepID", stepID);
				put("stepdes", stepdes);
				put("operateTime", Long.toString(operateTime.getTime()));
				put("operateObject", operateObject);
				put("workNum", workNum);
			}
		});		
	}

	// 分类信息同步（通用->图书）
	/*
	 * 新增、修改、删除内容分类需要触发【分类信息同步】,共用算了
	 */ 
	public void bookClassDiffSync(final String classcode) throws RemoteTaskException {
		this.client.createTask("book-class.diff-sync", createMap("classcode", classcode));	
	}
	
	/*
	 * 系列同步（通用->图书） 系列的增删改
	 */ 
	public void bookSeriesDiffSync(final String seriesID) throws RemoteTaskException {
		this.client.createTask("book-series.diff-sync", createMap("seriesid", seriesID));	
	}
	
	/*
	 * 系列和图书的关联关系管理，系列中增加图书，删除图书
	 */ 
	public void bookSeriesRelationDiffSync(final String[] bookIDs) throws RemoteTaskException {
		this.client.createTask("book-series-relatons.diff-sync", createMap("bookids", join(bookIDs)));	
	}

	
	/*
	 * 
	 * 抢先图书设置
	 * {:from  => 'common.con_chaperpublishmode',
	 * :to    => 'book.con_chaperpublishmode',
	 * 
	 */
	public void ChaperPublishModeDiffSync(final String bookID) throws RemoteTaskException {
		this.client.createTask("chaper-publish-mode.sync", createMap("bookid", bookID));
	}
	
	/*
	 * 连载预定提醒（book>common）
	 * 表con_serialpush在只在common侧存在
	 * bks只往同步系统传需要的参数
	 */ 
	public void bookSerialPush(final String lastOnshelfChaptername, final String bookID, final String bookName,
			final String status) throws RemoteTaskException {
		this.client.createTask("book.serial-push", 
				createMap("lastonshelfchaptername", lastOnshelfChaptername, 
						"bookid",bookID, 
						"bookname", bookName, 
						"status", status));
	}
	
	/*
	 * 续传章节免审操作表的同步（book>common）；move类型同步
	 */
	public void moveResumeChapNotReviewOper() throws RemoteTaskException {
		this.client.createTask("resume-chap-not-review-oper.move", createMap());
	}	
	
	/*
	 * 销量排行榜（book>common）；move类型同步
	 */
	public void moveBookRankStores() throws RemoteTaskException {
		this.client.createTask("book-rank-store.move", createMap());
	}
	
	/*
	 * MM话单：move类型同步
	 */
	public void moveConSyncBookInfo() throws RemoteTaskException {
		this.client.createTask("con_sync_book_info.move", createMap());
	}
	
	/*
	 * ###################################################### 
	 * 稽查图书信息
	 * ######################################################
	 */

	/*
	 * 图书全量信息同步
	 * 
	 * 当图书信息不一致的时候根据bookid进行全量书籍信息的同步
	 */ 
	public void bookAllInfoDiffSync(final String bookID) throws RemoteTaskException {
		this.client.createTask("book-all-info.diff-sync", createMap("bookid", bookID));
	}
	
	/*
	 * ###################################################### 
	 * 共存期需要额外同步的begin，由sql拦截器调用
	 * ######################################################
	 */
	
	/*
	 * #风险等级表:
	 * book_qc_result_items => 
	 * {:from  => 'common.con_qc_result_items',
	 * :to    => 'book.bks_qc_result_items',
	 * :key   => ['ID']}
	 * 
	 */	
	public void QcResultItemAdd(final String ID) throws RemoteTaskException {
		this.client.createTask("qc-result-item.add", createMap("id", ID));
	}
	
	public void QcResultItemUpdate(final String ID) throws RemoteTaskException {
		this.client.createTask("qc-result-item.update", createMap("id", ID));
	}
	
	public void QcResultItemDelete(final String ID) throws RemoteTaskException {
		this.client.createTask("qc-result-item.delete", createMap("id", ID));
	}
	
	/*
	 * #评分维度表:
	 * con_recommend_evaluations => 
	 * {:from  => 'common.con_recommend_evaluations',
	 * :to    => 'book.bks_recommend_evaluations',
	 * :key   => ['ITEMID']}
	 * 根据页面功能，只需提供新增和删除api
	 */	
	//新增
	public void RecommendEvaluationAdd(final String itemID) throws RemoteTaskException {
		this.client.createTask("recommend-evaluations.add", createMap("itemid", itemID));
	}
	//删除
	public void RecommendEvaluationDelete(final String itemID) throws RemoteTaskException {
		this.client.createTask("recommend-evaluations.delete", createMap("itemid", itemID));
	}
	
	/*
	 * #分类维度表:
	 * con_class_evaluation => 
	 * {:from  => 'common.con_class_evaluation',
	 * :to    => 'book.bks_class_evaluation',
	 * 根据页面功能，只需提供新增update
	 */
	public void ClassEvaluationUpdate(final String secondclassID) throws RemoteTaskException {
		this.client.createTask("recommend-class-evaluation.update", createMap("secondclassid", secondclassID));	
	}
	
	/*
	 * 总编管理
	 * recommend_chiefeditor => 
	 * {:from  => 'common.con_recommend_chiefeditor',
	 * :to    => 'book.bks_recommend_chiefeditor',
	 */
	public void RecommendChiefEditorDiffSync(final String auditorID) throws RemoteTaskException {
		this.client.createTask("recommend-chief-editor.sync", createMap("auditorid", auditorID));	
	}
	
	/*
	 * 分类标签 
	 * {:from  => 'common.con_commend_lable',
	 * :to    => 'book.bks_recommend_lable',
	 * 当心名字取得有差别
	 */
	public void RecommendLableDiffSync(final String ID) throws RemoteTaskException {
		this.client.createTask("recommend-lable.sync", createMap("id", ID));
	}
	
	/*
	 * 热门书单
	 * {:from  => 'common.con_book_hot',
	 * :to    => 'book.bks_book_hot',
	 */
	public void HotBooksDiffSync(final String bookID) throws RemoteTaskException {
		this.client.createTask("hot-books.sync", createMap("bookid", bookID));
	}
	
	/*
	 * 总编自动审核开关
	 * {:from  => 'common.con_rec_chiefeditor_statue',
	 * :to    => 'book.bks_rec_chiefeditor_status',
	 * 当心名字取得有差别
	 */
	public void ChiefEditorStatusDiffSync(final String auditorID) throws RemoteTaskException {
		this.client.createTask("chief-editor-status.sync", createMap("auditorid", auditorID));
	}	 	
	
	
	/*
	 * ###################################################### 
	 * 漫画相关同步
	 * ######################################################
	 */
	
	public void cartoonAdd(final String bookID) throws RemoteTaskException {
		this.client.createTask("cartoon.add", createMap("bookid", bookID));
	}
	
	public void cartoonDelete(final String bookID) throws RemoteTaskException {
		this.client.createTask("cartoon.delete", createMap("bookid", bookID));
	}
	
	public void cartoonInfomationUpdate(final String bookID, String subType) throws RemoteTaskException {
		this.client.createTask("cartoon.information.update", 
				createMap("bookid", bookID, "subtype", subType));
	}
	
	public void cartoonChapterUpdate(final String chapterID) throws RemoteTaskException {
		this.client.createTask("cartoon.chapter.update", createMap("chapterid", chapterID));
	}
	
	public void cartoonChapterDelete(final String chapterID) throws RemoteTaskException {
		this.client.createTask("cartoon.chapter.delete", createMap("chapterid", chapterID));
	}
	
	/*
	 * ###################################################### 
	 * 共存期需要额外同步的end
	 * ######################################################
	 */
	
    public static String join(String[] array) {
        if (array == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < array.length; ++i) {
        	if (i > 0) {
        		sb.append(",");
        	}
        	if (array[i] != null) {
        		sb.append(array[i]);
        	}
        }
        
        return sb.toString();
    }

 }
