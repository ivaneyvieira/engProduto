DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, loc)
)
SELECT P.no                                                                AS prdno,
       CAST(MID(COALESCE(A.localizacao, L.localizacao, ''), 1, 4) AS CHAR) AS loc
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdloc AS L
                 ON P.no = L.prdno
       LEFT JOIN sqldados.prdAdicional AS A
                 ON A.prdno = L.prdno
                   AND A.grade = L.grade
                   AND A.storeno = L.storeno
WHERE (MID(COALESCE(A.localizacao, L.localizacao, ''), 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND (L.storeno = :lojaLocal OR :lojaLocal = 0)
GROUP BY prdno, loc;

DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (codigo, grade, local)
)
SELECT X.storeno                                                               AS loja,
       pdvno                                                                   AS pdvno,
       xano                                                                    AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)                               AS nota,
       CAST(TRIM(P.no) AS CHAR)                                                AS codigo,
       P.no                                                                    AS prdno,
       IFNULL(X.grade, '')                                                     AS grade,
       TRIM(IFNULL(GROUP_CONCAT(DISTINCT B.barcode SEPARATOR ','), P.barcode)) AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))                                                AS descricao,
       P.mfno                                                                  AS vendno,
       IFNULL(F.auxChar1, '')                                                  AS fornecedor,
       P.typeno                                                                AS typeno,
       IFNULL(T.name, '')                                                      AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                                      AS clno,
       IFNULL(cl.name, '')                                                     AS clname,
       P.m6                                                                    AS altura,
       P.m4                                                                    AS comprimento,
       P.m5                                                                    AS largura,
       P.sp / 100                                                              AS precoCheio,
       IFNULL(S.ncm, '')                                                       AS ncm,
       ROUND(X.qtty / 1000)                                                    AS quantidade,
       ROUND(X.l12 / 1000)                                                     AS quantidadeEdt,
       X.preco                                                                 AS preco,
       (X.qtty / 1000) * X.preco                                               AS total,
       X.c6                                                                    AS gradeAlternativa,
       X.s11                                                                   AS marca,
       X.c5                                                                    AS usuarioExp,
       CAST(L.loc AS CHAR)                                                     AS local,
       X.c4                                                                    AS usuarioCD,
       N.tipo                                                                  AS tipoNota,
       X.l12 > 0 AND X.l12 < X.qtty                                            AS pendente
FROM sqldados.prd AS P
       INNER JOIN T_LOC AS L
                  ON L.prdno = P.no
       INNER JOIN sqldados.xaprd2 AS X
                  ON P.no = X.prdno
       INNER JOIN sqldados.nf AS N
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.prdbar AS B
                 ON P.no = B.prdno AND B.grade = X.grade
       LEFT JOIN sqldados.vend AS F
                 ON F.no = P.mfno
       LEFT JOIN sqldados.type AS T
                 ON T.no = P.typeno
       LEFT JOIN sqldados.cl
                 ON cl.no = P.clno
       LEFT JOIN sqldados.spedprd AS S
                 ON P.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
  AND CASE :marca
        WHEN 0 THEN (X.s11 = 0) OR (X.l12 >= 0 AND X.l12 < X.qtty)
        WHEN 1 THEN (X.s11 = 1) OR (X.l12 > 0 AND X.l12 <= X.qtty)
        WHEN 2 THEN (X.s11 = 2) OR (X.l12 = X.qtty)
        WHEN 999 THEN X.s11 IN (0, 1, 2)
        ELSE FALSE
      END
GROUP BY codigo, grade, local;

SELECT loja,
       pdvno,
       xano,
       nota,
       codigo,
       prdno,
       grade,
       local,
       barcodeStrList,
       descricao,
       vendno,
       fornecedor,
       typeno,
       typeName,
       clno,
       clname,
       altura,
       comprimento,
       largura,
       precoCheio,
       ncm,
       quantidade,
       quantidadeEdt,
       preco,
       total,
       gradeAlternativa,
       marca,
       usuarioExp,
       usuarioCD,
       tipoNota,
       pendente
FROM T_DADOS
