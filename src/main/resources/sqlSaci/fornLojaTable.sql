DROP TABLE IF EXISTS sqldados.vendLojaAdicional;
CREATE TABLE sqldados.vendLojaAdicional
(
  vendno  int NOT NULL,
  storeno int NOT NULL,
  data    int NULL,
  PRIMARY KEY (vendno, storeno)
);

ALTER TABLE sqldados.vendLojaAdicional
  ADD COLUMN userno int NULL;

SELECT *
FROM
  sqldados.vendLojaAdicional;