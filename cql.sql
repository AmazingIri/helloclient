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

SELECT id, date, name, items, courses, requires FROM test.t LIMIT 10;


// -------------------------------------------------------


CREATE TABLE test.s
(
  id  int PRIMARY KEY,
  info blob
)WITH caching = {'keys' : 'NONE', 'rows_per_partition' : 'ALL'};


// -------------------------------------------------------


CREATE TABLE test.j
(
  id    int PRIMARY KEY,
  info  VARCHAR
)WITH caching = {'keys' : 'NONE', 'rows_per_partition' : 'ALL'};
