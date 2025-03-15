USE sqldados;

DROP TABLE IF EXISTS produtoEstoqueAcerto;
CREATE TABLE produtoEstoqueAcerto
(
  numero    INT          NOT NULL,
  numloja   INT          NOT NULL,
  lojaSigla VARCHAR(2)   NOT NULL,
  data      DATE         NOT NULL,
  hora      TIME         NOT NULL,
  usuario   VARCHAR(50)  NOT NULL,
  prdno     VARCHAR(16)  NOT NULL,
  descricao VARCHAR(100) NOT NULL,
  grade     VARCHAR(20)  NOT NULL,
  diferenca INT          NOT NULL,
  PRIMARY KEY (numloja, numero, prdno, grade)
);

SELECT *
FROM
  produtoEstoqueAcerto

