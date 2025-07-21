CREATE TABLE sqldados.vendAdicional
(
  vendno  int          NOT NULL PRIMARY KEY,
  termDev varchar(100) NULL DEFAULT ''
);


DROP TABLE IF EXISTS sqldados.vendArquivo;
CREATE TABLE sqldados.vendArquivo
(
  seq      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  vendno   int          NOT NULL,
  filename varchar(100) NOT NULL,
  file     MEDIUMBLOB   NULL,
  UNIQUE (vendno, seq)
);

