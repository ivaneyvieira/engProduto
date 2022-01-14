SELECT N.storeno                                          AS loja,
       pdvno                                              AS pdvno,
       xano                                               AS xano,
       N.nfno                                             AS numero,
       N.nfse                                             AS serie,
       custno                                             AS cliente,
       CAST(issuedate AS DATE)                            AS data,
       N.empno                                            AS vendedor,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c5                                               AS usuarioExp,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.preco)                     AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       IF(N.status <> 1, 'N', 'S')                        AS cancelada
FROM sqldados.nf             AS N
  INNER JOIN sqldados.xaprd2 AS X
	       USING (storeno, pdvno, xano)
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
  LEFT JOIN  sqldados.emp    AS E
	       ON E.no = N.empno
WHERE N.issuedate >= 20220101
  AND (N.nfse IN (1, 5, 7) OR (N.nfse IN (1, 3, 5, 7) AND :marca = 2))
  AND (X.s12 = :marca OR :marca = 999)
  AND (N.storeno = :storeno OR :storeno = 0)
  AND (N.nfno = :nfno OR :nfno = 0)
  AND (N.nfse = :nfse OR :nfse = '')
  AND (N.custno = :cliente OR :cliente = 0)
  AND (E.sname = :vendedor OR :vendedor = '')
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY N.storeno,
	 pdvno,
	 xano,
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c5, '_', 1)),
	 IF(:marca = 999, '', SUBSTRING_INDEX(X.c4, '_', 1)),
	 IF(:marca = 999, '', MID(L.localizacao, 1, 4))
