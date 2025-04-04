CREATE TABLE sqldados.invAdicional
(
  invno  int PRIMARY KEY,
  volume int,
  peso   decimal(10, 4)
);

ALTER TABLE sqldados.invAdicional
  ADD carrno int NULL;

ALTER TABLE sqldados.invAdicional
  ADD situacaoDev int NULL;

ALTER TABLE sqldados.invAdicional
  ADD cet varchar(20) NULL;

ALTER TABLE sqldados.invAdicional
  ADD userno int NULL;

SELECT *
FROM
  sqldados.invAdicional;