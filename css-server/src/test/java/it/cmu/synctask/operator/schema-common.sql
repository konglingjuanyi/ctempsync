SET DATABASE SQL SYNTAX ORA TRUE; 

drop table if exists T_BME_OPERATOR;

create table T_BME_OPERATOR
(
  operid        VARCHAR2(64) not null,
  name          VARCHAR2(16) not null,
  password      VARCHAR2(64) not null,
  language      VARCHAR2(16),
  origintype    VARCHAR2(24) default '1',
  originid      VARCHAR2(255),
  status        NUMBER(38) default 0,
  type          NUMBER(38) default 1,
  createoperid  VARCHAR2(64),
  createdate    TIMESTAMP(6),
  updateoperid  VARCHAR2(64),
  updatedate    TIMESTAMP(6),
  effectivetime TIMESTAMP(6),
  expiredtime   TIMESTAMP(6)
);

drop table if exists CON_MCPADMIN_INFO;

create table CON_MCPADMIN_INFO
(
  userid                VARCHAR2(64) not null,
  useraccount           VARCHAR2(16) not null,
  username              VARCHAR2(60),
  mobiletelephonenumber VARCHAR2(100),
  department            VARCHAR2(512),
  mailbox               VARCHAR2(400),
  othercontact          VARCHAR2(2048),
  remark                VARCHAR2(2048),
  empno                 VARCHAR2(64)
);

drop table if exist SUP_OPERATER_EXTINFO;

create table SUP_OPERATER_EXTINFO
(
  opername varchar2(20),
  ps_lastmdftime TIMESTAMP(6),
  delflag varchar2(2),
  lastlogintime date,
  isfreezed number,
  freezetime date
)

drop table if exists T_BME_USER_ROLE;

create table T_BME_USER_ROLE
(
  operid       VARCHAR2(64) not null,
  roleid       VARCHAR2(64) not null,
  createoperid VARCHAR2(64),
  createdate   TIMESTAMP(6),
  updateoperid VARCHAR2(64),
  updatedate   TIMESTAMP(6)
)