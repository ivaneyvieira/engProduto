USE sqldados;

DROP TEMPORARY TABLE IF EXISTS T_PRD;
CREATE TEMPORARY TABLE T_PRD
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT storeno, prdno, grade, MAX(dataVenda * 1) AS dataVenda
FROM sqldados.qtd_vencimento
WHERE storeno IN (2, 3, 4, 5, 8)
  AND dataVenda IS NOT NULL
GROUP BY storeno, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_VENDAS;
CREATE TEMPORARY TABLE T_VENDAS
(
  PRIMARY KEY (storeno, prdno, grade)
)
SELECT N.storeno                 AS storeno,
       X.prdno                   AS prdno,
       X.grade                   AS grade,
       MAX(dataVenda)            AS dataVenda,
       SUM(ROUND(X.qtty / 1000)) AS vendas
FROM sqldados.nf AS N
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
       INNER JOIN T_PRD AS P
                  ON P.storeno = N.storeno
                    AND P.prdno = X.prdno
                    AND P.grade = X.grade
                    AND (N.issuedate >= P.dataVenda)
WHERE (N.storeno IN (2, 3, 4, 5, 8))
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY N.storeno, X.prdno, X.grade;

SELECT Q.storeno                          AS storeno,
       Q.prdno                            AS prdno,
       Q.grade                            AS grade,
       V.vendas                           AS vendas,
       CAST(V.dataVenda AS DATE)          AS dataVenda,
       SUM(IF(num = 1, quantidade, NULL)) AS qtty01,
       MAX(IF(num = 1, vencimento, NULL)) AS venc01,
       SUM(IF(num = 2, quantidade, NULL)) AS qtty02,
       MAX(IF(num = 2, vencimento, NULL)) AS venc02,
       SUM(IF(num = 3, quantidade, NULL)) AS qtty03,
       MAX(IF(num = 3, vencimento, NULL)) AS venc03,
       SUM(IF(num = 4, quantidade, NULL)) AS qtty04,
       MAX(IF(num = 4, vencimento, NULL)) AS venc04
FROM sqldados.qtd_vencimento AS Q
       LEFT JOIN T_VENDAS AS V
                 USING (storeno, prdno, grade)
WHERE Q.storeno IN (2, 3, 4, 5, 8)
  AND (Q.quantidade IS NOT NULL
  OR Q.vencimento IS NOT NULL)
GROUP BY Q.storeno, Q.prdno, Q.grade
ORDER BY Q.storeno, Q.prdno, Q.grade