/*DROP TABLE IF EXISTS sqldados.produtoValidade;*/
CREATE TABLE sqldados.produtoValidade
(
  storeno    INT         NOT NULL,
  prdno      VARCHAR(16) NOT NULL,
  grade      VARCHAR(8)  NOT NULL,
  vencimento INT         NOT NULL,
  estoque    INT         NULL,
  PRIMARY KEY (storeno, prdno, grade, vencimento)
);

DROP TABLE IF EXISTS sqldados.produtoValidadeLoja;
CREATE TABLE sqldados.produtoValidadeLoja
(
  seq          INT         NOT NULL AUTO_INCREMENT,
  prdno        VARCHAR(16) NOT NULL,
  grade        VARCHAR(8)  NOT NULL,
  vencimentoDS INT         NOT NULL,
  estoqueDS    INT         NULL,
  vencimentoMR INT         NOT NULL,
  estoqueMR    INT         NULL,
  vencimentoMF INT         NOT NULL,
  estoqueMF    INT         NULL,
  vencimentoPK INT         NOT NULL,
  estoquePK    INT         NULL,
  vencimentoTM INT         NOT NULL,
  estoqueTM    INT         NULL,
  PRIMARY KEY (seq),
  INDEX (prdno, grade)
);

INSERT INTO sqldados.produtoValidadeLoja (prdno, grade,
                                          vencimentoDS, estoqueDS,
                                          vencimentoMR, estoqueMR,
                                          vencimentoMF, estoqueMF,
                                          vencimentoPK, estoquePK,
                                          vencimentoTM, estoqueTM)
SELECT prdno                               AS prdno,
       grade                               AS grade,
       MAX(IF(storeno = 2, vencimento, 0)) AS vencimentoDS,
       SUM(IF(storeno = 2, estoque, 0))    AS estoqueDS,
       MAX(IF(storeno = 3, vencimento, 0)) AS vencimentoMR,
       SUM(IF(storeno = 3, estoque, 0))    AS estoqueMR,
       MAX(IF(storeno = 4, vencimento, 0)) AS vencimentoMF,
       SUM(IF(storeno = 4, estoque, 0))    AS estoqueMF,
       MAX(IF(storeno = 5, vencimento, 0)) AS vencimentoPK,
       SUM(IF(storeno = 5, estoque, 0))    AS estoquePK,
       MAX(IF(storeno = 8, vencimento, 0)) AS vencimentoTM,
       SUM(IF(storeno = 8, estoque, 0))    AS estoqueTM
FROM sqldados.produtoValidade
GROUP BY prdno, grade;

ALTER TABLE sqldados.produtoValidadeLoja
  ADD COLUMN dataEntrada INT NOT NULL DEFAULT 0;

UPDATE sqldados.produtoValidadeLoja
SET dataEntrada = CURRENT_DATE() * 1
WHERE dataEntrada = 0;


UPDATE sqldados.produtoValidadeLoja
SET dataEntrada = 0
WHERE (estoqueDS + estoqueMR + estoqueMF + estoquePK + estoqueTM ) =0;

SELECT *
FROM sqldados.produtoValidadeLoja;


ALTER TABLE sqldados.produtoValidadeLoja
  ADD COLUMN vencimento INT NOT NULL DEFAULT 0;