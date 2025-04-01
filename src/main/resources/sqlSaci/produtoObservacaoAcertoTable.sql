USE sqldados;

DROP TABLE IF EXISTS produtoObservacaoAcerto;
CREATE TABLE produtoObservacaoAcerto
(
  numero     INT          NOT NULL,
  numloja    INT          NOT NULL,
  observacao varchar(100) NULL,
  PRIMARY KEY (numloja, numero)
);

