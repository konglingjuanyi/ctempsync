SET DATABASE SQL SYNTAX ORA TRUE; 

drop table if exists MCP_AUTHORINFORMATION;

create table MCP_AUTHORINFORMATION
(
  auid              VARCHAR2(20) not null,
  accountid         VARCHAR2(20),
  auname            VARCHAR2(40),
  aupenname         VARCHAR2(40),
  sex               VARCHAR2(1),
  birthday          DATE,
  auaddress         VARCHAR2(100),
  marrystate        VARCHAR2(1),
  firm              VARCHAR2(100),
  position          VARCHAR2(60),
  aumsisdn          VARCHAR2(11),
  telephone         VARCHAR2(20),
  postaddress       VARCHAR2(100),
  idcard            VARCHAR2(20),
  postcode          VARCHAR2(6),
  austatus          VARCHAR2(1) not null,
  aulaststatus      VARCHAR2(1),
  createdate        DATE,
  grade             VARCHAR2(1),
  email             VARCHAR2(50),
  auclass           VARCHAR2(1),
  country           VARCHAR2(50),
  msn               VARCHAR2(50),
  qq				VARCHAR2(14),
  honor             VARCHAR2(1024),
  method            VARCHAR2(1),
  describe          VARCHAR2(1024),
  notes             VARCHAR2(512),
  filepackageid     VARCHAR2(20),
  repetition        VARCHAR2(20),
  authentication    VARCHAR2(3),
  commutative       VARCHAR2(3),
  authorfirstletter VARCHAR2(1),
  authorgrade       VARCHAR2(2) default '0',
  msisdn            VARCHAR2(22),
  burse             VARCHAR2(22),
  certificatepath   VARCHAR2(512),
  uncertificatepath VARCHAR2(512),
  signdate          DATE,
  expiredate        DATE,
  processid          VARCHAR2(32),
  claimuserid        VARCHAR2(20)
);

drop table if exists MCP_AUCLASSINFO;

create table MCP_AUCLASSINFO
(
  auid  VARCHAR2(20) not null,
  class VARCHAR2(40)
);

drop table if exists MCP_AUTHORANDMCP;

create table MCP_AUTHORANDMCP
(
  auid         VARCHAR2(20) not null,
  mcpid        VARCHAR2(10) not null,
  describe     VARCHAR2(1024),
  relationcode  VARCHAR2(1) not null
);