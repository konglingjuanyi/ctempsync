
# 版本发布情况

## v1.1.0 

	[特性]
	
	* [task] : 整合并统一所有文件表相关同步（file、xxxfile、ref_xxxfile）到一个类中，并全部支持延后删除特性；
	* [task] : 删除分册打包同步；
	* [task] : 漫画同步。增加新增漫画、删除漫画、修改漫画信息、修改章节实体信息、删除章节等cartoon->common的同步；
	* [task] : 操作员、mcp、系统表、作家、图书分类、抢先图书设置等增加common->cartoon的同步；

	
## v1.0.11 

	[特性]
	
	* [csync-client] : 修改客户端，支持所有同步通过sync()变为同步调用模式；
	
## v1.0.10 20160224

	[特性]
	
	* [csync-client] : 增加四个推荐标签库同步的API；
	* [task] : 新增禁止图书上下架书单同步(cms->bks)，同时删除原来的类似同步任务，使用新的更严谨的实现；
		
	[优化]
	
	* [csync-server] : 将日志中配置历史日志保留天数的参数提取出来，默认将日志保存变为15天，webjoin配置暂时设置为30天；
	* [csync-server] : 将持久化日志（数据库）保留时间(webjoin配置模板)默认改为30天；
	* [csync-server] : 日志优化，将任务日志记录在任务记录文件中。基本上相当于每次执行一个日志item，用json格式记录（方便后期进行分析和总结）；
	                   配置为每天一个文件，方便查找和搜索；
	* [csync-server] : 关闭sqlinvoke接口的执行修改sql的能力，仅对外开放查询接口；
	* [csync-server] : sqlinvoke的日志记录到csync-sqlinvoke.log文件中，不写入其他文件中（包括error.log），避免触发无谓告警；
	* [csync-server] : sqlinvoke 使用gson流模式返回结果，允许打断（客户端断掉就自动打断）;
	* [csync-server] : SqlExecutor允许通过方法关闭某个sql执行的结果的显示（ResultSet结果表）；sqlinvoke通过此方法关闭查询的结果输出；
	
	[Bug修复]
	
	* [csync-server] : 修复scheduler.properties的webjoin模板中task-clear.reserveDays参数默认值配置错误的问题；

## v1.0.9  20160204

	[优化]
	
	* [task] : 图书批次同步中con_auditbookinfocomp表在cms库查询很慢（约1分钟，原因是查询语句仅使用了该表的联合主键中的第二个，导致全表搜索，经优化后仅需1秒左右；
	
	[BUG修复]
	
	* [task] : 图书批次同步中出现当编辑乱序审批批次同步时，图书章节的顺序会出现错乱；因此前期编审这边反馈的对于t_cmp_type_chapter的ordernum、
	           prechapterid和nextchapterid的处理过程可能有问题，目前从稳妥角度考虑，在普通批次章节同步之后，增加一个图书的所有章节的上述三个
	           字段的同步，待后续和编审确认后再考虑优化方案；

## v1.0.8 20160203

	这个版本主要包含质检相关同步，实现sql via json外部访问接口（用于核查系统）、修复图书批次审核同步和打包同步的BUG；
	
	[特性]
	
	* [csync-server] : 新增/content-sync-system/css/sqlinvoke接口，实现对外基于json的数据库访问接口，主要为临时的基于ruby的核查和监控模块使用；
	* [task] : 新增质检-推荐语审核通过(book.qc.recommend-info.audit-pass)同步；
	* [task] : 新增质检-简介审核通过（book.qc.introduction.audit-pass）同步；
	* [task] : 新增质检-关键字审核通过（book.qc.keyword.audit-pass）同步；
	* [task] : 新增质检-封面审核通过（book.qc.coverfile.audit-pass）同步；
	* [task] : 新增质检-终审通过（bookFasciculeAction）同步；
	* [task] : 新增基本权限表[REFACTOR_T_BME_PRIVILEGE]全表同步;
	* [task] : 新增角色权限映射关系表[REFACTOR_T_BME_ROLE_PRIVILEGE]全表同步；
	* [task] : 新增质检词库[QA_SUP_BADKEYWORDS]全表同步;
	* [schedule] : 新增权限表和角色权限映射关系表的定时全表同步（每天2:00）；
	* [schedule] : 新增质检词库的定时全表同步（每1小时）； 
	
	[BUG修复]
	
	* [csync-client] : 当客户端调用方法为同步时，类似图书同步时间执行较久（大概为45秒），而客户端http读超时时间默认为30秒，导致出现客户端抛出错误日志；
	                   将客户端默认超时时间改为90秒，同时允许通过配置修改此参数；
	* [csync-server] : 当bks的表中也有ownedplace字段的时候，同步会报错；修复此问题（忽略bks表中的ownedplace字段）
	* [task] : 在图书批次审核通过同步中，增加con_bookchapter_dis和con_chapter_pageinfo表的同步；
	* [task] : 在打包完成同步中，也增加上面两个表的同步，目前得到信息是这两个表有部分流程是打包引擎生成，所以在打包同步中增加同步；
	        
## v1.0.7 20160125

	[优化]
	
	* [schedule] : BookOnOffShelfTrigger上下架同步定时触发器，将sql语句的结束时间由目前的提前3秒改为当前时间提前10秒，时钟不同步导致的丢失同步记录
	               的可能性相对更小； 
	* [schedule] : BookOnOffShelfTrigger，查询con_on_off_shelfrecord表的时候打印出operatiottime和offshelfreason字段（仅为调试使用，
	               无业务含义；
	* [schedule] : BookOnOffShelfTrigger和上下架同步代码中，将判断是否是bks库中的图书的select语句简化，由select * 改为select几个字段，
	               主要是日志方面的优化，减少不必要的日志量；
	* [csync-server] : 新增csync-error.log日志，记录所有ERROR和WARN的日志，包括定时器和任务执行等，主要为运维方便检查；这个日志不影响原
	                   csync-server.log记录的信息；  
	* [csync-server] : 将task相关日志写入到csync-task.log文件中，其他日志（包括定时器日志）写入到原来的csync-server.log文件中，且不加taskTraceID前缀；             
	* [csync-server] : 前面使用mvn打war包时，会将src/main/resources下的文件打在war的根目录上，此次通过修改pom避免出现这种情况；
	* [csync-server] : 将sqlexecutor中的两个直接返回ResultSet的方法改为ResultSetConvertor的回调方式，注释掉原方法，相对使用上更加安全；
	* [csync-server] : 增加sqlTaskID，实现sqltask的请求和响应的日志之间的关联； 
	
	[BUG修复]
	
	* [task] : 修复"book-batch.offshelf.success"(批量下架同步）没有在task-book.xml中正确映射的bug； 
	
## v1.0.6 20160120

	[FEATURE]
	
	* [task] : 分册更新同步中，增加t_cmp_type_ebook表的同步（该表中有一字段表示是否有分册）；
	* [task] : notice（我的提醒）的同步的部分sql是按照异步方式执行的，任务先结束，这个版本改为同步执行，所有都执行完成才算任务完成；

## v1.0.5 20160114 

	[FEATURE] 
	
	* [csync-server] : 支持DiffSync的ActionFilter拦截，可以在执行action之前拦截action，并做相应处理；
	
	[BUGFIX]
	
	* [task] : 打包同步，升级时出现先删除t_cmp_type_mebfile表，然后再删除相关t_cmp_ref_ebookmebfile表的情况，导致出现外键约束冲突；修改
	           代码，拦截前面diffSync的deleteAction，放到最后执行，解决这个问题；
	* [task] : 打包同步，将bks的con_package_queue表中的CONVERTSTATUS字段在插入cms时修改为null；

## v1.0.4 20160114

	[FEATURE]
	
	* [csync-server] : 在classpath中新增version.properties，包含项目版本号，由mvn打包时自动更新(尚未有界面显示);
	* [task] : 优化上下架同步。首先触发到一个非持久化任务中，在任务中检测bookid是否在bks库中，在则触发实际的上下架同步，否则不触发；
	* [task] : 优化上下架同步。将原先的大部分updateAction改为diffSyncAction，避免大批量章节更新（具体优化效果待确认）
	* [task] : 增加上下架自动触发定时任务（根据cms的con_on_off_shelfrecord表中上下架记录自动触发csync-server同步），主要为解决老CMU对bks
	           中图书上下架时导致失同步的问题；
	
	[BUGFIX]
	
	* [csync-server] : 修复csync-server内部触发任务时，新创建任务的MDC会污染原流程中的MDC的问题。主要影响为日志文件中的task-id出现不必要的关联。
	* [csync-server] : 修改sql日志前缀[dbName]仅当出于同步任务时才能打印，非同步任务没有显示的bug；
	* [task] : 修复作家同步中更新笔名出错的bug。准确的说，不算是csync的bug :)，当更新笔名时，需要更新bks中的一些表，其中现网
	           没有con_auditbookinfoback表导致同步出错，经查，现有bks中没有使用这个表，因此先讲此表更新语句注释掉；

## v1.0.3 20160111

	[feature]
	
	* [csync-server] : 调整log4jdbc配置，不设最大行长度限制（即sql打印不会自动换行了）
	* [csync-server] : 日志输出优化。针对多行日志增加traceID前缀（通过扩展logback的encoder实现）;
	* [csync-server] : 日志优化，统一任务结果日志，非SUCCESS的日志级别改为ERROR;
	* [csync-server] : 在任务调度模块中的增加当前任务数和总任务数的输出（输入到日志中）;
	* [csync-server] : 结构优化，将定时任务的配置从application-config.xml中移出到task-schedule.xml中;
	* [csync-client] : 统一添加客户端调用失败的告警日志； 
	* [task] : 新增mm话单表(con_sync_book_info)同步(move类型)，新增相应的定时触发任务，目前配置半小时执行一次; 
	
	[bugfix]
	
	* [task] : 修复连载更新通知同步book.seriel-push的一个BUG(con_serialpush), 表现为显示同步成功，但是有一个SQL中间出错，实际未执行成功；
	           同时发现并解决这个流程的一个逻辑错误；
	* [task] : 修复作家同步的一个BUG，原因是mcp_authorandmcp表的主键弄错，导致同步失败；
	* [task] : 图书批次审核通过，新增一个动作（更新当前审核批次最小章节的上一章节（上一批次的最后一章）的下一章节字段）
	* [task] : 在所有章节同步中增加对status的过滤处理（审核后状态以common为主）
	

## v1.0.2 20160105

	* [csync-client] : 添加批量上下架接口
	* [csync-server] : 添加批量上下架服务端方法

## v1.0.1 20160104
	
	* [csync-client] : 修复当传入的Map参数中的value为空值时，会在客户端出现编码异常的bug，目前统一改为空串；
	* [csync-server] : 删除无用的System.out打印语句；
	
## v1.0.0 20151230 

	* 第一个正式版本
	* [server] : 修复notive.move的delete-sql有问题的bug；

## v0.2.1 

	* [csync-client] : 消息体中的clientVersion改为<client>；

## v0.2.0   20151229 

	* [task] : 新增内容系列内图书同步; 客户端增加相应方法； 

## v0.1.15  20151229

	* [csync-server] : 修改数据库密码为加密后字符串；
	* [csync-server] : 修改同步记录表名为csync_task；同时新增同步成功记录表和失败记录表；
	* [csync-server] : 新增持久化全局开关；
	* [csync-server] : 修改内置同步任务的持久化开关和默认优先级;
	* [csync-server] : sql打印的上下文（增加在哪个数据库执行的上下文）;
	
## v0.1.14 20151228

	* [csync-client] : RecomendOnOffHisSync接口改为recomendOnOffHisSync

## v0.1.13 20151228

	* [task] : 将log4j的udp-appender放在这个版本中（临时）

## v0.1.12 20151227

	* [task] : 新增质检风险等级评价同步；新增免审统计同步；修改sup_sys_config（表名变更）；新增推荐分类维度同步和维度同步任务；
	* [task] : 将系统分册策略同步改为common->book，同时修改book表名为bks_system_tactic；
	* [task] : 修改我的提醒notice同步的sql语句错误的bug;
	* [task] : 配合sql拦截，新增若干临时同步任务：操作员更新(by name)、质检评分项、作家更新（by name）；维度新增同步；
	* [server] : 修复若干log日志错误；

## v0.1.11 2015121 

	* [server] * 解决了一个弱智的BUG，BUG的表现是当大批量请求发送到同步服务器时，同步服务器会丢失部分请求；将0.1.8的FIFO策略改回内容关联排队策略；测试并发1700同步任务成功；
	* [csync-client] : 增加图书批次同步的同步调用方法；
	* [task] : 新增连载更新同步；新增统计同步；
	* [task] : 新增图书核查同步（按图书ID而非批次ID)

## v0.1.10 20151215

	* [csync-client] : 增加recommendBookGradeUpdateSync方法（同步返回）；（重构）修改原方法recommand为recommend ~(@^_^@)~
	* [csync-client] : maven增加源码包配置，打包部署时自动部署source包；
	* [server] : maven增加配置，当执行deploy时不上传server的war包；
	* [check-task] : 修复推荐单元删除判断中“判断编辑规则是否存在”的sql语句写错的bug；

## v0.1.9 20151211

	* [check-task] : 新增推荐组删除、推荐单元删除、推荐单元成员删除的校验；
	* [task] : 新增推荐组删除、推荐单元删除同步任务；推荐单元编辑的删除操作太复杂，改为接口实现；	
	* [task] : 修改价格同步（经讨论，图书产品价格表放到图书子系统创建和维护，做单向同步），相关同步方法做了修改；

## v0.1.8 20151211

	* [server] : 将任务排队策略改到FIFO，目前的相关队列模型似乎在并发时存在问题（高负载情况下出现），待后续进一步测试；
	* [server] : bug-fix 修复优先级任务线程池在高并发下任务优先级比较异常的bug；
	* [task] : bug-fix 修改作家同步失败。原因是作家认证关系表在bks库中没有，暂时去掉；
	* [task] : 新增操作日志同步（直接写入common数据库）；
	* [task] : 增加内容分类、内容系列的同步、纠正产品表的问题、修复价格同步的问题；

## v0.1.7 20151210

	* [csync-client] : 删除客户端authorUpdateAuditPass同步方法，和authorAuditPass合并为一个；
	* [csync-client] : 增加slf4j日志（1.4.2），增加任务创建请求和响应的日志记录；
	* [csync-client] : 客户端在请求中上传客户端jar包名字，用于测试时协助诊断问题；
	* [server] : 增加定时任务开关（可以通过schedule.properties配置文件配置是否启动某个定时任务）
	* [server] : 服务器端日志完善，新增任务上下文ID，使用taskID将同一任务的日志关联起来，可以直接使用grep [taskID]搜索同一任务的日志；
	* [server] : webapp重新加载的时候会释放内部各个模块中的线程资源，避免出现内存泄漏；
	* [server] : 修改logback.xml配置，增加webjoin的配置支持。实现IDE中输出到console，webjoin中输出到tomcat日志目录中；
	* [task] : 新增我的消息同步（通过数据库move+定时任务）；
	* [task] : 修改作家同步，在作家修改笔名后，更新图书几张表；
	* [task] : 新增推荐标签库全表DiffSync同步任务；(至少测试阶段还是可以接受）
	* [task] : 更新上下架同步逻辑，增加了三个新的表的反向同步，同时补充了已有表的更新字段；
	* [task] : 新增图书库管理下，删除下架图书、内容校对（书籍、章节、卷、基本信息等）、内容屏蔽、图书分类、未完本、www门户发布、修改价格、关联作家等的同步（服务器和客户端）

## v0.1.6 20151207

	* [csync-client] : 新增两个上下架同步；
	* [server] : 实现上下架同步的服务器端；
	* [server] : bug-fix 修复图书同步的基类中toDB写死为common的bug；
	* [server] : 实现当diff-sync时，insert失败后直接delte再insert（临时方案，应当在后续版本中删除）；
	
## v0.1.5  20151206

	o(╯□╰)o 继续健忘中...
	
## v0.1.4  20151205

	* [csync-client] - 客户端支持关联查询（Helper方法支持author删除）
	* [server] - 实现两个推荐同步（分级审核、评分）
	* [server] - 支持关联查询任务能力

## v0.1.3  20151205

	* [csync-client] - 客户端API默认改为异步执行；
	* [csync-client] - 当客户端没有配置url或者配置url为空串的时候，跳过向服务器端发送请求（主要为测试考虑）直接返回；
	* [csync-client] - 新增推荐的两个同步api（图书分级审核同步和图书评分同步）: (服务器端未测）
	* [server] - 将默认的HTTP同步返回改为异步返回；可以通过客户端请求中的"sync"参数指定该响应是同步返回还是异步返回；
	* [server] - 增加部分日志；
	
## v0.1.2  20151204

	o(╯□╰)o 忘了这个版本改了啥了

## v0.1.1  20151203

	* [csync-client] - maven暂时去掉对slf4j和logback的依赖（因为和网站部分的slf4j版本冲突）

## v0.1.0  20151202

	* 初始版本

	

	
