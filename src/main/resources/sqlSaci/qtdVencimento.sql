USE sqldados;

SELECT Q.storeno                          AS storeno,
       Q.prdno                            AS prdno,
       Q.grade                            AS grade,
       Q.vendas                           AS vendas,
       MAX(CAST(Q.dataVenda AS DATE))     AS dataVenda,
       SUM(IF(num = 1, quantidade, NULL)) AS qtty01,
       MAX(IF(num = 1, vencimento, NULL)) AS venc01,
       SUM(IF(num = 2, quantidade, NULL)) AS qtty02,
       MAX(IF(num = 2, vencimento, NULL)) AS venc02,
       SUM(IF(num = 3, quantidade, NULL)) AS qtty03,
       MAX(IF(num = 3, vencimento, NULL)) AS venc03,
       SUM(IF(num = 4, quantidade, NULL)) AS qtty04,
       MAX(IF(num = 4, vencimento, NULL)) AS venc04
FROM sqldados.qtd_vencimento AS Q
WHERE Q.storeno IN (2, 3, 4, 5, 8)
  AND (Q.quantidade IS NOT NULL
  OR Q.vencimento IS NOT NULL)
GROUP BY Q.storeno, Q.prdno, Q.grade
ORDER BY Q.storeno, Q.prdno, Q.grade