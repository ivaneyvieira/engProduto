SELECT N.no                                               AS numero,
       vendno                                             AS fornecedor,
       CAST(date AS DATE)                                 AS data,
       N.empno                                            AS vendedor,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.obs                                              AS usuarioCD,
       SUM((X.qtty / 1000) * X.cost)                      AS totalProdutos,
       MAX(X.auxShort4)                                   AS marca,
       'N'                                                AS cancelada
FROM sqldados.ords AS N
         INNER JOIN sqldados.oprd AS X
                    ON N.storeno = X.storeno AND N.no = X.ordno
         LEFT JOIN sqldados.prdloc AS L
                   ON L.prdno = X.prdno AND L.storeno = 4
WHERE N.date >= 20220101
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (N.storeno = 1)
  AND (N.no = :ordno OR :ordno = 0)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND N.no >= 100000000
GROUP BY N.storeno,
         N.no,
         IF(:marca = 999, '', SUBSTRING_INDEX(X.remarks, '_', 1)),
         IF(:marca = 999, '', SUBSTRING_INDEX(X.obs, '_', 1)),
         IF(:marca = 999, '', MID(L.localizacao, 1, 4))