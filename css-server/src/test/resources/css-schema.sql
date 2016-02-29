SET DATABASE SQL SYNTAX ORA TRUE; 

drop table if exists csync_task;
drop table if exists csync_task_failed;
drop table if exists csync_task_success;

create table csync_task
(
  taskid varchar2(40), 
  tasktype varchar2(50),
  message varchar2(1024),
  clientip varchar2(15),
  clientstarttime timestamp,
  createtime timestamp,
  starttime timestamp,
  endtime timestamp,
  status number(1),
  failedtimes number(1),
  
  primary key (taskid)
);

create table csync_task_failed
(
  taskid varchar2(40), 
  tasktype varchar2(50),
  message varchar2(1024),
  clientip varchar2(15),
  clientstarttime timestamp,
  createtime timestamp,
  starttime timestamp,
  endtime timestamp,
  status number(1),
  failedtimes number(1),
  
  primary key (taskid)
);

create table csync_task_success
(
  taskid varchar2(40), 
  tasktype varchar2(50),
  message varchar2(1024),
  clientip varchar2(15),
  clientstarttime timestamp,
  createtime timestamp,
  starttime timestamp,
  endtime timestamp,
  status number(1),
  failedtimes number(1),
  
  primary key (taskid)
);