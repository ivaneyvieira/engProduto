SELECT X.storeno                            AS loja,
       pdvno                                AS pdvno,
       xano                                 AS xano,
       CAST(TRIM(P.no) AS CHAR)             AS codigo,
       IFNULL(B.grade, '')                  AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))   AS barcode,
       TRIM(MID(P.name, 1, 37))             AS descricao,
       P.mfno                               AS vendno,
       IFNULL(F.auxChar1, '')               AS fornecedor,
       P.typeno                             AS typeno,
       IFNULL(T.name, '')                   AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)   AS clno,
       IFNULL(cl.name, '')                  AS clname,
       P.m6                                 AS altura,
       P.m4                                 AS comprimento,
       P.m5                                 AS largura,
       P.sp / 100                           AS precoCheio,
       IFNULL(S.ncm, '')                    AS ncm,
       MID(IFNULL(L.localizacao, ''), 1, 4) AS localizacao,
       X.qtty / 1000                        AS quantidade,
       X.preco                              AS preco,
       (X.qtty / 1000) * X.preco            AS total,
       X.c6                                 AS gradeAlternativa,
       X.s12                                AS marca
FROM sqldados.prd             AS P
  INNER JOIN sqldados.xaprd2  AS X
	       ON P.no = X.prdno
  LEFT JOIN  sqldados.prdbar  AS B
	       ON P.no = B.prdno AND B.grade = X.grade
  LEFT JOIN  sqldados.prdloc  AS L
	       ON L.prdno = P.no AND L.grade = B.grade AND L.storeno = 4
  LEFT JOIN  sqldados.vend    AS F
	       ON F.no = P.mfno
  LEFT JOIN  sqldados.type    AS T
	       ON T.no = P.typeno
  LEFT JOIN  sqldados.cl
	       ON cl.no = P.clno
  LEFT JOIN  sqldados.spedprd AS S
	       ON P.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
GROUP BY codigo, grade

