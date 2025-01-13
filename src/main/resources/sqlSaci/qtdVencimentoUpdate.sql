DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  storeno    INT,
  prdno      VARCHAR(16),
  grade      VARCHAR(8),
  dataVenda  DATE,
  num        INT,
  quantidade INT,
  vencimento VARCHAR(10),
  PRIMARY KEY (storeno, prdno, grade, num)
)
SELECT :storeno   AS storeno,
       :prdno     AS prdno,
       :grade     AS grade,
       :dataVenda AS dataVenda,
       1          AS num,
       :qtty01    AS quantidade,
       :venc01    AS vencimento
FROM dual
UNION
SELECT :storeno   AS storeno,
       :prdno     AS prdno,
       :grade     AS grade,
       :dataVenda AS dataVenda,
       2          AS num,
       :qtty02    AS quantidade,
       :venc02    AS vencimento
FROM dual
UNION
SELECT :storeno   AS storeno,
       :prdno     AS prdno,
       :grade     AS grade,
       :dataVenda AS dataVenda,
       3          AS num,
       :qtty03    AS quantidade,
       :venc03    AS vencimento
FROM dual
UNION
SELECT :storeno   AS storeno,
       :prdno     AS prdno,
       :grade     AS grade,
       :dataVenda AS dataVenda,
       4          AS num,
       :qtty04    AS quantidade,
       :venc04    AS vencimento
FROM dual;

REPLACE INTO sqldados.qtd_vencimento(storeno, prdno, grade, dataVenda, num, quantidade, vencimento)
SELECT storeno, prdno, grade, dataVenda, num, quantidade, vencimento
FROM T_DADOS
WHERE storeno IN (2, 3, 4, 5, 8)