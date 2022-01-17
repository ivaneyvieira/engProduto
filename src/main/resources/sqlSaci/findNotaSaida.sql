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
       IF(N.status <> 1, 'N', 'S')                        AS cancelada,
       CASE
	 WHEN tipo = 0
	   THEN ''
	 WHEN tipo = 1
	   THEN ''
	 WHEN tipo = 2
	   THEN 'DEVOLUCAO'
	 WHEN tipo = 3
	   THEN 'SIMP REME'
	 WHEN tipo = 4
	   THEN 'ENTRE FUT'
	 WHEN tipo = 5
	   THEN 'RET DEMON'
	 WHEN tipo = 6
	   THEN 'VENDA USA'
	 WHEN tipo = 7
	   THEN 'OUTROS'
	 WHEN tipo = 8
	   THEN 'NF CF'
	 WHEN tipo = 9
	   THEN 'PERD/CONSER'
	 WHEN tipo = 10
	   THEN 'REPOSICAO'
	 WHEN tipo = 11
	   THEN 'RESSARCI'
	 WHEN tipo = 12
	   THEN 'COMODATO'
	 WHEN tipo = 13
	   THEN 'NF EMPRESA'
	 WHEN tipo = 14
	   THEN 'BONIFICA'
	 WHEN tipo = 15
	   THEN 'NFE'
	 ELSE ''
       END                                                AS tipoNotaSaida
FROM sqldados.nf             AS N
  INNER JOIN sqldados.xaprd2 AS X
	       USING (storeno, pdvno, xano)
  LEFT JOIN  sqldados.prdloc AS L
	       ON L.prdno = X.prdno AND L.storeno = 4
  LEFT JOIN  sqldados.emp    AS E
	       ON E.no = N.empno
WHERE N.issuedate >= 20220101
  AND (N.nfse IN (1, 5, 7) OR (N.nfse >= 10) OR (N.nfse IN (1, 3, 5, 7) AND :marca = 2))
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
