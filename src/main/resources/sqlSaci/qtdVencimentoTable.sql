DROP TABLE IF EXISTS sqldados.qtd_vencimento;
CREATE TABLE sqldados.qtd_vencimento
(
  prdno      VARCHAR(16) NOT NULL,
  grade      VARCHAR(8)  NOT NULL,
  num        INT         NOT NULL,
  quantidade INT         NULL,
  vencimento VARCHAR(10) NULL,
  PRIMARY KEY (prdno, grade, num)
);

CREATE UNIQUE INDEX idx_qtd_vencimento ON sqldados.qtd_vencimento (num, prdno, grade);
