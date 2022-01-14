SELECT X.storeno                                          AS loja,
       ordno                                              AS ordno,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)          AS nota,
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
       X.price / 100                                      AS preco,
       (X.qtty / 1000) * X.price / 100                    AS total,
       X.c2                                               AS gradeAlternativa,
       X.s12                                              AS marca,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c4                                               AS usuarioCD,
       ROUND(IFNULL(S.qtty_varejo, 0) / 1000)             AS estoque
FROM sqldados.prd             AS P
  INNER JOIN sqldados.eoprd   AS X
	       ON P.no = X.prdno
  INNER JOIN sqldados.eord    AS N
	       USING (storeno, ordno)
  LEFT JOIN  sqldados.stk     AS S
	       ON S.prdno = X.prdno AND S.grade = X.grade AND S.storeno = 4
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
  AND (X.s12 = :marca OR :marca = 999)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY codigo, grade

