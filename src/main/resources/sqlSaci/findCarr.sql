USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_CARR;
CREATE TEMPORARY TABLE T_CARR
(
  INDEX (doc)
)
SELECT no AS carrno, REPLACE(REPLACE(REPLACE(cgc, '.', ''), '/', ''), '-', '') AS doc
FROM sqldados.carr;

SELECT carrno AS quant
FROM T_CARR
WHERE doc = :doc