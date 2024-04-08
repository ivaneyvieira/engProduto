DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, loc)
)
SELECT P.no AS prdno, CAST(MID(IFNULL(L.localizacao, '****'), 1, 4) AS CHAR) AS loc
FROM sqldados.prd AS P
       LEFT JOIN sqldados.prdloc AS L
                 ON P.no = L.prdno
WHERE (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
  AND (L.storeno = :storeno OR :lojaLocal = 0)
GROUP BY prdno, loc;


DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (codigo, grade, local)
)
SELECT X.storeno                                 AS loja,
       pdvno                                     AS pdvno,
       xano                                      AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR) AS nota,
       CAST(TRIM(P.no) AS CHAR)                  AS codigo,
       IFNULL(X.grade, '')                       AS grade,
       TRIM(IFNULL(B.barcode, P.barcode))        AS barcode,
       TRIM(MID(P.name, 1, 37))                  AS descricao,
       P.mfno                                    AS vendno,
       IFNULL(F.auxChar1, '')                    AS fornecedor,
       P.typeno                                  AS typeno,
       IFNULL(T.name, '')                        AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)        AS clno,
       IFNULL(cl.name, '')                       AS clname,
       P.m6                                      AS altura,
       P.m4                                      AS comprimento,
       P.m5                                      AS largura,
       P.sp / 100                                AS precoCheio,
       IFNULL(S.ncm, '')                         AS ncm,
       X.qtty / 1000                             AS quantidade,
       X.preco                                   AS preco,
       (X.qtty / 1000) * X.preco                 AS total,
       X.c6                                      AS gradeAlternativa,
       X.s11                                     AS marca,
       X.c5                                      AS usuarioExp,
       CAST(L.loc AS CHAR)                       AS local,
       X.c4                                      AS usuarioCD,
       N.tipo                                    AS tipoNota
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
  AND (X.s11 = :marca OR :marca = 999)
GROUP BY codigo, grade, local;

SELECT loja,
       pdvno,
       xano,
       nota,
       codigo,
       grade,
       local,
       barcode,
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
       preco,
       total,
       gradeAlternativa,
       marca,
       usuarioExp,
       usuarioCD,
       tipoNota
FROM T_DADOS
