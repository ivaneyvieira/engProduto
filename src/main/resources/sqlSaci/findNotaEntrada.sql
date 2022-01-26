SELECT I.invno                              AS ni,
       I.storeno                            AS loja,
       nfname                               AS numero,
       invse                                AS serie,
       I.vendno                             AS fornecedor,
       CAST(I.issue_date AS DATE)           AS data,
       I.grossamt / 100                     AS valorNota,
       IF(I.bits & POW(2, 4) = 0, 'N', 'S') AS cancelada,
       N.nfekey                             AS chave
FROM sqldados.inv            AS I
  INNER JOIN sqldados.invnfe AS N
	       USING (invno)
WHERE I.storeno IN (2, 3, 4, 5)
  AND date >= 20220101
  AND (N.nfekey = :chave OR :chave = '')