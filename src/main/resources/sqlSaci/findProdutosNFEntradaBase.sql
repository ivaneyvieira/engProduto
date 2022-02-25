SELECT N.storeno                                          AS loja,
       X.invno                                            AS ni,
       K.nfekey                                           AS chave,
       CAST(CONCAT(N.nfname, '/', N.invse) AS CHAR)       AS nota,
       CAST(TRIM(P.no) AS CHAR)                           AS codigo,
       IFNULL(X.grade, '')                                AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))                 AS barcode,
       TRIM(P.mfno_ref)                                   AS referencia,
       IF(tipoGarantia = 2, garantia, NULL)               AS mesesGarantia,
       ROUND(qttyPackClosed / 1000)                       AS quantidadePacote,
       TRIM(MID(P.name, 1, 37))                           AS descricao,
       P.mfno                                             AS vendno,
       IFNULL(F.auxChar1, '')                             AS fornecedor,
       P.typeno                                           AS typeno,
       IFNULL(T.name, '')                                 AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                 AS clno,
       IFNULL(cl.name, '')                                AS clname,
       P.sp / 100                                         AS precoCheio,
       IFNULL(S.ncm, '')                                  AS ncm,
       X.qtty / 1000                                      AS quantidade,
       X.fob / 100                                        AS preco,
       (X.qtty / 1000) * (X.fob / 100)                    AS total,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       IFNULL(0, 0) / 1000                                AS qttyRef,
       0                                                  AS marca
FROM sqldados.prd             AS P
  INNER JOIN sqldados.iprd    AS X
	       ON P.no = X.prdno
  INNER JOIN sqldados.inv     AS N
	       USING (invno)
  INNER JOIN sqldados.invnfe  AS K
	       USING (invno)
  LEFT JOIN  sqldados.prdbar  AS B
	       ON P.no = B.prdno AND B.grade = X.grade
  LEFT JOIN  sqldados.prdloc  AS L
	       ON L.prdno = P.no AND L.storeno = 4
  LEFT JOIN  sqldados.vend    AS F
	       ON F.no = P.mfno
  LEFT JOIN  sqldados.type    AS T
	       ON T.no = P.typeno
  LEFT JOIN  sqldados.cl
	       ON cl.no = P.clno
  LEFT JOIN  sqldados.spedprd AS S
	       ON P.no = S.prdno
WHERE nfekey = :nfekey
GROUP BY codigo, grade

