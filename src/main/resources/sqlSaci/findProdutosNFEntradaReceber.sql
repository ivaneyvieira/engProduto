DROP TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV (
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT N.nfekey,
       I.storeno,
       I.invno,
       I.nfname,
       I.invse,
       prdno,
       grade,
       P.qtty,
       P.fob
FROM sqldados.invnfe                 AS N
  INNER JOIN sqldados.inv            AS I
	       USING (invno)
  INNER JOIN sqldados.iprd           AS P
	       USING (invno)
WHERE N.nfekey = :nfekey
GROUP BY nfekey, prdno, grade;

SELECT IFNULL(TI.storeno, 0)                                      AS loja,
       IFNULL(TI.invno, 0)                                        AS ni,
       N.nfekey                                                   AS chave,
       IFNULL(CAST(CONCAT(TI.nfname, '/', TI.invse) AS CHAR), '') AS nota,
       CAST(TRIM(X.prdno) AS CHAR)                                AS codigo,
       IFNULL(X.grade, '')                                        AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))                         AS barcode,
       TRIM(MID(P.name, 1, 37))                                   AS descricao,
       P.mfno                                                     AS vendno,
       IFNULL(F.auxChar1, '')                                     AS fornecedor,
       P.typeno                                                   AS typeno,
       IFNULL(T.name, '')                                         AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                         AS clno,
       IFNULL(cl.name, '')                                        AS clname,
       P.sp / 100                                                 AS precoCheio,
       IFNULL(S.ncm, '')                                          AS ncm,
       X.qtty / 1000                                              AS quantidade,
       TI.fob / 100                                               AS preco,
       (TI.qtty / 1000) * (TI.fob / 100)                          AS total,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR)         AS localizacao,
       TI.qtty / 1000                                             AS qttyRef,
       X.marca                                                    AS marca
FROM sqldados.prd                     AS P
  INNER JOIN sqldados.iprdConferencia AS X
	       ON P.no = X.prdno
  INNER JOIN sqldados.invConferencia  AS N
	       USING (nfekey)
  LEFT JOIN  T_INV                    AS TI
	       USING (nfekey, prdno, grade)
  LEFT JOIN  sqldados.prdbar          AS B
	       ON P.no = B.prdno AND B.grade = X.grade
  LEFT JOIN  sqldados.prdloc          AS L
	       ON L.prdno = P.no AND L.storeno = 4
  LEFT JOIN  sqldados.vend            AS F
	       ON F.no = P.mfno
  LEFT JOIN  sqldados.type            AS T
	       ON T.no = P.typeno
  LEFT JOIN  sqldados.cl
	       ON cl.no = P.clno
  LEFT JOIN  sqldados.spedprd         AS S
	       ON P.no = S.prdno
WHERE X.nfekey = :nfekey
  AND X.marca = 0
GROUP BY codigo, grade

