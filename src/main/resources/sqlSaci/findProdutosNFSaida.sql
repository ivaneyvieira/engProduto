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
  AND (L.storeno = :lojaLocal OR :lojaLocal = 0)
GROUP BY prdno, loc;


DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (codigo, grade, local)
)
SELECT X.storeno                                     AS loja,
       pdvno                                         AS pdvno,
       xano                                          AS xano,
       CAST(CONCAT(PXA.nfno, '/', PXA.nfse) AS CHAR) AS nota,
       CAST(TRIM(PRD.no) AS CHAR)                    AS codigo,
       IFNULL(X.grade, '')                           AS grade,
       TRIM(IFNULL(B.barcode, PRD.barcode))          AS barcode,
       TRIM(MID(PRD.name, 1, 37))                    AS descricao,
       PRD.mfno                                      AS vendno,
       IFNULL(F.auxChar1, '')                        AS fornecedor,
       PRD.typeno                                    AS typeno,
       IFNULL(T.name, '')                            AS typeName,
       CAST(LPAD(PRD.clno, 6, '0') AS CHAR)          AS clno,
       IFNULL(cl.name, '')                           AS clname,
       PRD.m6                                        AS altura,
       PRD.m4                                        AS comprimento,
       PRD.m5                                        AS largura,
       PRD.sp / 100                                  AS precoCheio,
       IFNULL(S.ncm, '')                             AS ncm,
       X.qtty / 1000                                 AS quantidade,
       X.ls_price / 100                              AS preco,
       (X.qtty / 1000) * (X.ls_price / 100)          AS total,
       A.gradeAlternativa                            AS gradeAlternativa,
       A.marca                                       AS marca,
       A.usuarioExp                                  AS usuarioExp,
       CAST(L.loc AS CHAR)                           AS local,
       A.usuarioCD                                   AS usuarioCD,
       IFNULL(N1.tipo, '')                           AS tipoNota
FROM sqlpdv.pxa AS PXA
       INNER JOIN sqlpdv.pxaprd AS X
                  USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.nfSaidaAdiciona AS A
                 USING (storeno, pdvno, xano, prdno, grade)
       LEFT JOIN sqlpdv.pxanf AS N1
                 USING (storeno, pdvno, xano)
       LEFT JOIN sqldados.prd AS PRD
                 ON PRD.no = X.prdno
       INNER JOIN T_LOC AS L
                  ON L.prdno = PRD.no
       LEFT JOIN sqldados.prdbar AS B
                 ON PRD.no = B.prdno AND B.grade = X.grade
       LEFT JOIN sqldados.vend AS F
                 ON F.no = PRD.mfno
       LEFT JOIN sqldados.type AS T
                 ON T.no = PRD.typeno
       LEFT JOIN sqldados.cl
                 ON cl.no = PRD.clno
       LEFT JOIN sqldados.spedprd AS S
                 ON PRD.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
  AND (IFNULL(A.marca, '') = :marca OR :marca = 999)
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
