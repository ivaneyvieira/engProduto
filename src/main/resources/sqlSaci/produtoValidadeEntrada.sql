SET SQL_MODE = '';

DROP TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (prdno)
)
SELECT P.no AS prdno
FROM sqldados.prd AS P
WHERE garantia > 0
  AND tipoGarantia = 2;

SELECT I.storeno               AS loja,
       P.prdno                 AS prdno,
       P.grade                 AS grade,
       CAST(I.date AS DATE)    AS date,
       ROUND(SUM(qtty / 1000)) AS qtty
FROM sqldados.inv AS I
       INNER JOIN sqldados.iprd AS P
                  ON I.invno = P.invno
       INNER JOIN T_PRD
                  USING (prdno)
WHERE I.date >= :dataInicial
  AND I.bits & POW(2, 4) = 0
  AND I.invno NOT IN (SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0)
  AND I.type in (1)
GROUP BY loja, prdno, grade, date


/*
         WHEN type = 0 THEN 'RECEBIMENTO'
         WHEN type = 1 THEN CONCAT('TRANSF DE ', IFNULL(L.sname, I.nfStoreno))
         WHEN type = 2 THEN 'DEVOLUCAO'
         WHEN type = 3 THEN 'TROCA'

 */

