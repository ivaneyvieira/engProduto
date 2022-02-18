SELECT I.invno                    AS ni,
       I.storeno                  AS loja,
       nfname                     AS numero,
       invse                      AS serie,
       ''                         AS nomeForn,
       CAST(I.issue_date AS DATE) AS emissao,
       CAST(NULL AS DATE)         AS entrada,
       0                          AS valorNota,
       'N'                        AS cancelada,
       I.nfekey                   AS chave,
       IFNULL(N.invno, 0)         AS invnoRef
FROM sqldados.invConferencia          AS I
  LEFT JOIN sqldados.iprdConferencia AS P
	       USING (invno)
  LEFT JOIN  sqldados.invnfe          AS N
	       ON N.nfekey = I.nfekey
WHERE (I.nfekey = :chave OR :chave = '')
  AND ifnull(P.s27, 0) = 0
GROUP BY I.invno