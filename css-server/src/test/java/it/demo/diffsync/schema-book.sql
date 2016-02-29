SET DATABASE SQL SYNTAX ORA TRUE; 

drop table if exists simple_user;
create table simple_user (
  userid int,
  username  varchar2(50)
);

drop table if exists simple_role;
create table simple_role (
  roleid int,
  rolename varchar2(50),
);

drop table if exists simple_user_role;
create table simple_user_role (
  userid int,
  roleid int,
  info varchar2(50)
);
