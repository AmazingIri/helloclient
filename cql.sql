CREATE KEYSPACE test 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;


// -------------------------------------------------------


CREATE TABLE test.t
(
  id       int PRIMARY KEY,
  date     timestamp,
  name     text,
  items    list<text>,
  courses  map<text, double>,
  requires set<int>,
)WITH caching = {'keys' : 'NONE', 'rows_per_partition' : 'ALL'};

SELECT id, date, name, items, courses, requires, json FROM test.t LIMIT 10;


// -------------------------------------------------------


CREATE TABLE test.s
(
  id  int PRIMARY KEY,
  info blob
)WITH caching = {'keys' : 'NONE', 'rows_per_partition' : 'ALL'};


// -------------------------------------------------------


CREATE TYPE test.info (
  date     timestamp,
  name     text,
  items    list<text>,
  courses  map<text, double>,
  requires set<int>
);

CREATE TABLE test.student
(
  id    int PRIMARY KEY,
  info  frozen <info>
)WITH caching = {'keys' : 'NONE', 'rows_per_partition' : 'ALL'};
