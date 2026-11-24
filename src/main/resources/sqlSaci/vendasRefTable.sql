USE sqldados;

CREATE TABLE sqldados.vendasRef
(
  storeno  SMALLINT NOT NULL,
  pdvno    SMALLINT NOT NULL,
  xano     INT      NOT NULL,
  impresso SMALLINT,
  PRIMARY KEY (storeno, pdvno, xano)
);

