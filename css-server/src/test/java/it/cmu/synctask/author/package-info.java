/**
 * 这个包主要包含“作家相关同步”的测试用例
 */
/**
 * 
 * 
panglaoying@cmu-server:~$ cmusync ct author
--> From Table : common.mcp_authorinformation
--> To   Table : book.mcp_authorinformation
+-------------------+--------+-------------+---------------------------------+---------+---------+--------------+------------+
| F-NAME            | F-TYPE | F-TYPE-NAME | F-COMMENT                       | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+-------------------+--------+-------------+---------------------------------+---------+---------+--------------+------------+
| AUID              | 12     | VARCHAR2    | 作家ID                            | --      | --      | --           | 作家ID       |
| ACCOUNTID         | 12     | VARCHAR2    | 帐号                              | --      | --      | --           | 帐号         |
| AUNAME            | 12     | VARCHAR2    | 作家姓名                            | --      | --      | --           | 作家姓名       |
| AUPENNAME         | 12     | VARCHAR2    | 作家笔名                            | --      | --      | --           | 作家笔名       |
| SEX               | 12     | VARCHAR2    | 0：男 1：女                         | --      | --      | --           | 0：男 1：女    |
| BIRTHDAY          | 93     | DATE        | 作家生日                            | --      | --      | --           | 作家生日       |
| AUADDRESS         | 12     | VARCHAR2    | 作家地址                            | --      | --      | --           | 作家地址       |
| MARRYSTATE        | 12     | VARCHAR2    | 状态---0,未婚 1,已婚 2,离异 3,丧偶        | --      | --      | --           | 婚姻状态       |
| FIRM              | 12     | VARCHAR2    | 公司                              | --      | --      | --           | 工作单位       |
| POSITION          | 12     | VARCHAR2    | 职务                              | --      | --      | --           | 作家职务       |
| AUMSISDN          | 12     | VARCHAR2    | 作家手机号码                          | --      | --      | --           | 手机号码       |
| TELEPHONE         | 12     | VARCHAR2    | 联系电话                            | --      | --      | --           | 座机号码       |
| POSTADDRESS       | 12     | VARCHAR2    | 通讯地址                            | --      | --      | --           | 通讯地址       |
| IDCARD            | 12     | VARCHAR2    | 身份证件号                           | --      | --      | --           | 身份证号码      |
| POSTCODE          | 12     | VARCHAR2    | 邮政编码                            | --      | --      | --           | 邮政编码       |
| AUSTATUS          | 12     | VARCHAR2    | 状态---0,正式 1,待提交 2,待审核 3,驳回      | --      | --      | --           | 作家审核状态     |
| AULASTSTATUS      | 12     | VARCHAR2    | 状态                              | --      | --      | --           | 状态         |
| CREATEDATE        | 93     | DATE        | 作家注册时间                          | --      | --      | --           | 作家注册时间     |
| GRADE             | 12     | VARCHAR2    | 级别---0,百万级 1,金领级 2,白领级 3,蓝领级    | --      | --      | --           | 作家等级       |
| EMAIL             | 12     | VARCHAR2    | 邮箱                              | --      | --      | --           | 作家邮箱       |
| AUCLASS           | 12     | VARCHAR2    | 类型---0,原创 1,出版 2,女频             | --      | --      | --           | 作家分类       |
| COUNTRY           | 12     | VARCHAR2    | 国家                              | --      | --      | --           | 国家         |
| MSN               | 12     | VARCHAR2    | MSN账号                           | --      | --      | --           | 作家MSN      |
| QQ                | 12     | VARCHAR2    | QQ账号                            | --      | --      | --           | 作家QQ       |
| HONOR             | 12     | VARCHAR2    | 获奖情况                            | --      | --      | --           | 获奖情况       |
| METHOD            | 12     | VARCHAR2    | 创作环境                            | --      | --      | --           | 创作环境       |
| DESCRIBE          | 12     | VARCHAR2    | 简介                              | --      | --      | --           | 作家简介       |
| NOTES             | 12     | VARCHAR2    | 备注                              | --      | --      | --           | 作家备注       |
| FILEPACKAGEID     | 12     | VARCHAR2    | 证件文件包编码                         | --      | --      | --           | 证件文件包编码    |
| REPETITION        | 12     | VARCHAR2    | 副本---空,非副本 非空,值为原作家ID           | --      | --      | --           | 是否副本       |
| AUTHENTICATION    | 12     | VARCHAR2    | 认证状态:0、启用 1、冻结 2、未开通 3、待签约 4、驳回 | --      | --      | --           | 作家认证状态     |
| COMMUTATIVE       | 12     | VARCHAR2    | 互动状态---0,启用互动 1,冻结互动 其他,未开通     | --      | --      | --           | 作家互动状态     |
| AUTHORFIRSTLETTER | 12     | VARCHAR2    | 笔名首字母                           | --      | --      | --           | 笔名首字母      |
| AUTHORGRADE       | 12     | VARCHAR2    | 作家等级---0,普通作者 0以上为名家            | --      | --      | --           | 是否名家       |
| MSISDN            | 12     | VARCHAR2    | 阅读号                             | --      | --      | --           | 阅读号        |
| BURSE             | 12     | VARCHAR2    | 手机钱包账号                          | --      | --      | --           | 手机钱包账号     |
| CERTIFICATEPATH   | 12     | VARCHAR2    | 身份证扫描件（正）                       | --      | --      | --           | 身份证扫描件正面   |
| UNCERTIFICATEPATH | 12     | VARCHAR2    | 身份证扫描件（反）                       | --      | --      | --           | 身份证扫描件反面   |
| SIGNDATE          | 93     | DATE        | 合同签约日期                          | --      | --      | --           | 合同签约日期     |
| EXPIREDATE        | 93     | DATE        | 合同到期日期                          | --      | --      | --           | 合同到期日期     |
| PROCESSID         | 12     | VARCHAR2    |                                 |         |         |              |            |
| CLAIMUSERID       | 12     | VARCHAR2    |                                 |         |         |              |            |
+-------------------+--------+-------------+---------------------------------+---------+---------+--------------+------------+

panglaoying@cmu-server:~$ cmusync ct author_class
--> From Table : common.mcp_auclassinfo
--> To   Table : book.bks_auclassinfo
+--------+--------+-------------+-----------+---------+---------+--------------+------------+
| F-NAME | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+--------+--------+-------------+-----------+---------+---------+--------------+------------+
| AUID   | 12     | VARCHAR2    | 作家ID      | --      | --      | --           | 作家ID       |
| CLASS  | 12     | VARCHAR2    | 类别        | --      | --      | --           | 类别         |
+--------+--------+-------------+-----------+---------+---------+--------------+------------+

panglaoying@cmu-server:~$ cmusync ct author_mcp
--> From Table : common.mcp_authorandmcp
--> To   Table : book.bks_authorandmcp
+--------------+--------+-------------+---------------------+---------+---------+--------------+---------------------+
| F-NAME       | F-TYPE | F-TYPE-NAME | F-COMMENT           | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT          |
+--------------+--------+-------------+---------------------+---------+---------+--------------+---------------------+
| AUID         | 12     | VARCHAR2    | 作家ID                | --      | --      | --           | 作家ID                |
| MCPID        | 12     | VARCHAR2    | MCP编号               | --      | --      | --           | MCP编号               |
| DESCRIBE     | 12     | VARCHAR2    | 作家简介                | --      | --      | --           | 作家简介                |
| RELATIONCODE | 12     | VARCHAR2    | 关系码---1,信息上传 2,信息复用 | --      | --      | --           | 关系码---1,信息上传 2,信息复用 |
+--------------+--------+-------------+---------------------+---------+---------+--------------+---------------------+

 * 
 * @author zhangtieying
 *
 *
 */
package it.cmu.synctask.author;