SELECT CAST(TRIM(P.no) AS CHAR)             AS codigo,
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
       MID(IFNULL(L.localizacao, ''), 1, 4) AS localizacao
FROM sqldados.prd            AS P
  LEFT JOIN sqldados.prdbar  AS B
	      ON P.no = B.prdno
  LEFT JOIN sqldados.prdloc  AS L
	      ON L.prdno = P.no AND L.grade = B.grade AND L.storeno = 4
  LEFT JOIN sqldados.vend    AS F
	      ON F.no = P.mfno
  LEFT JOIN sqldados.type    AS T
	      ON T.no = P.typeno
  LEFT JOIN sqldados.cl
	      ON cl.no = P.clno
  LEFT JOIN sqldados.spedprd AS S
	      ON P.no = S.prdno
WHERE (P.no = :prdno OR :prdno = '')
  AND (P.typeno = :typeno OR :typeno = 0)
  AND (P.clno = :clno OR P.deptno = :clno OR P.groupno = :clno OR :clno = 0)
  AND (P.mfno = :vendno OR :vendno = 0)
  AND (L.localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')
GROUP BY codigo, grade