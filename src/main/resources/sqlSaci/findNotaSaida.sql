SELECT storeno                 AS loja,
       pdvno                   AS pdvno,
       xano                    AS xano,
       N.nfno                  AS numero,
       N.nfse                  AS serie,
       custno                  AS cliente,
       CAST(issuedate AS DATE) AS data,
       N.empno                 AS vendedor
FROM sqldados.nf             AS N
  INNER JOIN sqldados.xaprd2 AS X
	       USING (storeno, pdvno, xano)
WHERE N.issuedate >= 20220101
  AND N.status <> 1
  AND (N.nfse IN (1, 3, 5, 7))
  AND (X.s12 = :marca OR :marca = 999)
  AND (storeno = :storeno OR :storeno = 0)
  AND (N.nfno = :nfno OR :nfno = 0)
  AND (N.nfse = :nfse OR :nfse = '')
GROUP BY storeno, pdvno, xano