DROP TABLE IF EXISTS sqldados.invAdicionalArquivos;
CREATE TABLE sqldados.invAdicionalArquivos
(
  seq      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  invno    INT          NOT NULL,
  title    VARCHAR(100) NOT NULL,
  date     INT          NOT NULL,
  filename VARCHAR(100) NOT NULL,
  file     MEDIUMBLOB   NULL,
  UNIQUE (invno, seq)
);
