USE sqldados;

DROP TABLE IF EXISTS produtoObservacaoGarantia;
CREATE TABLE produtoObservacaoGarantia
(
  numero     INT          NOT NULL,
  numloja    INT          NOT NULL,
  observacao varchar(100) NULL,
  PRIMARY KEY (numloja, numero)
);

ALTER TABLE sqldados.produtoObservacaoGarantia
  ADD nfd varchar(20) NULL DEFAULT '';

ALTER TABLE sqldados.produtoObservacaoGarantia
  ADD dataNfd int NULL DEFAULT 0;

