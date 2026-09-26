USE sqldados;

DROP TABLE IF EXISTS sqldados.nfSolicitacaoCancelar;
CREATE TABLE sqldados.nfSolicitacaoCancelar
(
  storeno    int NOT NULL,
  pdvno      int NOT NULL,
  xano       int NOT NULL,
  motivo     varchar(100),
  userCancel Int,
  PRIMARY KEY (storeno, pdvno, xano)
);

