USE sqldados;

DROP TABLE IF EXISTS T_INV_CONF;
CREATE TEMPORARY TABLE T_INV_CONF (
  PRIMARY KEY (nfekey, prdno, grade)
)
SELECT :ni    AS invno,
       I.nfekey,
       prdno,
       grade,
       P.qtty AS qtty,
       P.s27  AS marca
FROM sqldados.invConferencia          AS I
  INNER JOIN sqldados.iprdConferencia AS P
	       USING (invno)
WHERE P.s27 = 1
GROUP BY nfekey, prdno, grade;

DROP TABLE IF EXISTS T_INV_SACI;
CREATE TEMPORARY TABLE T_INV_SACI (
  PRIMARY KEY (invno, prdno, grade)
)
SELECT I.invno,
       I.storeno,
       CAST(CONCAT(I.nfname, '/', I.invse) AS CHAR) AS nota,
       prdno,
       grade,
       P.qtty,
       P.fob
FROM sqldados.inv            AS I
  INNER JOIN sqldados.iprd   AS P
	       USING (invno)
  INNER JOIN sqldados.invnfe AS K
	       USING (invno)
  LEFT JOIN  T_INV_CONF      AS TI
	       USING (nfekey, prdno, grade)
WHERE P.invno = :ni
  AND TI.nfekey IS NOT NULL
GROUP BY invno, prdno, grade;

DROP TABLE IF EXISTS T_INV_PEND;
CREATE TEMPORARY TABLE T_INV_PEND (
  PRIMARY KEY (invno, prdno, grade)
)
SELECT C.invno,
       0    AS storeno,
       ''   AS nota,
       C.prdno,
       C.grade,
       C.qtty,
       NULL AS fob
FROM T_INV_CONF           AS C
  LEFT JOIN sqldados.iprd AS P
	      USING (invno, prdno, grade)
WHERE P.invno IS NULL
GROUP BY C.invno, C.prdno, C.grade;


DROP TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV (
  PRIMARY KEY (invno, prdno, grade)
)
SELECT invno,
       storeno,
       nota,
       prdno,
       grade,
       qtty,
       fob
FROM T_INV_PEND
UNION
SELECT invno,
       storeno,
       nota,
       prdno,
       grade,
       qtty,
       fob
FROM T_INV_SACI;

SELECT N.storeno                                          AS loja,
       N.invno                                            AS ni,
       N.nota                                             AS nota,
       CAST(TRIM(P.no) AS CHAR)                           AS codigo,
       IFNULL(N.grade, '')                                AS grade,
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
       N.qtty / 1000                                      AS quantidade,
       N.fob / 100                                        AS preco,
       (N.qtty / 1000) * (N.fob / 100)                    AS total,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       IFNULL(N.qtty, 0) / 1000                           AS qttyRef,
       1                                                  AS marca
FROM T_INV                    AS N
  INNER JOIN sqldados.prd     AS P
	       ON P.no = N.prdno
  LEFT JOIN  sqldados.prdbar  AS B
	       ON P.no = B.prdno AND B.grade = N.grade
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
GROUP BY codigo, grade

