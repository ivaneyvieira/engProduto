DO @QUERY := :query;
DO @QUERYLIKE := CONCAT(@QUERY, '%');
DO @QUERYNUM := IF(@QUERY REGEXP '^[0-9]+$', LPAD(@QUERY, 16, ' '), '');

SELECT CAST(TRIM(P.no) AS CHAR)           AS codigo,
       IFNULL(B.grade, '')                AS grade,
       TRIM(IFNULL(B.barcode, P.barcode)) AS barcode,
       TRIM(MID(P.name, 1, 37))           AS descricao,
       P.mfno                             AS vendno,
       IFNULL(F.auxChar1, '')             AS fornecedor,
       P.typeno                           AS typeno,
       IFNULL(T.name, '')                 AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR) AS clno,
       cl.name                            AS clname,
       P.m6                               AS altura,
       P.m4                               AS comprimento,
       P.m5                               AS largura,
       P.sp / 100                         AS precoCheio,
       IFNULL(S.ncm, '')                  AS ncm
FROM sqldados.prd            AS P
  LEFT JOIN sqldados.prdbar  AS B
	      ON P.no = B.prdno
  LEFT JOIN sqldados.vend    AS F
	      ON F.no = P.mfno
  LEFT JOIN sqldados.type    AS T
	      ON T.no = P.typeno
  LEFT JOIN sqldados.cl
	      ON cl.no = P.clno
  LEFT JOIN sqldados.spedprd AS S
	      ON P.no = S.prdno
WHERE (P.no = @QUERYNUM)
   OR (P.name LIKE @QUERYLIKE AND @QUERYNUM != '')
   OR (@QUERY = '')
GROUP BY codigo, grade

