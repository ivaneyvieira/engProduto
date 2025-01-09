DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  prdno      VARCHAR(16),
  grade      VARCHAR(8),
  num        INT,
  quantidade INT,
  vencimento DATE,
  PRIMARY KEY (prdno, grade, num)
)
SELECT :prdno AS prdno, :grade AS grade, 1 AS num, :qtty01 AS quantidade, :venc01 AS vencimento
FROM dual
UNION
SELECT :prdno AS prdno, :grade AS grade, 2 AS num, :qtty02 AS quantidade, :venc02 AS vencimento
FROM dual
UNION
SELECT :prdno AS prdno, :grade AS grade, 3 AS num, :qtty03 AS quantidade, :venc03 AS vencimento
FROM dual
UNION
SELECT :prdno AS prdno, :grade AS grade, 4 AS num, :qtty04 AS quantidade, :venc04 AS vencimento
FROM dual;

REPLACE INTO sqldados.qtd_vencimento(prdno, grade, num, quantidade, vencimento)
SELECT prdno, grade, num, quantidade, vencimento
FROM T_DADOS
WHERE quantidade IS NOT NULL
  AND vencimento IS NOT NULL