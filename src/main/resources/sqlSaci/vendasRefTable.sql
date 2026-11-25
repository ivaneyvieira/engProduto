USE sqldados;

CREATE TABLE sqldados.vendasRef
(
  storeno  SMALLINT NOT NULL,
  pdvno    SMALLINT NOT NULL,
  xano     INT      NOT NULL,
  impresso SMALLINT,
  PRIMARY KEY (storeno, pdvno, xano)
);


ALTER TABLE sqldados.vendasRef
  ADD COLUMN dataImpressao int(10)  NULL,
  ADD COLUMN horaImpessao  int(10) NULL;

ALTER TABLE sqldados.vendasRef
  ADD COLUMN userSeparacao int(10)  NULL;
