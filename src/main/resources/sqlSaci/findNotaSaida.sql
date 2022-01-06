SELECT storeno                 AS loja,
       pdvno                   AS pdvno,
       xano                    AS xano,
       nfno                    AS numero,
       nfse                    AS serie,
       custno                  AS cliente,
       CAST(issuedate AS DATE) AS data,
       empno                   AS vendedor
FROM sqldados.nf AS N
WHERE N.issuedate >= 20220101
  AND N.status <> 1
  AND (nfse IN (1, 3, 5, 7))
  AND (storeno = :storeno OR :storeno = 0)
  AND (nfno = :nfno OR :nfno = 0)
  AND (nfse = :nfse OR :nfse = '')