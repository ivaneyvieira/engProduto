USE sqldados;

DROP TABLE IF EXISTS produtoObservacaoGarantia;
CREATE TABLE produtoObservacaoGarantia
(
  numero     INT          NOT NULL,
  numloja    INT          NOT NULL,
  observacao varchar(100) NULL,
  PRIMARY KEY (numloja, numero)
);

