/**
 * 

# cmusync ct book_file
+----------------+--------+--------------+-----------+------------+---------+--------------+----------------------+
| F-NAME         | F-TYPE | F-TYPE-NAME  | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT           |
+----------------+--------+--------------+-----------+------------+---------+--------------+----------------------+
| OBJECTID       | 3      | NUMBER       |           | --         | --      | --           | ID                   |
| TYPE           | 12     | VARCHAR2     |           | --         | --      | --           | 类型                   |
| CREATEDATE     | 93     | TIMESTAMP(6) |           | --         | --      | --           | 创建时间                 |
| LASTUPDATEDATE | 93     | TIMESTAMP(6) |           | --         | --      | --           | 最后更新时间               |
| FILEIDX        | 3      | NUMBER       |           | --         | --      | --           | 文件编号                 |
| FILESIZE       | 3      | NUMBER       |           | --         | --      | --           | 文件尺寸                 |
| FILEMIMETYPE   | 12     | VARCHAR2     |           | --         | --      | --           | MIME类型               |
| FILEFORMAT     | 12     | VARCHAR2     |           | --         | --      | --           | 文件格式                 |
| FILESUPPORT    | 12     | VARCHAR2     |           | --         | --      | --           | 文件支持类型               |
| FILEPATH       | 12     | VARCHAR2     |           | --         | --      | --           | 文件路径                 |
| FILEURL        | 12     | VARCHAR2     |           | --         | --      | --           | 文件url                |
| FILEATMOSID    | 12     | VARCHAR2     |           | --         | --      | --           | 该文件在atmos系统的objectid |
| FILESIZEATMOS  | 3      | NUMBER       |           | --         | --      | --           | 文件大小                 |
| FILENAME       | 12     | VARCHAR2     |           | --         | --      | --           | 文件名                  |
|                |        |              |           | OWNEDPLACE | 12      | VARCHAR2     |                      |
+----------------+--------+--------------+-----------+------------+---------+--------------+----------------------+

# cmusync ct book_coverfile
--> From Table : book.t_cmp_type_coverfile
--> To   Table : common.t_cmp_type_coverfile
+----------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+
| F-NAME         | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                   |
+----------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+
| OBJECTID       | 3      | NUMBER      |           | --         | --      | --           | ID                                           |
| SUITTYPE       | 3      | NUMBER      |           | --         | --      | --           | 门户适配类型---1,WAP 2,WWW 3,手机终端 4,终端软件 5,客服 6,其他 |
| IMAGETYPE      | 3      | NUMBER      |           | --         | --      | --           | 图片类型---1,大图 2,中图 3,小图                        |
| HEIGHT         | 3      | NUMBER      |           | --         | --      | --           | 图片高度                                         |
| WIDTH          | 3      | NUMBER      |           | --         | --      | --           | 图片宽度                                         |
| LASTMODIFYTIME | 12     | VARCHAR2    |           | --         | --      | --           | 图片最后修改时间                                     |
|                |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                              |
+----------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+

# cmusync ct book_copyrightfile
--> From Table : book.t_cmp_type_copyrightfile
--> To   Table : common.t_cmp_type_copyrightfile
+--------------+--------+-------------+-----------+------------+---------+--------------+------------+
| F-NAME       | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+--------------+--------+-------------+-----------+------------+---------+--------------+------------+
| OBJECTID     | 3      | NUMBER      |           | --         | --      | --           | 对象id       |
| FILETYPE     | 3      | NUMBER      |           | --         | --      | --           | 文件类型       |
| FILETYPENAME | 12     | VARCHAR2    |           | --         | --      | --           | 版权文件名      |
|              |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |            |
+--------------+--------+-------------+-----------+------------+---------+--------------+------------+


# cmusync ct book_ref_ebookcopyrightfile
--> From Table : book.t_cmp_ref_ebookcopyrightfile
--> To   Table : common.t_cmp_ref_ebookcopyrightfile
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+
| F-NAME    | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+
| SOURCE_ID | 3      | NUMBER      |           | --         | --      | --           | 书籍编号       |
| TARGET_ID | 3      | NUMBER      |           | --         | --      | --           | 版权文件编号     |
|           |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |            |
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+



# cmusync ct book_otherinfo
--> From Table : book.bks_book_otherinfo
--> To   Table : common.con_book_otherinfo
+--------------------+--------+-------------+-----------+------------+---------+--------------+------------+
| F-NAME             | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+--------------------+--------+-------------+-----------+------------+---------+--------------+------------+
| BOOKID             | 12     | VARCHAR2    |           | --         | 3       | NUMBER       | 书籍id       |
| WAP20NOMINATEINFOR | 12     | VARCHAR2    |           | --         | --      | --           | wap2.0推荐语  |
|                    |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |            |
+--------------------+--------+-------------+-----------+------------+---------+--------------+------------+

# cmusync ct book_extrainformation
+-----------------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------+
| F-NAME                | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                      |
+-----------------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------+
| BOOKID                | 3      | NUMBER      |           | --         | 12      | VARCHAR2     | 书项ID                            |
| CHARGEMODE            | 12     | VARCHAR2    |           | --         | --      | --           | 收费方式---0,免费 1,按本 2,按章 3,按字 4,包月 |
| CHARGESERVICEFEENO    | 3      | NUMBER      |           | --         | --      | --           | 试读章节起始序号                        |
| CHARGEINFOFEENO       | 3      | NUMBER      |           | --         | --      | --           | 收费章节起始序号                        |
| SERVICEFEE            | 3      | NUMBER      |           | --         | --      | --           | 服务费                             |
| INFOFEE               | 3      | NUMBER      |           | --         | --      | --           | 信息费                             |
| ISPROMOTION           | 12     | VARCHAR2    |           | --         | --      | --           | 是否促销---0,否 1,是                  |
| ISCHARGE              | 12     | VARCHAR2    |           | --         | --      | --           | 是否付费---0,否 1,是                  |
| MCPBOOKID             | 12     | VARCHAR2    |           | --         | --      | --           | MCP书项ID                         |
| MCPSALEVOLUME         | 12     | VARCHAR2    |           | --         | --      | --           | 销量、漫画杂志的发行量                     |
| MCPCOLLECTION         | 12     | VARCHAR2    |           | --         | --      | --           | 收藏、漫画杂志的下载量                     |
| MCPFRISTISSUE         | 12     | VARCHAR2    |           | --         | --      | --           | MCP首次发布                         |
| MCPCOMMEND            | 12     | VARCHAR2    |           | --         | --      | --           | MCP推荐                           |
| MCPCLICKNUM           | 12     | VARCHAR2    |           | --         | --      | --           | MCP点击数                          |
| MCPOTHER              | 12     | VARCHAR2    |           | --         | --      | --           | 其他、漫画杂志的其他                      |
| MCPRECOMMEND          | 12     | VARCHAR2    |           | --         | --      | --           | 是否强推---0,否 1,是                  |
| RECOMMENDREASON       | 12     | VARCHAR2    |           | --         | --      | --           | 推荐理由                            |
| FIXPRICE              | 12     | VARCHAR2    |           | --         | --      | --           | 固定价格                            |
| MCPCHARGEFEENO        | 3      | NUMBER      |           | --         | --      | --           | MCP计费点                          |
| MCPWORDSCOUNT         | 12     | VARCHAR2    |           | --         | --      | --           | 总字数                             |
| WEAKVERSION           | 12     | VARCHAR2    |           | --         | --      | --           | 弱版式二期：2-是；null值-不是              |
| ISINCLUDEVIDEO        | 12     | VARCHAR2    |           | --         | --      | --           | 是否含有视频：‘0’无；‘1’有。               |
| ISSINGLECHAPTER       | 3      | NUMBER      |           | --         | --      | --           | 表示是否一本一集，1表示一本一集，0表示非一本一集       |
| INNERPOINT            | 3      | NUMBER      |           | --         | --      | --           | 表示计费触发点，一本一集时展示计费触发点，否则为空       |
| COOPERATECOPYRIGHTPIC | 12     | VARCHAR2    |           | --         | --      | --           | 合作图书版权图片路径                      |
|                       |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                 |
+-----------------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------+

# cmusync ct book_chaperpublishmode
--> From Table : book.con_chaperpublishmode
--> To   Table : common.con_chaperpublishmode
+----------------+--------+-------------+-----------+------------+---------+--------------+-----------------------+
| F-NAME         | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT            |
+----------------+--------+-------------+-----------+------------+---------+--------------+-----------------------+
| BOOKID         | 3      | NUMBER      |           | --         | --      | --           | 书项ID                  |
| TYPE           | 12     | VARCHAR2    |           | --         | --      | --           | 发布方式---0,自动 1,手动 2,定时 |
| CREATOR        | 12     | VARCHAR2    |           | --         | --      | --           | 创建者                   |
| CREATETIME     | 12     | VARCHAR2    |           | --         | --      | --           | 创建时间                  |
| DESCRIPTION    | 12     | VARCHAR2    |           | --         | --      | --           | 描述                    |
| NEWCHAPTERFLAG | 12     | VARCHAR2    |           | --         | --      | --           | 新章节标识                 |
| ADVANCEFLAG    | 12     | VARCHAR2    |           | --         | --      | --           | 抢先标识---0,不支持抢先 1,支持抢先 |
| SCANAREA       | 1      | CHAR        |           | --         | --      | --           | 扫描词库---1,全库 2,重点      |
|                |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                       |
+----------------+--------+-------------+-----------+------------+---------+--------------+-----------------------+

cmusync ct book_content
--> From Table : book.t_cmp_type_content
--> To   Table : common.t_cmp_type_content
+----------------+--------+--------------+-----------+------------+---------+--------------+--------------------------------------------------------------------------------------------------------------------------------------------------+
| F-NAME         | F-TYPE | F-TYPE-NAME  | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                                                                                                                       |
+----------------+--------+--------------+-----------+------------+---------+--------------+--------------------------------------------------------------------------------------------------------------------------------------------------+
| OBJECTID       | 3      | NUMBER       |           | --         | --      | --           | ID                                                                                                                                               |
| TYPE           | 12     | VARCHAR2     |           | --         | --      | --           | 书籍内容类型                                                                                                                                           |
| CREATEDATE     | 93     | TIMESTAMP(6) |           | --         | --      | --           | 创建时间                                                                                                                                             |
| LASTUPDATEDATE | 93     | TIMESTAMP(6) |           | --         | --      | --           | 最后更新时间                                                                                                                                           |
| CONTENTCODE    | 12     | VARCHAR2     |           | --         | --      | --           | 书籍内容代码                                                                                                                                           |
| STATUS         | 3      | NUMBER       |           | --         | --      | --           | 书籍内容状态---0,审核中 -1,提交审核中 1,待提交状态 60,待分配 61,待初审 62,待终审 63, 待修改 11,内容制作中 12,待上架 13,上架 17,抢先上架 14,屏蔽状态 16,已下架 77,驳回状态 89,否决状态 99,待删除状态(软) 98,待删除状态(硬) |
| ISCURRENT      | 1      | CHAR         |           | --         | --      | --           | 是否当前值                                                                                                                                            |
| VERSION        | 3      | NUMBER       |           | --         | --      | --           | 版本                                                                                                                                               |
| ACLID          | 3      | NUMBER       |           | --         | --      | --           | 专区id                                                                                                                                             |
| OWNERID        | 12     | VARCHAR2     |           | --         | --      | --           | 拥有者id                                                                                                                                            |
| CREATOR        | 12     | VARCHAR2     |           | --         | --      | --           | 创建者                                                                                                                                              |
| LASTUPDATEUSER | 12     | VARCHAR2     |           | --         | --      | --           | 最后更新的人                                                                                                                                           |
|                |        |              |           | OWNEDPLACE | 12      | VARCHAR2     |                                                                                                                                                  |
+----------------+--------+--------------+-----------+------------+---------+--------------+--------------------------------------------------------------------------------------------------------------------------------------------------+

##### 图书基本信息


  :book_bookitem => {
    :from  => 'book.t_cmp_type_bookitem',
    :to    => 'common.t_cmp_type_bookitem',
    :key   => ['OBJECTID']
  },

 cmusync ct book_bookitem
--> From Table : book.t_cmp_type_bookitem
--> To   Table : common.t_cmp_type_bookitem
+---------------------+--------+-------------+-----------------------------------------+------------+---------+--------------+-----------------------------------------+
| F-NAME              | F-TYPE | F-TYPE-NAME | F-COMMENT                               | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                              |
+---------------------+--------+-------------+-----------------------------------------+------------+---------+--------------+-----------------------------------------+
| OBJECTID            | 3      | NUMBER      | ID                                      | --         | --      | --           | ID                                      |
| NAME                | 12     | VARCHAR2    | 图书名称                                    | --         | --      | --           | 图书名称                                    |
| SEARCHCODE          | 12     | VARCHAR2    | 搜索关键字                                   | --         | --      | --           | 搜索关键字                                   |
| MCPID               | 12     | VARCHAR2    | MCPID                                   | --         | --      | --           | MCPID                                   |
| MCPNAME             | 12     | VARCHAR2    | MCP名称                                   | --         | --      | --           | MCP名称                                   |
| ITEMTYPE            | 12     | VARCHAR2    | 类别---1,图书 2,漫画 3,杂志 5,听书 6,平板杂志 7,报纸    | --         | --      | --           | 类别---1,图书 2,漫画 3,杂志 5,听书 6,平板杂志 7,报纸    |
| WWWDESCRIPTION      | 12     | VARCHAR2    | www的描述说明                                | --         | --      | --           | www的描述说明                                |
| WAPDESCRIPTION      | 12     | VARCHAR2    | wap的描述说明                                | --         | --      | --           | wap的描述说明                                |
| KEYWORDS            | 12     | VARCHAR2    | 关键字                                     | --         | --      | --           | 关键字                                     |
| LASTSTATUS          | 3      | NUMBER      | 上一个状态                                   | --         | --      | --           | 上一个状态                                   |
| LINKCOUNT           | 3      | NUMBER      | 关联目录数                                   | --         | --      | --           | 关联目录数                                   |
| TOTALFEE            | 3      | NUMBER      | 总费用                                     | --         | --      | --           | 总费用                                     |
| SERIESID            | 3      | NUMBER      | 连载id                                    | --         | --      | --           | 连载id                                    |
| CREATETIME          | 12     | VARCHAR2    | 创建时间                                    | --         | --      | --           | 创建时间                                    |
| ONLINETIME          | 12     | VARCHAR2    | 上线时间                                    | --         | --      | --           | 上线时间                                    |
| OFFLINETIME         | 12     | VARCHAR2    | 下线时间                                    | --         | --      | --           | 下线时间                                    |
| ACTUALPRICE         | 3      | NUMBER      | 实体书价格 单位:分                              | --         | --      | --           | 实体书价格 单位:分                              |
| ONSHELFCHAPTERCOUNT | 3      | NUMBER      | 上架章节数目                                  | --         | --      | --           | 上架章节数目                                  |
| ISSN                | 12     | VARCHAR2    | 杂志的品牌编号（期刊号），对应con_magazine_issninfo的主键 | --         | --      | --           | 杂志的品牌编号（期刊号），对应con_magazine_issninfo的主键 |
| LASTOPERATETIME     | 12     | VARCHAR2    | 最后操作时间                                  | --         | --      | --           | 最后操作时间                                  |
| ISBEAUTY            | 12     | VARCHAR2    | 是否精品图书                                  | --         | --      | --           | 是否精品图书                                  |
|                     |        |             |                                         | OWNEDPLACE | 12      | VARCHAR2     |                                         |
+---------------------+--------+-------------+-----------------------------------------+------------+---------+--------------+-----------------------------------------+

  :book_ebook => {
    :from  => 'book.t_cmp_type_ebook',
    :to    => 'common.t_cmp_type_ebook',
    :key   => ['OBJECTID'],
    :ignore => ['AUDITIDEA', 'TESTNEWCOLUMN', 'BOOKLEVEL'],
    :convert_to_string => ['ISSUPPORTPIC']
  },

# cmusync ct book_ebook
--> From Table : book.t_cmp_type_ebook
--> To   Table : common.t_cmp_type_ebook
+----------------------+--------+-------------+---------------------------------------------+---------------+---------+--------------+---------------------------------------------+
| F-NAME               | F-TYPE | F-TYPE-NAME | F-COMMENT                                   | TO-NAME       | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                  |
+----------------------+--------+-------------+---------------------------------------------+---------------+---------+--------------+---------------------------------------------+
| OBJECTID             | 3      | NUMBER      | ID                                          | --            | --      | --           | ID                                          |
| PARTORDER            | 12     | VARCHAR2    | 部件                                          | --            | --      | --           | 部件                                          |
| AUTHORID             | 12     | VARCHAR2    | 作家id                                        | --            | --      | --           | 作家id                                        |
| AUTHORNAME           | 12     | VARCHAR2    | 作家名字                                        | --            | --      | --           | 作家名字                                        |
| CANDOWNLOAD          | 1      | CHAR        | 是否下载---1,是 其他否                              | --            | --      | --           | 是否下载---1,是 其他否                              |
| ISPUBLISH            | 3      | NUMBER      | 是否出版---0,否 1,是                              | --            | --      | --           | 是否出版---0,否 1,是                              |
| PUBLISHER            | 12     | VARCHAR2    | 出版商                                         | --            | --      | --           | 出版商                                         |
| PUBLISHDATE          | 12     | VARCHAR2    | 发布时间                                        | --            | --      | --           | 发布时间                                        |
| ISFRISTISSUE         | 1      | CHAR        | 是否首发---1,是 其他,否                             | --            | --      | --           | 是否首发---1,是 其他,否                             |
| FIRSTPUBLISHDATE     | 12     | VARCHAR2    | 首次发布时间                                      | --            | --      | --           | 首次发布时间                                      |
| ISBN                 | 12     | VARCHAR2    | 国际标准书号                                      | --            | --      | --           | 国际标准书号                                      |
| DEFAULTCATEGORYID    | 12     | VARCHAR2    | 所属专区                                        | --            | --      | --           | 所属专区                                        |
| LASTMODIFIER         | 12     | VARCHAR2    | 最后修改的人                                      | --            | --      | --           | 最后修改的人                                      |
| LASTMODIFYTIME       | 12     | VARCHAR2    | 最后修改时间                                      | --            | --      | --           | 最后修改时间                                      |
| CREATEOR             | 12     | VARCHAR2    | 创建者                                         | --            | --      | --           | 创建者                                         |
| ISSERIAL             | 1      | CHAR        | 是否连载---1,是 0,否                              | --            | --      | --           | 是否连载---1,是 0,否                              |
| SERIALAUDITSTATUS    | 3      | NUMBER      | 连载状态                                        | --            | --      | --           | 连载状态                                        |
| ISFINISH             | 3      | NUMBER      | 是否完结---0,未完本 1,完本                           | --            | --      | --           | 是否完结---0,未完本 1,完本                           |
| CHARGEMODE           | 3      | NUMBER      | 收费方式---0,免费 1,按本 2,按章 3,按字 4,包月             | --            | --      | --           | 收费方式---0,免费 1,按本 2,按
章 3,按字 4,包月             |
| WORDCOUNT            | 3      | NUMBER      | 字数                                          | --            | --      | --           | 字数                                          |
| ISPROMOTION          | 3      | NUMBER      | 是否促销---0,否 1,是                              | --            | --      | --           | 是否促销---0,否 1,是                              |
| LANGUAGEID           | 12     | VARCHAR2    | 语言id                                        | --            | --      | --           | 语言id                                        |
| RECOMMENDREASON      | 12     | VARCHAR2    | 长推荐语                                        | --            | --      | --           | 长推荐语                                        |
| SHORTRECOMMENDREASON | 12     | VARCHAR2    | 短推荐语                                        | --            | --      | --           | 短推荐语                                        |
| ISFORCE              | 1      | CHAR        | 是否强推---1,是 0,否                              | --            | --      | --           | 是否强推---1,是 0,否                              |
| FORCEREASON          | 12     | VARCHAR2    | 强推理由                                        | --            | --      | --           | 强推理由                                        |
| FORCELEVEL           | 3      | NUMBER      | 强推等级                                        | --            | --      | --           | 强推等级                                        |
| SHAPEID              | 12     | VARCHAR2    | 形式编码                                        | --            | --      | --           | 形式编码                                        |
| BOOKURL              | 12     | VARCHAR2    | 书籍url                                       | --            | --      | --           | 书籍url                                       |
| OPENFLAG             | 3      | NUMBER      | 启动键                                         | --            | --      | --           | 启动键                                         |
| GRADE                | 3      | NUMBER      | 作品分级                                        | --            | --      | --           | 作品分级                                        |
| BOOKCLASS            | 3      | NUMBER      | 内容分类                                        | --            | --      | --           | 内容分类                                        |
| STYLE                | 12     | VARCHAR2    | 风格分类                                        | --            | --      | --           | 风格分类                                        |
| CLASSSUGGEST         | 12     | VARCHAR2    | 内容分类建议                                      | --            | --      | --           | 内容分类建议                                      |
| BATCHNUM             | 12     | VARCHAR2    | 批次Id                                        | --            | --      | --           | 批次Id                                        |
| FIRSTAUDITBATCHID    | 3      | NUMBER      | 首批上次审核批次                                    | --            | --      | --           | 首批上次审核批次                                    |
| LEGALCODE            | 12     | VARCHAR2    | 出版物法定编号                                     | --            | --      | --           | 出版物法定编号                                     |
| COPYRIGHTBEGINDATE   | 12     | VARCHAR2    | 版权开始日期                                      | --            | --      | --           | 版权开始日期                                      |
| COPYRIGHTENDDATE     | 12     | VARCHAR2    | 版权截止日期                                      | --            | --      | --           | 版权截止日期                                      |
| ACCREDITAREA         | 12     | VARCHAR2    | 授权地区                                        | --            | --      | --           | 授权地区                                        |
| ACCREDITTYPE         | 12     | VARCHAR2    | 授权类别                                        | --            | --      | --           | 授权类别                                        |
| PRINCIPAL            | 12     | VARCHAR2    | 内容引入负责人                                     | --            | --      | --           | 内容引入负责人                                     |
| UNIQUEFLAGE          | 1      | CHAR        | 是否专用---0,否 1,是                              | --            | --      | --           | 是否专用---0,否 1,是                              |
| VOICEFLAG            | 1      | CHAR        | 设置是否有音频---1,是 0,否                           | --            | --      | --           | 设置是否有音频---1,是 0,否                           |
| RECOMMENDLEVEL       | 12     | VARCHAR2    | 设置内容推荐级别                                    | --            | --      | --           | 设置内容推荐级别                                    |
| LASTSERIALTIME       | 12     | VARCHAR2    | 连载更新时间                                      | --            | --      | --           | 连载更新时间                                      |
| SUPPORTWWWFLAG       | 3      | NUMBER      | 是否支持www---0,不支持 1,支持                        | --            | --      | --           | 是否支持www---0,不支持 1,支持                        |
| ONSHELFWORDCOUNT     | 3      | NUMBER      | 上架字数                                        | --            | --      | --           | 上架字数                                        |
| INFOFEECHAPTERID     | 3      | NUMBER      | 起始收费章节ID                                    | --            | --      | --           | 起始收费章节ID                                    |
| FASCICULESTATE       | 3      | NUMBER      | 图书分册状态---0,未分册 1,有分册 2,分册停用                 | --            | --      | --           | 图书分册状态---0,未分册 1, 有分册 2,分册停用                 |
| ISSUPPORTPIC         | 3      | NUMBER      | 是否支持图文混排---1,是 0,否                          | --            | 12      | VARCHAR2     | 是否支持图文混排---1,是 0,否                          |
| TRADEFLAG            | 12     | VARCHAR2    | 行业标识符号---0,行业非行业公用 -1,默认非行业 2,仅行业           | --            | --      | --           | 行业标识符号---0,行业 非行业公用 -1,默认非行业 2,仅行业           |
| FORMATTYPEID         | 3      | NUMBER      | 图书版式分类类型                                    | --            | --      | --           | 图书版式分类类型                                    |
| READERID             | 12     | VARCHAR2    | 读者id                                        | --            | --      | --           | 读者id                                        |
| READERNAME           | 12     | VARCHAR2    | 本字段已废弃，请关联主播表CON_AUDIOBOOK_ANCHORINFO查询主播名称 | --            | --      | --           | 本字段已废弃，请关联主播表CON_AUDIOBOOK_ANCHORINFO查询主播名称 |
| TOTALDURATION        | 3      | NUMBER      | 时长                                          | --            | --      | --           | 时长                                          |
| SCORE                | 3      | NUMBER      | 作品评分                                        | --            | --      | --           | 作品评分                                        |
| ONSHELFDURATION      | 3      | NUMBER      | 听书上架时长                                      | --            | --      | --           | 听书上架时长                                      |
| PARTNERIDENTITY      | 12     | VARCHAR2    | 用于区分是否为“新浪专有图书”---0,不是 1,是                  | --            | --      | --           | 用于区分是否为“新浪专有图书”---0,不是 1,是                  |
| SHOWWORDCOUNT        | 3      | NUMBER      | 展示字数                                        | --            | --      | --           | 展示字数                                        |
| SUPPORTPIC           | 3      | NUMBER      | 是否支持在线看图---1,不支持 2,支持                       | --            | --      | --           | 是否支持在线看图---1,不支持 2,支持                       |
| SUPPORTAPP           | 3      | NUMBER      | 是否支持单机包---1,不支持 2,支持                        | --            | --      | --           | 是否支持单机包---1,不支持 2,支 持                        |
| RESOURCETYPE         | 12     | VARCHAR2    | 漫画资源类型(1：传统漫画2：美图漫画)                        | --            | --      | --           | 漫画资源类型(1：传统漫画2：美图漫画)                        |
| COMMONLABELID        | 12     | VARCHAR2    | 通用标签                                        | --            | --      | --           | 通用标签                                        |
| ISCOOPERATE          | 12     | VARCHAR2    | 是否合作图书 1：yes 其他：no                          | --            | --      | --           | 是否合作图书 1：yes 其他：no                          |
| AUDITIDEA            | 12     | VARCHAR2    |                                             | --            | --      | --           |                                             |
|                      |        |             |                                             | TESTNEWCOLUMN | 12      | VARCHAR2     |                                             |
|                      |        |             |                                             | BOOKLEVEL     | 3       | NUMBER       | 图书级别                                        |
|                      |        |             |                                             | OWNEDPLACE    | 12      | VARCHAR2     |                                             |
+----------------------+--------+-------------+---------------------------------------------+---------------+---------+--------------+---------------------------------------------+


  # 图书封面信息同步(2) 这个表依赖t_cmp_type_coverfile和t_cmp_type_ebook
  # 注意这个表配置的位置，必须在上面两个表下面
  :book_ebookcoverfile => {
    :from  => 'book.t_cmp_ref_ebookcoverfile',
    :to    => 'common.t_cmp_ref_ebookcoverfile',
    :key   => ['SOURCE_ID', 'TARGET_ID'],
    :ignore => ['ID']
  }, # TODO 有依赖关系，前面测试失败，需要重新测试

# cmusync ct book_ebookcoverfile
--> From Table : book.t_cmp_ref_ebookcoverfile
--> To   Table : common.t_cmp_ref_ebookcoverfile
+-----------+--------+-------------+-----------+---------+---------+--------------+------------+
| F-NAME    | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+-----------+--------+-------------+-----------+---------+---------+--------------+------------+
| ID        | 3      | NUMBER      |           | --      | --      | --           |            |
| SOURCE_ID | 3      | NUMBER      |           | --      | --      | --           | 书籍编号编号     |
| TARGET_ID | 3      | NUMBER      |           | --      | --      | --           | 封面图片编号     |
+-----------+--------+-------------+-----------+---------+---------+--------------+------------+



####### 图书编审信息


  # 这个同步涉及到反向状态更新！！！
  :book_auditbookinfo => {
    :from  => 'book.con_auditbookinfo',
    :to    => 'common.con_auditbookinfo',
    :key   => ['BOOKID'],
    :reverse_conf => {
        "BOOKSTATUS" => [12, 13, 14, 16, 17]
    }
  },

#cmusync ct book_auditbookinfo
--> From Table : book.con_auditbookinfo
--> To   Table : common.con_auditbookinfo
+------------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------+
| F-NAME                 | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                       |
+------------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------+
| BOOKID                 | 3      | NUMBER      |           | --         | --      | --           |                                                  |
| BOOKNAME               | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| MCPID                  | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| MCPNAME                | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| AUTHORID               | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| PENNAME                | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| WAPDESCRIPTION         | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| PUBLISHFLAG            | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| PUBLISHER              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| ISBN                   | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| SERIALFLAG             | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| FORCEFLAG              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| FORCEREASON            | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| BOOKSTATUS             | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| BOOKCLASSID            | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| BOOKSERIESID           | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| KEYWORD                | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| RECOMMENDREASON        | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| BOOKUPLOADTIME         | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| FORCELEVEL             | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| WWWDESCRIPTION         | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| DOWNLOADFLAG           | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| PARTORDER              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| LANGUAGEID             | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| LEGALCODE              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| COPYRIGHTBEGINDATE     | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| COPYRIGHTENDDATE       | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| ACCREDITTYPE           | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| ACCREDITAREA           | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| COPYRIGHTSTATUS        | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| PUBLISHTIME            | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| FIRSTISSUEFLAG         | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| SHAPEID                | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| OPENFLAG               | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| UPLOADBATCHID          | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| LASTSUBMITTIME         | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| WORDCOUNT              | 3      | NUMBER      |           | --         | --      | --           |                                                  |
| BOOKURL                | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| ISFINISH               | 12     | VARCHAR2    |           | --         | --      | --           | 是否完本 0：否 1：是                                     |
| SHORTRECOMMENDREASON   | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| STORETIME              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| AUDITRECOMMENDREASON   | 12     | VARCHAR2    |           | --         | --      | --           | 推荐理由                                             |
| SUBMITCOUNT            | 3      | NUMBER      |           | --         | --      | --           |                                                  |
| ISDIRECTORYCHANGE      | 12     | VARCHAR2    |           | --         | --      | --           | 目录是否变更  0：否   1：是                                |
| WAITONSHELFCOUNT       | 3      | NUMBER      |           | --         | --      | --           | 可上架章节数                                           |
| BOOKTYPE               | 3      | NUMBER      |           | --         | --      | --           | 书籍类型 1:图书,2:漫画,3:杂志                              |
| RECOMMENDLEVEL         | 3      | NUMBER      |           | --         | --      | --           | 推荐位级别 0:不推荐,1:1级推荐位,2:2级推荐位,3:3级推荐位              |
| VOICEFLAG              | 12     | VARCHAR2    |           | --         | --      | --           |                                                  |
| NEWCHAPTERCOUNT        | 3      | NUMBER      |           | --         | --      | --           | 漫画一个系列下处于草稿状态的新章节个数，初始为1，提交审核后改为0，新增同系列漫画，加一     |
| LASTONSHELFCHAPTERNAME | 12     | VARCHAR2    |           | --         | --      | --           | 最后更新的上架章节                                        |
| LASTONSHELFCHAPTERTIME | 12     | VARCHAR2    |           | --         | --      | --           | 最后上架章节的更新时间                                      |
| LASTUPLOADCHAPTERNAME  | 12     | VARCHAR2    |           | --         | --      | --           | 最后更新的续传章节                                        |
| LASTUPLOADCHAPTERTIME  | 12     | VARCHAR2    |           | --         | --      | --           | 最后续传章节的更新时间                                      |
| ISFIRST                | 12     | VARCHAR2    |           | --         | --      | --           | 是否抢先                                             |
| PRIORITY               | 12     | VARCHAR2    |           | --         | --      | --           | 图书优先级                                            |
| BOOKLISTLEVEL          | 12     | VARCHAR2    |           | --         | --      | --           | 特殊书单等级                                           |
| ONSHELFWORDCOUNT       | 3      | NUMBER      |           | --         | --      | --           | 已上架章节的总字数                                        |
| SUPPORTWWWFLAG         | 3      | NUMBER      |           | --         | --      | --           | 是否在WWW门户发布 0：否   1：是                             |
| FORBIDTYPE             | 3      | NUMBER      |           | --         | --      | --           | 下架书籍的禁止标记.0未禁止，1禁止                               |
| UNIQUEFLAGE            | 1      | CHAR        |           | --         | --      | --           | 版权信息:是否专用                                        |
| ACTUALPRICE            | 3      | NUMBER      |           | --         | --      | --           | 实体书价格 单位:分                                       |
| ISSUPPORTPIC           | 12     | VARCHAR2    |           | --         | --      | --           | 是否支持图文混排                                         |
| PLATFORM               | 12     | VARCHAR2    |           | --         | --      | --           | 来自的平台名称                                          |
| INSTORECHAPTER4CAR     | 3      | NUMBER      |           | --         | --      | --           | 漫画已入库章节数                                         |
| TATOLCHAPTER4CAR       | 3      | NUMBER      |           | --         | --      | --           | 漫画总章节数                                           |
| FORMATTYPEID           | 3      | NUMBER      |           | --         | --      | --           | 图书版式分类类型                                         |
| QATIMES                | 3      | NUMBER      |           | --         | --      | --           | 质检次数                                             |
| GROUPID                | 12     | VARCHAR2    |           | --         | --      | --           | 图书审核员所属组                                         |
| GRADE                  | 3      | NUMBER      |           | --         | --      | --           |                                                  |
| DRAFTCHAPTERNUMBER     | 3      | NUMBER      |           | --         | --      | --           | 草稿章节数，用于mcp组员书单草稿列表，每天5点从t_cmp_type_chapter表统计过来 |
| COPYRIGHTRELATESTATUS  | 12     | VARCHAR2    |           | --         | --      | --           | 0:否;1:是                                          |
| SIMILIARSTATUS         | 3      | NUMBER      |           | --         | --      | --           | 0:默认值;1:检查中;2:检查完成;3:检查请求失败;4:检查响应失败             |
| TOTALDURATION          | 3      | NUMBER      |           | --         | --      | --           | 听书总时长                                            |
| ONSHELFDURATION        | 3      | NUMBER      |           | --         | --      | --           | 听书上架时长                                           |
| WAITONSHELFDURATION    | 3      | NUMBER      |           | --         | --      | --           | 听书待上架时长                                          |
| READERID               | 12     | VARCHAR2    |           | --         | --      | --           | 听书主播id                                           |
| ISSINAPRIVATE          | 12     | VARCHAR2    |           | --         | --      | --           | 用于区分是否为“新浪专有图书”, "1"表示是，“0”表示不是                  |
| LASTMODIFYTIME         | 93     | DATE        |           | --         | --      | --           |                                                  |
| AUTOSTOREFLAG          | 3      | NUMBER      |           | --         | --      | --           | 自动入库标示：1是，其他否                                    |
| COMMONLABELID          | 12     | VARCHAR2    |           | --         | --      | --           | 通用标签                                             |
|                        |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                                  |
+------------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------+


  :book_auditbookinfocomp => {
    :from  => 'book.bks_auditbookinfocomp',
    :to    => 'common.con_auditbookinfocomp',
    :key   => ['AUDITBATCHID', 'BOOKID'],
    :ignore => ['ID', 'UNIQUEFLAGE']  # UNIQUEFLAGE是char类型，比较奇怪，select * 没有返回，按时忽略
  },

#cmusync ct book_auditbookinfocomp
--> From Table : book.bks_auditbookinfocomp
--> To   Table : common.con_auditbookinfocomp
+------------------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+
| F-NAME                 | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                   |
+------------------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+
| ID                     | 3      | NUMBER      |           |            |         |              |                                              |
| BOOKTYPE               | 3      | NUMBER      |           | --         | --      | --           | 书籍类型---1,图书 2,漫画 3,杂志                        |
| PUBLISHFLAG            | 12     | VARCHAR2    |           | --         | --      | --           | 出版标记 0表示未出版；1表示出版                            |
| SERIALFLAG             | 12     | VARCHAR2    |           | --         | --      | --           | 连载标识                                         |
| FORCEFLAG              | 12     | VARCHAR2    |           | --         | --      | --           | 强推标识                                         |
| DOWNLOADFLAG           | 12     | VARCHAR2    |           | --         | --      | --           | 下载标识                                         |
| ISFINISH               | 12     | VARCHAR2    |           | --         | --      | --           | 是否完本---0,否 1,是                               |
| FIRSTISSUEFLAG         | 12     | VARCHAR2    |           | --         | --      | --           | 首版标识                                         |
| OPENFLAG               | 12     | VARCHAR2    |           | --         | --      | --           | 不良信息关键字搜索启动键                                 |
| GRADE                  | 3      | NUMBER      |           | --         | --      | --           | 评级                                           |
| RECOMMENDLEVEL         | 3      | NUMBER      |           | --         | --      | --           | 推荐位级别 0:不推荐,1:1级推荐位,2:2级推荐位,3:3级推荐位          |
| VOICEFLAG              | 12     | VARCHAR2    |           | --         | --      | --           | 有声读物标识                                       |
| ISDIRECTORYCHANGE      | 12     | VARCHAR2    |           | --         | --      | --           | 目录是否变更---0,否 1,是                             |
| SUPPORTWWWFLAG         | 3      | NUMBER      |           | --         | --      | --           | 是否在WWW门户发布---0,否 1,是                         |
| UNIQUEFLAGE            | 12     | VARCHAR2    |           | --         | 1       | CHAR         | 版权信息:是否专用                                    |
| FORCELEVEL             | 12     | VARCHAR2    |           | --         | --      | --           | 强推级别                                         |
| ISSUPPORTPIC           | 12     | VARCHAR2    |           | --         | --      | --           | 是否支持图文混排                                     |
| BOOKSTATUS             | 12     | VARCHAR2    |           | --         | --      | --           | 图书状态                                         |
| COPYRIGHTSTATUS        | 12     | VARCHAR2    |           | --         | --      | --           | 版权状态                                         |
| ISFIRST                | 12     | VARCHAR2    |           | --         | --      | --           | 是否抢先                                         |
| FORMATTYPEID           | 3      | NUMBER      |           | --         | --      | --           | 图书版式分类类型 1:女频,2:出版,3:原创                      |
| QATIMES                | 3      | NUMBER      |           | --         | --      | --           | 质检次数                                         |
| BOOKCLASSID            | 12     | VARCHAR2    |           | --         | --      | --           | 图书分类ID                                       |
| ACCREDITTYPE           | 12     | VARCHAR2    |           | --         | --      | --           | 授权类型                                         |
| BOOKSERIESID           | 12     | VARCHAR2    |           | --         | --      | --           | 图书系列ID                                       |
| PARTORDER              | 12     | VARCHAR2    |           | --         | --      | --           | 书部                                           |
| MCPID                  | 12     | VARCHAR2    |           | --         | --      | --           | MCPID                                        |
| SUBMITCOUNT            | 3      | NUMBER      |           | --         | --      | --           | 提交次数                                         |
| WAITONSHELFCOUNT       | 3      | NUMBER      |           | --         | --      | --           | 可上架章节数                                       |
| NEWCHAPTERCOUNT        | 3      | NUMBER      |           | --         | --      | --           | 漫画一个系列下处于草稿状态的新章节个数，初始为1，提交审核后改为0，新增同系列漫画，加一 |
| ACTUALPRICE            | 3      | NUMBER      |           | --         | --      | --           | 实体书价格 单位:分                                   |
| WORDCOUNT              | 3      | NUMBER      |           | --         | --      | --           | 字数                                           |
| ONSHELFWORDCOUNT       | 3      | NUMBER      |           | --         | --      | --           | 已上架章节的总字数                                    |
| INSTORECHAPTER4CAR     | 3      | NUMBER      |           | --         | --      | --           | 漫画已入库章节数                                     |
| TATOLCHAPTER4CAR       | 3      | NUMBER      |           | --         | --      | --           | 漫画总章节数                                       |
| BOOKUPLOADTIME         | 12     | VARCHAR2    |           | --         | --      | --           | 更新时间                                         |
| COPYRIGHTBEGINDATE     | 12     | VARCHAR2    |           | --         | --      | --           | 版权开始时间                                       |
| LASTONSHELFCHAPTERTIME | 12     | VARCHAR2    |           | --         | --      | --           | 最后上架章节的更新时间                                  |
| COPYRIGHTENDDATE       | 12     | VARCHAR2    |           | --         | --      | --           | 版权结束时间                                       |
| SHAPEID                | 12     | VARCHAR2    |           | --         | --      | --           | 形式标识ID                                       |
| LASTSUBMITTIME         | 12     | VARCHAR2    |           | --         | --      | --           | 最后提交时间                                       |
| PUBLISHTIME            | 12     | VARCHAR2    |           | --         | --      | --           | 出版时间                                         |
| STORETIME              | 12     | VARCHAR2    |           | --         | --      | --           | 存储时间                                         |
| LASTUPLOADCHAPTERTIME  | 12     | VARCHAR2    |           | --         | --      | --           | 最后续传章节的更新时间                                  |
| SHORTRECOMMENDREASON   | 12     | VARCHAR2    |           | --         | --      | --           | 短推荐语                                         |
| BOOKID                 | 3      | NUMBER      |           | --         | --      | --           | 书项ID                                         |
| AUTHORID               | 12     | VARCHAR2    |           | --         | --      | --           | 作家ID                                         |
| LEGALCODE              | 12     | VARCHAR2    |           | --         | --      | --           | 法律编码                                         |
| UPLOADBATCHID          | 12     | VARCHAR2    |           | --         | --      | --           | 更新批次ID                                       |
| BOOKLISTLEVEL          | 12     | VARCHAR2    |           | --         | --      | --           | 特殊书单等级                                       |
| PRIORITY               | 12     | VARCHAR2    |           | --         | --      | --           | 图书优先级                                        |
| FORBIDTYPE             | 3      | NUMBER      |           | --         | --      | --           | 下架书籍的禁止标记.---0,未禁止 1,禁止                      |
| PENNAME                | 12     | VARCHAR2    |           | --         | --      | --           | 作家笔名                                         |
| PLATFORM               | 12     | VARCHAR2    |           | --         | --      | --           | 来自的平台名称                                      |
| ISBN                   | 12     | VARCHAR2    |           | --         | --      | --           | 国际标准图书编号                                     |
| MCPNAME                | 12     | VARCHAR2    |           | --         | --      | --           | MPC名称                                        |
| LANGUAGEID             | 12     | VARCHAR2    |           | --         | --      | --           | 语言ID                                         |
| ACCREDITAREA           | 12     | VARCHAR2    |           | --         | --      | --           | 授权地区                                         |
| BOOKNAME               | 12     | VARCHAR2    |           | --         | --      | --           | 书项名称                                         |
| PUBLISHER              | 12     | VARCHAR2    |           | --         | --      | --           | 出版者                                          |
| KEYWORD                | 12     | VARCHAR2    |           | --         | --      | --           | 关键字                                          |
| LASTONSHELFCHAPTERNAME | 12     | VARCHAR2    |           | --         | --      | --           | 最后更新的上架章节                                    |
| LASTUPLOADCHAPTERNAME  | 12     | VARCHAR2    |           | --         | --      | --           | 最后更新的续传章节                                    |
| BOOKURL                | 12     | VARCHAR2    |           | --         | --      | --           | 图书URL地址                                      |
| AUDITRECOMMENDREASON   | 12     | VARCHAR2    |           | --         | --      | --           | 推荐理由                                         |
| WAPDESCRIPTION         | 12     | VARCHAR2    |           | --         | --      | --           | Wap描述                                        |
| FORCEREASON            | 12     | VARCHAR2    |           | --         | --      | --           | 强推理由                                         |
| RECOMMENDREASON        | 12     | VARCHAR2    |           | --         | --      | --           | 推荐理由                                         |
| WWWDESCRIPTION         | 12     | VARCHAR2    |           | --         | --      | --           | WWW注释                                        |
| FIRST_CHECK_TIMES      | 3      | NUMBER      |           | --         | --      | --           | 第几次进入初审，默认值1                                 |
| FINAL_CHECK_TIMES      | 3      | NUMBER      |           | --         | --      | --           | 第几次进入终审，默认值1                                 |
| AUDITBATCHID           | 3      | NUMBER      |           | --         | --      | --           | 批次ID                                         |
| BATCHWORDCOUNT         | 3      | NUMBER      |           | --         | --      | --           | 当前批次总字数                                      |
| CHARGEMODE             | 12     | VARCHAR2    |           | --         | --      | --           | 收费方式---0,免费 1,按本 2,按章 3,按字 4,包月              |
| MCPCHARGEFEENO         | 3      | NUMBER      |           | --         | --      | --           | MCP计费点                                       |
| CHARGEINFOFEENO        | 3      | NUMBER      |           | --         | --      | --           | 平台收费点                                        |
| INFOFEE                | 3      | NUMBER      |           | --         | --      | --           | 信息费                                          |
| ISPROMOTION            | 12     | VARCHAR2    |           | --         | --      | --           | 是否促销---0,不促销 1,促销                            |
| ISCHARGE               | 12     | VARCHAR2    |           | --         | --      | --           | 是否收费---0,否 1,是                               |
| FIRSTBATCHFLAG         | 3      | NUMBER      |           | --         | --      | --           | 首发首批, 1首批；其他非首批                              |
| REJECTEDOPTION         | 12     | VARCHAR2    |           | --         | --      | --           | 驳回选项---0,封面 1,版权 2,基本信息 3,价格 4,内容            |
| READERID               | 12     | VARCHAR2    |           | --         | --      | --           | 听书主播id                                       |
|                        |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                              |
+------------------------+--------+-------------+-----------+------------+---------+--------------+----------------------------------------------+

  :book_auditbatchinfo => {
    :from  => 'book.bks_auditbatchinfo',
    :to    => 'common.con_auditbatchinfo',
    :key   => ['AUDITBATCHID'],
    :ignore => ['PROCESSNAME'],
    :reverse_conf => {
        "AUDITBATCHSTATUS" => [12, 13, 14, 16, 17]
    }
  },
  

#cmusync ct book_auditbatchinfo
--> From Table : book.bks_auditbatchinfo
--> To   Table : common.con_auditbatchinfo
+---------------------+--------+-------------+-----------+------------+---------+--------------+-----------------------------+
| F-NAME              | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                  |
+---------------------+--------+-------------+-----------+------------+---------+--------------+-----------------------------+
| AUDITBATCHID        | 3      | NUMBER      |           | --         | --      | --           | 审批批次ID                      |
| BOOKID              | 3      | NUMBER      |           | --         | --      | --           | 图书ID                        |
| BEGINCHAPTERID      | 3      | NUMBER      |           | --         | --      | --           | 开始章节ID                      |
| ENDCHAPTERID        | 3      | NUMBER      |           | --         | --      | --           | 结束章节ID                      |
| AUDITBATCHSTATUS    | 12     | VARCHAR2    |           | --         | --      | --           | 状态                          |
| UPLOADTIME          | 12     | VARCHAR2    |           | --         | --      | --           | 上传时间，格式YYYYMMDDHH24MISS     |
| BADINFOFLAG         | 12     | VARCHAR2    |           | --         | --      | --           | 不良信息标识                      |
| BADINFOSERACHRESULT | 12     | VARCHAR2    |           | --         | --      | --           | 不良信息搜索结果                    |
| FIRSTBATCHFLAG      | 3      | NUMBER      |           | --         | --      | --           | 是否首批                        |
| SUBMITAUDITTIME     | 12     | VARCHAR2    |           | --         | --      | --           | 提交审核时间                      |
| PROCESSID           | 12     | VARCHAR2    |           | --         | --      | --           | 处理活动ID                      |
| OPERATESTEP         | 12     | VARCHAR2    |           | --         | --      | --           | 审核步骤                        |
| PARTICIPANTID       | 12     | VARCHAR2    |           | --         | --      | --           | 参与者ID                       |
| CLAIMEDUSERID       | 12     | VARCHAR2    |           | --         | --      | --           | 声明用户ID                      |
| MARKEDUSERID        | 12     | VARCHAR2    |           | --         | --      | --           | 标记用户ID                      |
| PARTICIPANTTYPE     | 3      | NUMBER      |           | --         | --      | --           | 参与者类型                       |
| PROCESSSTATE        | 3      | NUMBER      |           | --         | --      | --           | 处理状态                        |
| WORKITEMID          | 12     | VARCHAR2    |           | --         | --      | --           | 工作项ID                       |
| STARTTIME           | 12     | VARCHAR2    |           | --         | --      | --           | 开始时间                        |
| BASETEMPLATEID      | 3      | NUMBER      |           | --         | --      | --           | 模板ID                        |
| AUTOPOSTIL          | 12     | VARCHAR2    |           | --         | --      | --           | 自动批注模式---0,常规模式 1,批注模式 2,老书 |
| BEGINDIRECTORYNAME  | 12     | VARCHAR2    |           | --         | --      | --           | 起始目录名称：卷名称-章节名称             |
| ENDDIRECTORYNAME    | 12     | VARCHAR2    |           | --         | --      | --           | 终止目录名称：卷名称-章节名称             |
| COPYRIGHTSNUM       | 12     | VARCHAR2    |           | --         | --      | --           | 版权号                         |
| FLOWSTATU           | 12     | VARCHAR2    |           | --         | --      | --           | 工作流状态                       |
| REJECTLEVEL         | 3      | NUMBER      |           | --         | --      | --           | 驳回等级---0,紧急驳回 1,普通驳回        |
| WFTEMPLETID         | 3      | NUMBER      |           | --         | --      | --           | 工作流模板ID                     |
| BATCHWORDCOUNT      | 3      | NUMBER      |           | --         | --      | --           | 批次统计字数                      |
| LASTAUDITTACHE      | 12     | VARCHAR2    |           | --         | --      | --           | 上一个审核环节                     |
| ADDCHAPTERFLAG      | 12     | VARCHAR2    |           | --         | --      | --           | 是否允许CP新增章节---0,不允许 1,允许     |
| DELCHAPTERFLAG      | 12     | VARCHAR2    |           | --         | --      | --           | 是否允许CP删除章节---0,不允许 1,允许     |
| BATCHDURATION       | 3      | NUMBER      |           | --         | --      | --           | 听书批次时长                      |
| WARNFLAG            | 12     | VARCHAR2    |           | --         | --      | --           | 预警标示：1是，其他否                 |
| PROCESSNAME         | 12     | VARCHAR2    |           |            |         |              |                             |
|                     |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                             |
+---------------------+--------+-------------+-----------+------------+---------+--------------+-----------------------------+
  
  # 分册信息同步

  :book_fascicule_info => {
    :from  => 'book.con_fascicule_info',
    :to    => 'common.con_fascicule_info',
    :key   => ['FASCICULEID']
  },  # TODO 没数据，未测试 表结构一致

#cmusync ct book_fascicule_info
--> From Table : book.con_fascicule_info
--> To   Table : common.con_fascicule_info
+-------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------------------------+
| F-NAME            | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                                         |
+-------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------------------------+
| FASCICULEID       | 12     | VARCHAR2    |           | --         | --      | --           | 分册ID                                                               |
| FASCICULENAME     | 12     | VARCHAR2    |           | --         | --      | --           | 分册名称                                                               |
| BOOKID            | 3      | NUMBER      |           | --         | --      | --           | 分册图书ID                                                             |
| STARTCHAPTERID    | 3      | NUMBER      |           | --         | --      | --           | 起始章节ID                                                             |
| STARTCHAPTERORDER | 3      | NUMBER      |           | --         | --      | --           | 起始章节order                                                          |
| STARTCHAPTERNAME  | 12     | VARCHAR2    |           | --         | --      | --           | 起始章节名称                                                             |
| ENDCHAPTERID      | 3      | NUMBER      |           | --         | --      | --           | 结束章节ID                                                             |
| ENDCHAPTERORDER   | 3      | NUMBER      |           | --         | --      | --           | 结束章节order                                                          |
| ENDCHAPTERNAME    | 12     | VARCHAR2    |           | --         | --      | --           | 结束章节名称                                                             |
| WORDCOUNT         | 3      | NUMBER      |           | --         | --      | --           | 字数                                                                 |
| CHAPTERCOUNT      | 3      | NUMBER      |           | --         | --      | --           | 章节数                                                                |
| DISPLAYORDER      | 3      | NUMBER      |           | --         | --      | --           | 展示顺序                                                               |
| DISPLAYSTATE      | 3      | NUMBER      |           | --         | --      | --           | 展示状态,0不展示,1展示---0,不展示 1,展示                                         |
| AUDITSTATE        | 3      | NUMBER      |           | --         | --      | --           | 审核状态,1 草稿, 2 审核中,3 驳回,4 审核完成---1,卷名修改 2,章节名修改 3,章节内容修改 4,书名修改      |
| PRODUCTID         | 12     | VARCHAR2    |           | --         | --      | --           | 产品ID                                                               |
| PRICE             | 3      | NUMBER      |           | --         | --      | --           | 价格                                                                 |
| CREATETIME        | 12     | VARCHAR2    |           | --         | --      | --           | 创建时间                                                               |
| CREATOR           | 12     | VARCHAR2    |           | --         | --      | --           | 创建人                                                                |
| MEBURL            | 12     | VARCHAR2    |           | --         | --      | --           | meb文件的存放目录：如果是图文混排，则将不同分辨率meb文件的存放目录以分号隔开记录                        |
| MEBSIZE           | 3      | NUMBER      |           | --         | --      | --           | meb文件的大小（单位：Byte）                                                  |
| PACKAGESTATUS     | 12     | VARCHAR2    |           | --         | --      | --           | 打包状态(2：打包中，3：打包成功，4：打包失败 ，10: 放弃重试)---2,打包中 3,打包成功 4,打包失败 ,10:放弃重试 |
| LASTPACKTIME      | 93     | DATE        |           | --         | --      | --           | meb打包时间                                                            |
|                   |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                                                    |
+-------------------+--------+-------------+-----------+------------+---------+--------------+--------------------------------------------------------------------+


  :book_fascicule => {
    :from  => 'book.bks_book_fascicule',
    :to    => 'common.con_book_fascicule',
    :key   => ['BOOKID'],  # TODO 待确认
  }, # TODO 没数据，未测试

#cmusync ct book_fascicule     
--> From Table : book.bks_book_fascicule
--> To   Table : common.con_book_fascicule
+--------------------+--------+-------------+---------------------------------+------------+---------+--------------+------------------------------------+
| F-NAME             | F-TYPE | F-TYPE-NAME | F-COMMENT                       | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                         |
+--------------------+--------+-------------+---------------------------------+------------+---------+--------------+------------------------------------+
| BOOKID             | 3      | NUMBER      | 图书ID                            | --         | --      | --           | 图书ID                               |
| TOTALFASCICULE     | 3      | NUMBER      | 分册总数                            | --         | --      | --           | 分册总数                               |
| DRAFTCOUNT         | 3      | NUMBER      | 草稿数                             | --         | --      | --           | 草稿数                                |
| WAITAUDITCOUNT     | 3      | NUMBER      | 待审核                             | --         | --      | --           | 待审数                                |
| REJECTCOUNT        | 3      | NUMBER      | 驳回数                             | --         | --      | --           | 驳回数                                |
| AUDITPRICESTATE    | 3      | NUMBER      | 价格修改审核状态                        | --         | --      | --           | 价格修改审核状态                           |
| STARTENDSTATE      | 3      | NUMBER      | 启停操作审核状态                        | --         | --      | --           | 启停操作审核状态                           |
| STARTENDFLAG       | 3      | NUMBER      | 启停标识                            | --         | --      | --           | 启停标识                               |
| PASSCOUNT          | 3      | NUMBER      | 通过数                             | --         | --      | --           | 通过数                                |
| FASCICULEMODE      | 3      | NUMBER      | 分册方式---1,按章节数 2,按字数             | --         | --      | --           | 分册方式---1,按章节数 2,按字数                |
| CHAPTERCOUNT       | 3      | NUMBER      | 每册章节数                           | --         | --      | --           | 每册章节数                              |
| WORDCOUNT          | 3      | NUMBER      | 每册字数                            | --         | --      | --           | 每册字数                               |
| PRICE              | 3      | NUMBER      | 价格                              | --         | --      | --           | 价格                                 |
| PRICEBETWEENPOINTS | 3      | NUMBER      | 计费点间分册价格                        | --         | --      | --           | 计费点间分册价格                           |
| UNPACKCOUNT        | 3      | NUMBER      | 不打包单元数                          | --         | --      | --           | 不打包单元数                             |
| STATE              | 3      | NUMBER      | 图书分册状态；0，待处理1，待处理，2.处理成功 3.处理失败 | --         | --      | --           | 图书分册状态---0,待处理 1,待处理 2,处理成功 3,处理失败 |
| CREATETIME         | 12     | VARCHAR2    | 创建时间                            | --         | --      | --           | 创建时间                               |
| CREATOR            | 12     | VARCHAR2    | 创建者                             | --         | --      | --           | 驳回原因                               |
| REJECTREASON       | 12     | VARCHAR2    | 分册驳回原因                          | --         | --      | --           | 分册驳回原因                             |
| FASCICULETYPE      | 3      | NUMBER      | 分册类型：手动分册，自动                    | --         | --      | --           | 分册类型（1：手动分册、2：自动分册）                |
| FAILUREREASON      | 12     | VARCHAR2    | 分册失败原因                          | --         | --      | --           | 分册失败原因                             |
|                    |        |             |                                 | OWNEDPLACE | 12      | VARCHAR2     |                                    |
+--------------------+--------+-------------+---------------------------------+------------+---------+--------------+------------------------------------+


  # 图书打包（文件信息同步）

  :book_cmp_mebfile => {
    :from  => 'book.t_cmp_type_mebfile',
    :to    => 'common.t_cmp_type_mebfile',
    :key   => ['OBJECTID']
  },

#cmusync ct book_cmp_mebfile
--> From Table : book.t_cmp_type_mebfile
--> To   Table : common.t_cmp_type_mebfile
+----------------+--------+-------------+-----------+------------+---------+--------------+------------------------------------+
| F-NAME         | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                         |
+----------------+--------+-------------+-----------+------------+---------+--------------+------------------------------------+
| OBJECTID       | 3      | NUMBER      |           | --         | --      | --           | ID                                 |
| SUITTYPEID     | 3      | NUMBER      |           | --         | --      | --           | 适配类型---1,WAP 2,WWW 3,客户端大图 4,客户端小图 |
| LASTMODIFYTIME | 12     | VARCHAR2    |           | --         | --      | --           | 最后修改时间                             |
| RESOLUTION     | 12     | VARCHAR2    |           | --         | --      | --           | MEB包中图片分辨率大小                       |
|                |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                    |
+----------------+--------+-------------+-----------+------------+---------+--------------+------------------------------------+


  :book_ref_ebookmebfile => {
    :from  => 'book.t_cmp_ref_ebookmebfile',
    :to    => 'common.t_cmp_ref_ebookmebfile',
    :key   => ['SOURCE_ID', 'TARGET_ID']
  }, # 没数据，待测

#cmusync ct book_ref_ebookmebfile
--> From Table : book.t_cmp_ref_ebookmebfile
--> To   Table : common.t_cmp_ref_ebookmebfile
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+
| F-NAME    | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+
| SOURCE_ID | 3      | NUMBER      |           | --         | --      | --           | 书籍编号       |
| TARGET_ID | 3      | NUMBER      |           | --         | --      | --           | 文件编号       |
|           |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |            |
+-----------+--------+-------------+-----------+------------+---------+--------------+------------+


  # 打包队列表

  :book_package_queue => {
    :from  => 'book.con_package_queue',
    :to    => 'common.con_package_queue',
    :key   => ['PKID'],
    :reverse_conf => {
      # 打包状态---01,待打包;02,打包中;03,打包成功;04,打包失败;05,处理打包成功记录异常;06,待删除;07,重复记录不处理;10,放弃重试
      # 原表中没有08和09，为保险起见，把09也加上了 :)
      "PKSTATUS" => ['05', '06', '07', '08', '09', '10'],
    },
    :reverse_action => :ignore,
    :delete_conf => {
      "PKSTATUS" => ['08'],
    },
    # 删除行为先写死
  },

#cmusync ct book_package_queue
--> From Table : book.con_package_queue
--> To   Table : common.con_package_queue
+---------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------------------------------------------------------------------+
| F-NAME        | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                                                                  |
+---------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------------------------------------------------------------------+
| PKID          | 3      | NUMBER      |           | --         | --      | --           | 打包队列ID                                                                                      |
| BOOKID        | 3      | NUMBER      |           | --         | --      | --           | 打包书项ID                                                                                      |
| PKTYPE        | 12     | VARCHAR2    |           | --         | --      | --           | 打包类型                                                                                        |
| PKENTRY       | 12     | VARCHAR2    |           | --         | --      | --           | 打包入口---1,产品定价重新修改 2,连载设置成完本打包 3,主编审核通过打包 4,完本设置成未完本打包 5,在线驳回审核通过打包 6,打包入口质检审核通过打包           |
| PKSTATUS      | 12     | VARCHAR2    |           | --         | --      | --           | 打包状态---01,待打包;02,打包中;03,打包成功;04,打包失败;05,处理打包成功记录异常;06,待删除;07,重复记录不处理;10,放弃重试                |
| PKTIME        | 93     | DATE        |           | --         | --      | --           | 打包记录插入时间                                                                                    |
| DRMPUBLISH    | 12     | VARCHAR2    |           | --         | --      | --           | DRM打包成功后回调manager的DRMPackageStatusListenner中DRMHolder.finishPackage，"1"---1,需要上架publishbook |
| CONVERTSTATUS | 12     | VARCHAR2    |           | --         | --      | --           | 内容转换状态（0：已发送，1：已处理）                                                                         |
| RETRIES       | 3      | NUMBER      |           | --         | --      | --           | 重试次数                                                                                        |
| REMARK        | 12     | VARCHAR2    |           | --         | --      | --           | 备注                                                                                          |
| CREATETIME    | 93     | DATE        |           | --         | --      | --           | 创建时间                                                                                        |
| RETRYTIME     | 93     | DATE        |           | --         | --      | --           | 重发时间                                                                                        |
| PKSTEP        | 12     | VARCHAR2    |           | --         | --      | --           | 打包中细分步骤                                                                                     |
| STEPTIME      | 93     | DATE        |           | --         | --      | --           | 打包中细分步骤更新时间                                                                                 |
| BEGINTIME     | 93     | DATE        |           | --         | --      | --           | 打包结束时间                                                                                      |
| ENDTIME       | 93     | DATE        |           | --         | --      | --           |                                                                                             |
| ISPRIORITY    | 12     | VARCHAR2    |           | --         | --      | --           | 是否优先处理；null:(默认),1:(优先打包)                                                                   |
|               |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                                                                                             |
+---------------+--------+-------------+-----------+------------+---------+--------------+---------------------------------------------------------------------------------------------+


  :book_fascicule_package_queue => {
    :from  => 'book.con_fascicule_package_queue',
    :to    => 'common.con_fascicule_package_queue',
    :key   => ['PKID']
  },

cmusync ct book_fascicule_package_queue
--> From Table : book.con_fascicule_package_queue
--> To   Table : common.con_fascicule_package_queue
+-------------+--------+-------------+--------------------------------------------------------------------------------+------------+---------+--------------+--------------------------------------------------------------------------------+
| F-NAME      | F-TYPE | F-TYPE-NAME | F-COMMENT                                                                      | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                                                     |
+-------------+--------+-------------+--------------------------------------------------------------------------------+------------+---------+--------------+--------------------------------------------------------------------------------+
| PKID        | 3      | NUMBER      | 打包任务Id                                                                         | --         | --      | --           | 打包任务Id                                                                         |
| BOOKID      | 3      | NUMBER      | 分册图书ID                                                                         | --         | --      | --           | 分册图书ID                                                                         |
| FASCICULEID | 3      | NUMBER      | 分册ID                                                                           | --         | --      | --           | 分册ID                                                                           |
| STATUS      | 12     | VARCHAR2    | 打包状态（1：待打包，2：打包中，3：打包成功，4：打包失败，10: 放弃重试）---1,待打包 2,打包中 3,打包成功 4,打包失败 ,10: 放弃重试 | --         | --      | --           | 打包状态（1：待打包，2：打包中，3：打包成功，4：打包失败，10: 放弃重试）---1,待打包 2,打包中 3,打包成功 4,打包失败 ,10: 放弃重试 |
| CREATETIME  | 93     | DATE        | 打包任务生成时间                                                                       | --         | --      | --           | 打包任务生成时间                                                                       |
| PACKTYPE    | 12     | VARCHAR2    | 打包类型，1：drm打包（包含meb打包），2：meb打包---1,drm打包（包含meb打包） 2,meb打包                       | --         | --      | --           | 打包类型，1：drm打包（包含meb打包），2：meb打包---1,drm打包（包含meb打包） 2,meb打包                       |
| BEGINTIME   | 93     | DATE        | 打包开始时间                                                                         | --         | --      | --           | 打包开始时间                                                                         |
| RETRIES     | 3      | NUMBER      | 重试次数；上限为10                                                                     | --         | --      | --           | 重试次数；上限为10                                                                     |
| RETRYTIME   | 93     | DATE        | 重试时间                                                                           | --         | --      | --           | 重试时间                                                                           |
| ISPRIORITY  | 12     | VARCHAR2    | 是否优先处理；null:(默认),1:(优先打包)                                                      | --         | --      | --           | 是否优先处理；null:(默认),1:(优先打包)                                                      |
|             |        |             |                                                                                | OWNEDPLACE | 12      | VARCHAR2     |                                                                                |
+-------------+--------+-------------+--------------------------------------------------------------------------------+------------+---------+--------------+--------------------------------------------------------------------------------+


  :book_package_batch => {
    :from  => 'book.con_package_batch',
    :to    => 'common.con_package_batch',
    :key   => ['PKID', 'BATCHID']
  },

#cmusync ct book_package_batch
--> From Table : book.con_package_batch
--> To   Table : common.con_package_batch
+---------+--------+-------------+-----------+------------+---------+--------------+---------------------------+
| F-NAME  | F-TYPE | F-TYPE-NAME | F-COMMENT | TO-NAME    | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                |
+---------+--------+-------------+-----------+------------+---------+--------------+---------------------------+
| PKID    | 3      | NUMBER      |           | --         | --      | --           | 打包ID，来至CON_PACKAGE_QUEUE表 |
| BATCHID | 3      | NUMBER      |           | --         | --      | --           | 批次ID，可为null               |
|         |        |             |           | OWNEDPLACE | 12      | VARCHAR2     |                           |
+---------+--------+-------------+-----------+------------+---------+--------------+---------------------------+

    
      # 应该是元数据
      :book_ebookformat_type => {
        :from  => "common.t_cmp_ebookformat_type",
        :to    => "book.bks_ebookformat_type",
        :key   => ['FORMATTYPEID']
      },

cmusync ct book_ebookformat_type
--> From Table : common.t_cmp_ebookformat_type
--> To   Table : book.bks_ebookformat_type
+----------------+--------+-------------+------------+---------+---------+--------------+------------+
| F-NAME         | F-TYPE | F-TYPE-NAME | F-COMMENT  | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+----------------+--------+-------------+------------+---------+---------+--------------+------------+
| FORMATTYPEID   | 3      | NUMBER      |            | --      | --      | --           |            |
| FORMATTYPENAME | 12     | VARCHAR2    | 图书版式分类类型名称 | --      | --      | --           |            |
+----------------+--------+-------------+------------+---------+---------+--------------+------------+


      # 同上，是元数据
      :book_classmanager => {
        :from  => 'common.sup_classmanage',
        :to    => 'book.sup_classmanage',
        :key   => ['CLASSCODE']
      },

#cmusync ct book_classmanager
--> From Table : common.sup_classmanage
--> To   Table : book.sup_classmanage
+------------------+--------+-------------+----------------------------------------------------------------------------------------------------------------------------------------+---------+---------+--------------+------------+
| F-NAME           | F-TYPE | F-TYPE-NAME | F-COMMENT                                                                                                                              | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT |
+------------------+--------+-------------+----------------------------------------------------------------------------------------------------------------------------------------+---------+---------+--------------+------------+
| CLASSCODE        | 12     | VARCHAR2    | 分类编码                                                                                                                                   | --      | --      | --           |            |
| PARENTCLASSCODE  | 12     | VARCHAR2    | 父分类编码                                                                                                                                  | --      | --      | --           |            |
| CLASSNAME        | 12     | VARCHAR2    | 分类名称                                                                                                                                   | --      | --      | --           |            |
| MMFLAG           | 1      | CHAR        | 是否同步给MM平台                                                                                                                              | --      | 12      | VARCHAR2     |            |
| DEFAULTAREA      | 12     | VARCHAR2    | 默认专区                                                                                                                                   | --      | --      | --           |            |
| UNITEAREA        | 12     | VARCHAR2    | 整合专区                                                                                                                                   | --      | --      | --           |            |
| CAREERDEPARTMENT | 12     | VARCHAR2    | 归属事业部 0:不详 1:图书-综合运营部 2:图书-原创内容运营事业部 3:图书-女生内容运营中心 4:图书-出版内容运营事业部 5:漫画-出版内容运营事业部 6:听书-产品创新部 7:杂志-手机报媒体事业部 8:报纸-手机报 媒体事业部 9:图片-手机报媒体事业部; | --      | --      | --           |            |
+------------------+--------+-------------+----------------------------------------------------------------------------------------------------------------------------------------+---------+---------+--------------+------------+      
      
    :sup_sys_config => {
      :from  => 'common.sup_sys_config',
      :to    => 'book.bks_sys_config',
      :key   => ['KEY', 'MODULEID']
    },
    
cmusync ct sup_sys_config
--> From Table : common.sup_sys_config
--> To   Table : book.bks_sys_config
+---------------------+--------+-------------+--------------------------------------------------------+---------+---------+--------------+--------------------------------------------------------+
| F-NAME              | F-TYPE | F-TYPE-NAME | F-COMMENT                                              | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                                             |
+---------------------+--------+-------------+--------------------------------------------------------+---------+---------+--------------+--------------------------------------------------------+
| KEY                 | 12     | VARCHAR2    | 配置项键值                                                  | --      | --      | --           | 配置项键值                                                  |
| VALUE               | 12     | VARCHAR2    | 配置项值                                                   | --      | --      | --           | 配置项值                                                   |
| LASTMODIFER         | 12     | VARCHAR2    | 最后修改人                                                  | --      | --      | --           | 最后修改人                                                  |
| LASTMODIFYTIME      | 93     | DATE        | 最后修改时间                                                 | --      | --      | --           | 最后修改时间                                                 |
| MODULEID            | 12     | VARCHAR2    | 模块ID--- -1,所有 1,manager 2,server 3,portal              | --      | --      | --           | 模块ID--- -1,所有 1,manager 2,server 3,portal              |
| DESCRIPTION         | 12     | VARCHAR2    | 描述                                                     | --      | --      | --           | 描述                                                     |
| SHOWTYPE            | 12     | VARCHAR2    | 显示类型---1,文本框 2,下拉列表                                    | --      | --      | --           | 显示类型---1,文本框 2,下拉列表                                    |
| NAME                | 12     | VARCHAR2    | 配置项名称                                                  | --      | --      | --           | 配置项名称                                                  |
| CHECK_METHOD_NAME   | 12     | VARCHAR2    | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的方法名    | --      | --      | --           | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的方法名    |
| CHECK_METHOD_REGEXP | 12     | VARCHAR2    | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的正则表达式  | --      | --      | --           | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的正则表达式  |
| CHECK_METHOD_ERRMSG | 12     | VARCHAR2    | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的返回错误信息 | --      | --      | --           | 需要调用校验类【SystemConfigValueCheckUtils.java】中的某个校验的返回错误信息 |
+---------------------+--------+-------------+--------------------------------------------------------+---------+---------+--------------+--------------------------------------------------------+

     # 风险等级表
  :book_qc_result_items => {
    :from  => 'common.con_qc_result_items',
    :to    => 'book.bks_qc_result_items',
    :key   => ['ID']
  },

cmusync ct book_qc_result_items
--> From Table : common.con_qc_result_items
--> To   Table : book.bks_qc_result_items
+------------+--------+-------------+---------------------------+---------+---------+--------------+---------------------------+
| F-NAME     | F-TYPE | F-TYPE-NAME | F-COMMENT                 | TO-NAME | TO-TYPE | TO-TYPE-NAME | TO-COMMENT                |
+------------+--------+-------------+---------------------------+---------+---------+--------------+---------------------------+
| ID         | 3      | NUMBER      | ID号，必填，唯一标识               | --      | --      | --           | ID号，必填，唯一标识               |
| TYPE       | 3      | NUMBER      | 类型，必填---1,风险等级【默认】 2,品质等级 | --      | --      | --           | 类型，必填---1,风险等级【默认】 2,品质等级 |
| NAME       | 12     | VARCHAR2    | 名称，必填，同类型不允许重复、最大长度100个字符 | --      | --      | --           | 名称，必填，同类型不允许重复、最大长度100个字符 |
| NEEDREHEAR | 3      | NUMBER      | 是否需要复审，必填---0,否 1,是【默认】   | --      | --      | --           | 是否需要复审，必填---0,否 1,是【默认】   |
| REMARK     | 12     | VARCHAR2    | 备注，可选，最大长度1024个字符         | --      | --      | --           | 备注，可选，最大长度1024个字符         |
+------------+--------+-------------+---------------------------+---------+---------+--------------+---------------------------+


 */
/**
 * @author zhangtieying
 *
 */
package com.cmread.cmu.css.service.sync.book;