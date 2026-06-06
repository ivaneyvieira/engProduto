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

ALTER TABLE sqldados.devClienteAutorizacao
  ADD COLUMN dataEntrega datetime NULL;

ALTER TABLE sqldados.devClienteAutorizacao
  ADD COLUMN horaEntrega time NULL;


ALTER TABLE sqldados.devClienteAutorizacao
  ADD COLUMN dataRecebimento datetime NULL;

ALTER TABLE sqldados.devClienteAutorizacao
  ADD COLUMN horaRecebimento time NULL;

UPDATE sqldados.devClienteAutorizacao
SET dataEntrega = CURDATE()
WHERE dataEntrega IS NULL;

UPDATE sqldados.devClienteAutorizacao
SET dataRecebimento = CURDATE()
WHERE dataRecebimento IS NULL;

UPDATE sqldados.devClienteAutorizacao
SET horaEntrega = CURRENT_TIME
WHERE horaEntrega IS NULL;

UPDATE sqldados.devClienteAutorizacao
SET horaRecebimento = CURRENT_TIME
WHERE horaRecebimento IS NULL;
