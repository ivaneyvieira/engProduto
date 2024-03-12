USE sqldados;

DO @DATA := SUBDATE(CURRENT_DATE, 30) * 1;

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT S.prdno                                               AS prdno,
       S.grade                                               AS grade,
       COALESCE(A.localizacao, MID(L.localizacao, 1, 4), '') AS localizacao
FROM sqldados.stk AS S
       LEFT JOIN sqldados.prdloc AS L
                 ON S.storeno = L.storeno
                   AND S.prdno = L.prdno
                   AND S.grade = L.grade
       LEFT JOIN sqldados.prdAdicional AS A
                 ON S.storeno = A.storeno
                   AND S.prdno = A.prdno
                   AND S.grade = A.grade
                   AND A.localizacao != ''
WHERE S.storeno = 4
GROUP BY S.storeno, S.prdno, S.grade;

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO_NOTA;
CREATE TEMPORARY TABLE T_PEDIDO_NOTA
(
  PRIMARY KEY (storeno, ordno, prdno, grade)
)
SELECT 1                                                      AS storeno,
       N.l2                                                   AS ordno,
       X.prdno                                                AS prdno,
       X.grade                                                AS grade,
       MID(MAX(CONCAT(LPAD(N.issuedate, 8, '0'), ':',
                      CONCAT(N.nfno, '/', N.nfse))), 10, 100) AS numero,
       MAX(CAST(N.issuedate AS DATE))                         AS dataNota,
       SUM(X.qtty / 1000)                                     AS qtty
FROM sqldados.nf AS N
       INNER JOIN sqldados.xaprd2 AS X
                  USING (storeno, pdvno, xano)
WHERE N.l2 BETWEEN 100000000 AND 999999999
  AND N.issuedate >= @DATA
  AND N.issuedate >= 20240226
  AND N.status <> 1
GROUP BY N.l2, X.prdno, X.grade;

DROP TEMPORARY TABLE IF EXISTS T_ORDS;
CREATE TEMPORARY TABLE T_ORDS
(
  PRIMARY KEY (storeno, no)
)
SELECT *
FROM (SELECT *
      FROM sqldados.ords
      WHERE storeno = 1
        AND no = :ordno
      UNION
      DISTINCT
      SELECT *
      FROM sqldados.ordsRessu
      WHERE storeno = 1
        AND no = :ordno) AS D
GROUP BY storeno, no;

DROP TEMPORARY TABLE IF EXISTS T_OPRD;
CREATE TEMPORARY TABLE T_OPRD
(
  PRIMARY KEY (storeno, ordno, prdno, grade, seqno)
)
SELECT *
FROM (SELECT *
      FROM sqldados.oprd
      WHERE storeno = 1
        AND ordno = :ordno
      UNION
      DISTINCT
      SELECT *
      FROM sqldados.oprdRessu
      WHERE storeno = 1
        AND ordno = :ordno) AS D
GROUP BY storeno, ordno, prdno, grade, seqno;

SELECT X.ordno                                                     AS ordno,
       CAST(TRIM(P.no) AS CHAR)                                    AS codigo,
       IFNULL(X.grade, '')                                         AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))                          AS barcode,
       TRIM(MID(P.name, 1, 37))                                    AS descricao,
       P.mfno                                                      AS vendno,
       IFNULL(F.auxChar1, '')                                      AS fornecedor,
       P.typeno                                                    AS typeno,
       IFNULL(T.name, '')                                          AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                          AS clno,
       IFNULL(cl.name, '')                                         AS clname,
       P.m6                                                        AS altura,
       P.m4                                                        AS comprimento,
       P.m5                                                        AS largura,
       P.sp / 100                                                  AS precoCheio,
       X.qtty                                                      AS qtPedido,
       TN.qtty                                                     AS qtQuantNF,
       X.qtty * S.cm_real / 10000                                  AS vlPedido,
       TN.qtty * S.cm_real / 10000                                 AS vlQuantNF,
  /*IF(X.auxLong2 = 0, X.qtty, X.auxLong2)  AS qtEntregue,*/
       IF(X.auxLong1 = 0, TN.qtty, X.auxLong1)                     AS qtRecebido,
       IF(X.auxLong1 = 0, TN.qtty, X.auxLong1) * S.cm_real / 10000 AS vlRecebido,
       X.cost                                                      AS preco,
       (X.qtty * X.mult / 1000) * X.cost                           AS total,
       X.auxShort4                                                 AS marca,
       X.auxShort3                                                 AS selecionado,
       X.auxLong4                                                  AS posicao,
       L.localizacao                                               AS localizacao,
       ROUND(IFNULL(S.qtty_varejo + S.qtty_atacado, 0) / 1000)     AS estoque,
       SUBSTRING_INDEX(X.obs, ':', 1)                              AS codigoCorrecao,
       TRIM(MID(PR.name, 1, 37))                                   AS descricaoCorrecao,
       SUBSTRING_INDEX(SUBSTRING_INDEX(X.obs, ':', 2), ':', -1)    AS gradeCorrecao,
       TN.numero                                                   AS numeroNota,
       TN.dataNota                                                 AS dataNota
FROM T_OPRD AS X
       INNER JOIN T_ORDS AS N
                  ON N.storeno = X.storeno
                    AND N.no = X.ordno
       INNER JOIN sqldados.prd AS P
                  ON P.no = X.prdno
       LEFT JOIN T_PEDIDO_NOTA AS TN
                 ON TN.storeno = X.storeno
                   AND TN.ordno = X.ordno
                   AND TN.prdno = X.prdno
                   AND TN.grade = X.grade
       LEFT JOIN sqldados.stk AS S
                 ON S.prdno = X.prdno
                   AND S.grade = X.grade
                   AND S.storeno = 4
       LEFT JOIN sqldados.prdbar AS B
                 ON P.no = B.prdno
                   AND B.grade = X.grade
       LEFT JOIN T_LOC AS L
                 ON X.prdno = L.prdno
                   AND X.grade = L.grade
       LEFT JOIN sqldados.vend AS F
                 ON F.no = P.mfno
       LEFT JOIN sqldados.type AS T
                 ON T.no = P.typeno
       LEFT JOIN sqldados.cl
                 ON cl.no = P.clno
       LEFT JOIN sqldados.prd AS PR
                 ON PR.no = LPAD(SUBSTRING_INDEX(X.obs, ':', 1), 16, ' ')
WHERE X.storeno = 1
  AND X.ordno = :ordno
  AND (X.auxShort4 = :marca)
  AND (L.localizacao IN (:locais) OR 'TODOS' IN (:locais))
  AND (IFNULL(L.localizacao, '') = :locApp OR :locApp = 'TODOS')
GROUP BY codigo, IFNULL(X.grade, '')
HAVING CASE :marca
         WHEN 0 THEN TRUE
         WHEN 1 THEN qtQuantNF IS NOT NULL
         WHEN 2 THEN qtQuantNF IS NOT NULL
         ELSE FALSE
       END
/*
update sqldados.oprdRessu
set auxLong1 = 0
where storeno = 1
  and ordno > 100000000

update sqldados.oprd
set auxLong1 = 0
where storeno = 1
  and ordno > 100000000
*/
