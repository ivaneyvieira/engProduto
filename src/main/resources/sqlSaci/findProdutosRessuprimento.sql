SELECT X.storeno                                          AS loja,
       ordno                                              AS ordno,
       CAST(TRIM(P.no) AS CHAR)                           AS codigo,
       IFNULL(X.grade, '')                                AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))                 AS barcode,
       TRIM(MID(P.name, 1, 37))                           AS descricao,
       P.mfno                                             AS vendno,
       IFNULL(F.auxChar1, '')                             AS fornecedor,
       P.typeno                                           AS typeno,
       IFNULL(T.name, '')                                 AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                 AS clno,
       IFNULL(cl.name, '')                                AS clname,
       P.m6                                               AS altura,
       P.m4                                               AS comprimento,
       P.m5                                               AS largura,
       P.sp / 100                                         AS precoCheio,
       IFNULL(S.ncm, '')                                  AS ncm,
       X.qtty / 1000                                      AS quantidade,
       X.cost                                             AS preco,
       (X.qtty / 1000) * X.cost                           AS total,
       X.auxShort4                                        AS marca,
       X.remarks                                          AS usuarioExp,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.obs                                              AS usuarioCD
FROM sqldados.prd             AS P
  INNER JOIN sqldados.oprd    AS X
	       ON P.no = X.prdno
  INNER JOIN sqldados.ords    AS N
	       ON N.storeno = X.storeno AND N.no = X.ordno
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
WHERE X.storeno = :storeno
  AND X.ordno = :ordno
  AND (X.auxShort4 = :marca OR :marca = 999)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY codigo, grade

