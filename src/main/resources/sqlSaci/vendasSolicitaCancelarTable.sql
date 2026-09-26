USE sqldados;

CREATE TABLE sqldados.nfSolicitacaoCancelar
(
  storeno int NOT NULL,
  pdvno   int NOT NULL,
  xano    int NOT NULL,
  motivo  varchar(100),
  PRIMARY KEY (storeno, pdvno, xano)
);

