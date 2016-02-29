/**
 * 
 * cmusync ct copyright_info
--> From Table : book.bks_copyright_info
--> To   Table : common.con_copyright_info
+--------------------+--------+-------------+-----------+---------+---------+--------------+---------------------------------+
| F-NAME             | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                      |
+--------------------+--------+-------------+-----------+---------+---------+--------------+---------------------------------+
| COPYRIGHTID        | 3      | NUMBER      |           | --      | --      | --           | 版权ID                            |
| BOOKNAME           | 12     | VARCHAR2    |           | --      | --      | --           | 书名，t_cmp_type_bookitem匹配        |
| AUTHORNAME         | 12     | VARCHAR2    |           | --      | --      | --           | 作者姓名,                           |
| AUTHORPENNAME      | 12     | VARCHAR2    |           | --      | --      | --           | 作者笔名                            |
| ANCHORNAME         | 12     | VARCHAR2    |           | --      | --      | --           | 主播姓名                            |
| BOOKTYPE           | 12     | VARCHAR2    |           | --      | --      | --           | 书籍类型---1,图书 2,漫画 3,杂志           |
| MCPID              | 12     | VARCHAR2    |           | --      | --      | --           | MCP的ID,v_mcpinfo中spid匹配         |
| AUTHORIZESTARTDATE | 12     | VARCHAR2    |           | --      | --      | --           | 授权开始日期(yyyy-MM-dd)              |
| AUTHORIZESTOPDATE  | 12     | VARCHAR2    |           | --      | --      | --           | 授权结束日期(yyyy-MM-dd)              |
| SUPPORTWWWFLAG     | 12     | VARCHAR2    |           | --      | 3       | NUMBER       | 是否在WWW门户发布 0：否   1：是            |
| AUTHORIZEFILEPATH  | 12     | VARCHAR2    |           | --      | --      | --           | 授权文件地址                          |
| COPYRIGHTTYPE      | 12     | VARCHAR2    |           | --      | --      | --           | 版权类型---1,专有信息网络传播权 2,无线信息网络传播权  |
| SECONDBOOKTYPE     | 12     | VARCHAR2    |           | --      | --      | --           | 二级内容类型，0、图书，1、期刊杂志，2、移动学习       |
| AUTHORIZEATMOSID   | 12     | VARCHAR2    |           | --      | --      | --           | 该文件在atmos系统的objectid            |
| AUTHORIZEFILENAME  | 12     | VARCHAR2    |           | --      | --      | --           | 文件名                             |
| AUTHORIZEFILESIZE  | 3      | NUMBER      |           | --      | --      | --           | 文件大小                            |
| BOOKID             | 3      | NUMBER      |           | --      | --      | --           | 书ID，匹配之后加上的，有的话表示关联，无的话表示未关联    |
| REFTYPE            | 12     | VARCHAR2    |           | --      | --      | --           | 关联方式                            |
| REFSTATUS          | 12     | VARCHAR2    |           | --      | --      | --           | 0:未关联;1:已关联;2:关联失败              |
| CHECKTIME          | 12     | VARCHAR2    |           | --      | --      | --           | 稽核时间(yyyy-MM-dd HH:mm:ss)       |
| CHECKOPERATORID    | 12     | VARCHAR2    |           | --      | --      | --           | 稽核人员ID                          |
| CHECKREMARK        | 12     | VARCHAR2    |           | --      | --      | --           | 稽核备注                            |
| CHECKSTATUS        | 12     | VARCHAR2    |           | --      | --      | --           | 稽核状态---1,未稽核 2,通过 3,不通过         |
| FREEZEREMARK       | 12     | VARCHAR2    |           | --      | --      | --           | 冻结或解冻备注                         |
| ACCOUNTREMARK      | 12     | VARCHAR2    |           | --      | --      | --           | 结算备注                            |
| ACCOUNTSTOPDATE    | 12     | VARCHAR2    |           | --      | --      | --           | 结算结束日期(yyyy-MM-dd)              |
| ACCOUNTSTARTDATE   | 12     | VARCHAR2    |           | --      | --      | --           | 结算开始日期(yyyy-MM-dd)              |
| ACCOUNTMODIFYTIME  | 12     | VARCHAR2    |           | --      | --      | --           | 结算最后修改时间(yyyy-MM-dd HH:mm:ss)   |
| ACCOUNTTYPE        | 12     | VARCHAR2    |           | --      | --      | --           | 结算类型---1,正常 2,扣减 3,延迟           |
| CREATORID          | 12     | VARCHAR2    |           | --      | --      | --           | 操作员ID，t_bme_operator中operid字段匹配 |
| OPERATETIME        | 12     | VARCHAR2    |           | --      | --      | --           | 操作时间(yyyy-MM-dd HH:mm:ss)       |
| FREEZESTATUS       | 12     | VARCHAR2    |           | --      | --      | --           | 修改之后的状态（1正常, 0 冻结）---0,冻结 1,正常  |
| ALARMSTATUS        | 12     | VARCHAR2    |           |         |         |              |                                 |
| COPYRIGHTSTATUS    | 12     | VARCHAR2    |           |         |         |              |                                 |
| SUBMITDATE         | 93     | DATE        |           |         |         |              |                                 |
| PUBLISHEDCASE      | 12     | VARCHAR2    |           |         |         |              |                                 |
| AUDITPASSDATE      | 93     | DATE        |           |         |         |              |                                 |
| CURRENTOPERATOR    | 12     | VARCHAR2    |           |         |         |              |                                 |
| BATCHID            | 3      | NUMBER      |           |         |         |              |                                 |
+--------------------+--------+-------------+-----------+---------+---------+--------------+---------------------------------+
 */
/**
 * @author zhangtieying
 *
 */
package com.cmread.cmu.css.service.sync.copyright;