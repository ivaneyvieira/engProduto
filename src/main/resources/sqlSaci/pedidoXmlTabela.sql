DROP TABLE IF EXISTS sqldados.pedidoPrdNdd;

CREATE TABLE sqldados.pedidoPrdNdd
(
  storeno  SMALLINT NOT NULL,
  ordno    INT      NOT NULL,
  prdno    CHAR(16) NOT NULL,
  grade    CHAR(8)  NOT NULL,
  quantFat DOUBLE   NULL,
  PRIMARY KEY (storeno, ordno, prdno, grade)
);