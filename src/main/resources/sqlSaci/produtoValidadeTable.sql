DROP TABLE IF EXISTS sqldados.produtoValidade;
CREATE TABLE sqldados.produtoValidade
(
  storeno    INT         NOT NULL,
  prdno      VARCHAR(16) NOT NULL,
  grade      VARCHAR(8)  NOT NULL,
  vencimento INT         NOT NULL,
  estoque    INT         NULL,
  PRIMARY KEY (storeno, prdno, grade, vencimento)
);




