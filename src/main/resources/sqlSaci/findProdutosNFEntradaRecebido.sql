DROP TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV (
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT N.nfekey,
       prdno,
       grade,
       P.qtty / 1000 AS qtty
FROM sqldados.invnfe                 AS N
  INNER JOIN sqldados.inv            AS I
	       USING (invno)
  INNER JOIN sqldados.iprd           AS P
	       USING (invno)
  INNER JOIN sqldados.invConferencia AS C
	       USING (nfekey)
GROUP BY nfekey, prdno, grade;

SELECT N.storeno                                          AS loja,
       X.invno                                            AS ni,
       CAST(CONCAT(N.nfname, '/', N.invse) AS CHAR)       AS nota,
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
       P.sp / 100                                         AS precoCheio,
       IFNULL(S.ncm, '')                                  AS ncm,
       X.qtty / 1000                                      AS quantidade,
       X.fob / 100                                        AS preco,
       (X.qtty / 1000) * (X.fob / 100)                    AS total,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       IFNULL(TI.qtty, 0) / 1000                          AS qttyRef
FROM sqldados.prd             AS P
  INNER JOIN sqldados.iprd    AS X
	       ON P.no = X.prdno
  INNER JOIN sqldados.inv     AS N
	       USING (invno)
  INNER JOIN sqldados.invnfe  AS K
	       USING (invno)
  LEFT JOIN  T_INV            AS TI
	       USING (nfekey, prdno, grade)
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
WHERE X.invno = :ni
GROUP BY codigo, grade
HAVING quantidade = qttyRef

