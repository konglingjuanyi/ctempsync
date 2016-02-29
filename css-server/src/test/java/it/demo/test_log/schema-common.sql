SET DATABASE SQL SYNTAX ORA TRUE; 

create table test_log (
  logid IDENTITY,
  operator varchar(50),
  logtime datetime
);