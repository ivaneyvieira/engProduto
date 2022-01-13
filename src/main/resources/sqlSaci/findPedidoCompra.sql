SELECT N.storeno                                          AS loja,
       pdvno                                              AS pdvno,
       N.nfno                                             AS numero,
       N.nfse                                             AS serie,
       custno                                             AS cliente,
       CAST(date AS DATE)                                 AS data,
       N.empno                                            AS vendedor,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c3                                               AS usuarioExp,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.price / 100)               AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       'N'                                                AS cancelada
FROM sqldados.eord           AS N
  INNER JOIN sqldados.eoprd  AS X
	       USING (storeno, ordno)
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
WHERE N.date >= 20220101
  AND N.status <> 1
  AND (N.nfse IN (1, 3, 5, 7))
  AND (X.s12 = :marca OR :marca = 999)
  AND (N.storeno = :storeno OR :storeno = 0)
  AND (N.nfno = :nfno OR :nfno = 0)
  AND (N.nfse = :nfse OR :nfse = '')
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY N.storeno,
	 ordno,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c3, '_', 1)),
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c4, '_', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4))