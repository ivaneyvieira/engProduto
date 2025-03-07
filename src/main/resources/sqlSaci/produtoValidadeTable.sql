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
FROM
  sqldados.produtoValidade
GROUP BY prdno, grade;

ALTER TABLE sqldados.produtoValidadeLoja
  ADD COLUMN dataEntrada INT NOT NULL DEFAULT 0;

UPDATE sqldados.produtoValidadeLoja
SET dataEntrada = CURRENT_DATE() * 1
WHERE dataEntrada = 0;


UPDATE sqldados.produtoValidadeLoja
SET dataEntrada = 0
WHERE (estoqueDS + estoqueMR + estoqueMF + estoquePK + estoqueTM) = 0;

SELECT *
FROM
  sqldados.produtoValidadeLoja;


ALTER TABLE sqldados.produtoValidadeLoja
  ADD COLUMN vencimento INT NOT NULL DEFAULT 0;

UPDATE sqldados.produtoValidadeLoja
SET vencimento = vencimentoTM
WHERE vencimentoTM > 0
  AND vencimento = 0;



SELECT *
FROM
  sqldados.produtoValidade;

ALTER TABLE sqldados.produtoValidade
  ADD COLUMN dataEntrada INT DEFAULT 0 NOT NULL AFTER grade;

TRUNCATE TABLE sqldados.produtoValidade;

INSERT INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT 2 AS storeno, prdno, grade, dataEntrada, vencimentoDS, estoqueDS
FROM
  sqldados.produtoValidadeLoja
WHERE vencimentoDS > 0
  AND estoqueDS > 0;

INSERT INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT 3 AS storeno, prdno, grade, dataEntrada, vencimentoMR, estoqueMR
FROM
  sqldados.produtoValidadeLoja
WHERE vencimentoMR > 0
  AND estoqueMR > 0;

INSERT INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT 4 AS storeno, prdno, grade, dataEntrada, vencimentoMF, estoqueMF
FROM
  sqldados.produtoValidadeLoja
WHERE vencimentoMF > 0
  AND estoqueMF > 0;

INSERT INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT 5 AS storeno, prdno, grade, dataEntrada, vencimentoPK, estoquePK
FROM
  sqldados.produtoValidadeLoja
WHERE vencimentoPK > 0
  AND estoquePK > 0;


INSERT INTO sqldados.produtoValidade (storeno, prdno, grade, dataEntrada, vencimento, estoque)
SELECT 8 AS storeno, prdno, grade, dataEntrada, vencimentoTM, estoqueTM
FROM
  sqldados.produtoValidadeLoja
WHERE vencimentoTM > 0
  AND estoqueTM > 0;


SELECT *
FROM
  sqldados.produtoValidade
WHERE storeno = 3;

SELECT *
FROM
  sqldados.produtoValidadeLoja;

SELECT *
FROM
  sqldados.stk
WHERE prdno = 113613;

ALTER TABLE sqldados.produtoValidade
  ADD COLUMN compras INT NOT NULL DEFAULT 0;

ALTER TABLE sqldados.produtoValidade
  ADD COLUMN vencimentoEdit INT NULL;

/**********************************************************/

CREATE TABLE sqldados.produtoValidadeBak
SELECT *
FROM
  sqldados.produtoValidade;

ALTER TABLE sqldados.produtoValidade
  DROP COLUMN vencimentoEdit;

ALTER TABLE sqldados.produtoValidade
  ADD COLUMN tipo VARCHAR(3) NOT NULL DEFAULT '' AFTER vencimento;

ALTER TABLE sqldados.produtoValidade
  DROP PRIMARY KEY;

ALTER TABLE sqldados.produtoValidade
  ADD PRIMARY KEY (storeno, prdno, grade, vencimento, tipo);

UPDATE sqldados.produtoValidade
SET tipo = 'SAI'
WHERE vencimento = 0
  AND tipo = '';

UPDATE sqldados.produtoValidade
SET tipo = 'TRA'
WHERE vencimento = 1
  AND tipo = '';

UPDATE sqldados.produtoValidade
SET tipo = 'INV'
WHERE vencimento > 10
  AND estoque > 0
  AND tipo = '';

UPDATE sqldados.produtoValidade
SET estoque = compras
WHERE estoque = 0
  AND compras > 0;

UPDATE sqldados.produtoValidade
SET vencimento = 0
WHERE vencimento < 10;

ALTER TABLE sqldados.produtoValidade
  DROP COLUMN compras;

DELETE
FROM
  sqldados.produtoValidade
WHERE estoque = 0
  AND tipo = '';

SELECT DISTINCT tipo
FROM
  sqldados.produtoValidade;

ALTER TABLE sqldados.produtoValidade RENAME COLUMN estoque TO movimento;

DELETE
FROM
  sqldados.produtoValidade
WHERE movimento = 0;

SELECT DISTINCT tipo
FROM
  sqldados.produtoValidade;

SELECT *
FROM
  sqldados.produtoValidade
WHERE prdno = 105769
  AND storeno = 2;

/***************************************************************************/

ALTER TABLE sqldados.produtoValidade
  DROP PRIMARY KEY;

ALTER TABLE sqldados.produtoValidade
  ADD PRIMARY KEY (storeno, prdno, grade, vencimento, tipo, dataEntrada);


