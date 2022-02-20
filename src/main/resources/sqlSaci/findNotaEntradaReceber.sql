SELECT IFNULL(NI.invno, 0)                               AS ni,
       IFNULL(NI.storeno, 0)                             AS loja,
       IFNULL(NI.nfname, '')                             AS numero,
       IFNULL(NI.invse, '')                              AS serie,
       IFNULL(V.sname, '')                               AS nomeForn,
       CAST(NI.issue_date AS DATE)                       AS emissao,
       CAST(NI.date AS DATE)                             AS entrada,
       0                                                 AS valorNota,
       IFNULL(IF(NI.bits & POW(2, 4) = 0, 'N', 'S'), '') AS cancelada,
       I.nfekey                                          AS chave
FROM sqldados.invConferencia         AS I
  LEFT JOIN sqldados.iprdConferencia AS P
	      USING (nfekey)
  LEFT JOIN sqldados.invnfe          AS N
	      ON N.nfekey = I.nfekey
  LEFT JOIN sqldados.inv             AS NI
	      USING (invno)
  LEFT JOIN sqldados.vend            AS V
	      ON V.no = NI.vendno
WHERE (I.nfekey = :chave OR :chave = '')
  AND IFNULL(P.marca, 0) = 0
GROUP BY I.nfekey