SELECT I.invno                    AS ni,
       I.storeno                  AS loja,
       nfname                     AS numero,
       invse                      AS serie,
       0                          AS fornecedor,
       CAST(I.issue_date AS DATE) AS data,
       0                          AS valorNota,
       'N'                        AS cancelada,
       I.nfekey                   AS chave
FROM sqldados.invConferencia AS I
WHERE (I.nfekey = :chave OR :chave = '')