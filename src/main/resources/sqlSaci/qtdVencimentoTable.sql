DROP TABLE IF EXISTS sqldados.qtd_vencimento;
CREATE TABLE sqldados.qtd_vencimento
(
  storeno    INT         NOT NULL,
  prdno      VARCHAR(16) NOT NULL,
  grade      VARCHAR(8)  NOT NULL,
  num        INT         NOT NULL,
  quantidade INT         NULL,
  vencimento VARCHAR(10) NULL,
  PRIMARY KEY (storeno, prdno, grade, num)
);

CREATE UNIQUE INDEX idx_qtd_vencimento ON sqldados.qtd_vencimento (num, storeno, prdno, grade);


ALTER TABLE sqldados.qtd_vencimento
  ADD COLUMN dataVenda DATE NULL AFTER num;


ALTER TABLE sqldados.qtd_vencimento
  ADD COLUMN vendas INT NULL AFTER dataVenda;


SELECT *
FROM
  sqldados.qtd_vencimento