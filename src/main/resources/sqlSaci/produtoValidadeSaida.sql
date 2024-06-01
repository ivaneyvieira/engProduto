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

SELECT N.storeno                 AS lojaOrigem,
       I.storeno                 AS lojaDestino,
       SD.sname                  AS abrevDestino,
       X.prdno                   AS prdno,
       X.grade                   AS grade,
       CAST(N.issuedate AS DATE) AS date,
       ROUND(SUM(X.qtty / 1000)) AS qtty
FROM sqldados.nf AS N
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.inv AS I
                 ON N.invno = I.invno
       LEFT JOIN sqldados.store AS SD
                 ON I.storeno = SD.no
       INNER JOIN T_PRD AS P
                  USING (prdno)
WHERE N.issuedate >= :dataInicial
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.status <> 1
GROUP BY N.storeno, X.prdno, X.grade, N.issuedate
ORDER BY N.storeno, X.prdno, X.grade, N.issuedate


