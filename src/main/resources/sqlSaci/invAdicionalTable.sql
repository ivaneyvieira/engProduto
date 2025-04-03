CREATE TABLE sqldados.invAdicional
(
  invno  int PRIMARY KEY,
  volume int,
  peso   decimal(10, 4)
);

ALTER TABLE sqldados.invAdicional
  ADD carrno int NULL;

SELECT *
FROM
  sqldados.invAdicional;