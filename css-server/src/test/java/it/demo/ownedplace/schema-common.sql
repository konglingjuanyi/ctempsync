SET DATABASE SQL SYNTAX ORA TRUE; 

create table test_ownedplace_book (
  id Integer,
  ownedplace varchar(1)
);

create table test_common_ownedplace (
  id Integer,
  name varchar(20),
  ownedplace varchar(1)
);