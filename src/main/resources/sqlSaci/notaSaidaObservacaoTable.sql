CREATE TABLE sqldados.notaSaidaObservacao
(
  storeno    SMALLINT     NOT NULL,
  pdvno      SMALLINT     NOT NULL,
  xano       INT(11)      NOT NULL,
  observacao VARCHAR(400) NOT NULL DEFAULT '',
  PRIMARY KEY (storeno, pdvno, xano)
);