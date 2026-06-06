DROP TABLE IF EXISTS sqldados.devClienteAutorizacao;
CREATE TABLE sqldados.devClienteAutorizacao
(
  invno           int      DEFAULT 0  NOT NULL,
  prdno           char(16) DEFAULT '' NOT NULL,
  grade           char(8)  DEFAULT '' NOT NULL,
  userEntrega     int                 NULL,
  userRecebimento int                 NULL,
  PRIMARY KEY (invno, prdno, grade)
) CHARSET = latin1;

