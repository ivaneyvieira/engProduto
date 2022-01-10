SELECT N.storeno                                                  AS loja,
       pdvno                                                      AS pdvno,
       xano                                                       AS xano,
       N.nfno                                                     AS numero,
       N.nfse                                                     AS serie,
       custno                                                     AS cliente,
       CAST(issuedate AS DATE)                                    AS data,
       N.empno                                                    AS vendedor,
       CAST(CONCAT(X.c5, '_', N.nfno, '/', N.nfse, '_', X.c4, '_',
		   MID(IFNULL(L.localizacao, ''), 1, 4)) AS char) AS chaveExp,
       CAST(CONCAT('Entregue', '_', X.c5, '_', N.nfno, '/', N.nfse, '_', X.c4, '_',
		   MID(IFNULL(L.localizacao, ''), 1, 4)) AS char) AS chaveCD,
       SUM((X.qtty / 1000) * X.preco)                             AS totalProdutos
FROM sqldados.nf             AS N
  INNER JOIN sqldados.xaprd2 AS X
	       USING (storeno, pdvno, xano)
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
WHERE N.issuedate >= 20220101
  AND N.status <> 1
  AND (N.nfse IN (1, 3, 5, 7))
  AND (X.s12 = :marca OR :marca = 999)
  AND (N.storeno = :storeno OR :storeno = 0)
  AND (N.nfno = :nfno OR :nfno = 0)
  AND (N.nfse = :nfse OR :nfse = '')
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY N.storeno, pdvno, xano, IF(:marca = 999, '', X.c5)