USE sqldados;

SELECT I.invno                              AS ni,
       I.storeno                            AS loja,
       I.nfname                             AS numero,
       I.invse                              AS serie,
       I.vendno                             AS fornecedor,
       V.sname                              AS nomeForn,
       CAST(I.issue_date AS DATE)           AS emissao,
       CAST(I.date AS DATE)                 AS entrada,
       I.grossamt / 100                     AS valorNota,
       IF(I.bits & POW(2, 4) = 0, 'N', 'S') AS cancelada,
       N.nfekey                             AS chave
FROM sqldados.inv            AS I
  INNER JOIN sqldados.invnfe AS N
	       USING (invno)
  INNER JOIN sqldados.vend   AS V
	       ON V.no = I.vendno
WHERE I.storeno IN (2, 3, 4, 5)
  AND date >= 20220225
  AND I.type = 0
  AND I.vendno NOT IN (SELECT no FROM sqldados.vend WHERE V.name LIKE 'ENGECOPI%')
  AND I.bits & POW(2, 4) = 0
  AND I.bits & POW(2, 6) = 0
  AND I.cfo NOT IN (1551, 2551, 1556, 2556)
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.invno = :ni OR :ni = 0)
  AND (I.nfname = :nfno OR :nfno = '')
  AND (I.invse = :nfse OR :nfse = '')
  AND (I.vendno = :vendno OR :vendno = 0)
  AND (N.nfekey = :chave OR :chave = '')
GROUP BY I.invno
