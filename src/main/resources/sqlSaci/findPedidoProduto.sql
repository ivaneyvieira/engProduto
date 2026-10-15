USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_OPRD;
CREATE TEMPORARY TABLE T_OPRD
(
  PRIMARY KEY (storeno, ordno, prdno, grade)
)
SELECT storeno,
       ordno,
       prdno,
       grade,
       AVG(cost)       AS cost,
       SUM(qtty)       AS qtty,
       SUM(qttyCancel) AS qttyCancel,
       SUM(qttyRcv)    AS qttyRcv
FROM sqldados.oprd
WHERE storeno = :loja
  AND ordno = :pedido
GROUP BY storeno, ordno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade
FROM T_OPRD
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_REF;
CREATE TEMPORARY TABLE T_REF
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno,
       grade,
       MID(MAX(CONCAT(LPAD(l1, 8, '0'), prdrefno)), 9, 100) AS ref,
       COUNT(DISTINCT prdrefno)                             AS quant
FROM
  sqldados.prdref
    INNER JOIN T_PRD
               USING (prdno, grade)
WHERE grade != ''
GROUP BY prdno, grade;

SELECT E.storeno                               AS loja,
       E.ordno                                 AS pedido,
       TRIM(E.prdno)                           AS codigo,
       MAX(TRIM(IFNULL(B.barcode, P.barcode))) AS barcode,
       IFNULL(R.ref, P.mfno_ref)               AS refFor,
       P.name                                  AS descricao,
       P.unit                                  AS un,
       E.cost                                  AS custo,
       E.grade                                 AS grade,
       E.qtty                                  AS qttyPedido,
       E.qtty - E.qttyCancel - E.qttyRcv       AS qttyPendente
FROM
  T_OPRD                       AS E
    LEFT JOIN  T_REF           AS R
               USING (prdno, grade)
    INNER JOIN sqldados.prd    AS P
               ON P.no = E.prdno
    LEFT JOIN  sqldados.prdbar AS B
               ON E.prdno = B.prdno AND E.grade = B.grade
WHERE storeno = :loja
  AND ordno = :pedido
GROUP BY E.storeno, E.ordno, E.prdno, E.grade