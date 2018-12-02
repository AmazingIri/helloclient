CREATE KEYSPACE test 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;


CREATE TABLE test.t
(
  id       int PRIMARY KEY,
  date     timestamp,
  name     text,
  items    list<text>,
  courses  map<text, double>,
  requires set<int>,
  json     text
);


INSERT INTO test.t (id, date, name, items, courses, requires, json) VALUES (?, ?, ?, ?, ?, ?, ?)


SELECT id, date, name, items, courses, requires, json FROM test.t LIMIT 10;