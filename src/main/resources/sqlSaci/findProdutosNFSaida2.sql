USE sqldados;

SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (prdno, grade, seq)
)
SELECT X.storeno                                               AS loja,
       X.pdvno                                                 AS pdvno,
       X.xano                                                  AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)               AS nota,
       P.no                                                    AS prdno,
       CAST(TRIM(P.no) AS CHAR)                                AS codigo,
       IFNULL(X.grade, '')                                     AS grade,
       IF(LENGTH(TRIM(P.barcode)) = 13, TRIM(P.barcode), NULL) AS barcodeProd,
       ''                                                      AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))                                AS descricao,
       P.mfno                                                  AS vendno,
       ''                                                      AS fornecedor,
       P.typeno                                                AS typeno,
       ''                                                      AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                      AS clno,
       ''                                                      AS clname,
       P.m6                                                    AS altura,
       P.m4                                                    AS comprimento,
       P.m5                                                    AS largura,
       P.sp / 100                                              AS precoCheio,
       ''                                                      AS ncm,
       X.qtty / 1000                                           AS quantidade,
       X.preco                                                 AS preco,
       (X.qtty / 1000) * X.preco                               AS total,
       X.c6                                                    AS gradeAlternativa,
       IFNULL(M.marca, 0)                                      AS marca,
       IFNULL(M.impresso, 0)                                   AS marcaImpressao,
       0                                                       AS usernoExp,
       ''                                                      AS usuarioExp,
       X.c5                                                    AS dataHoraExp,
       ''                                                      AS local,
       X.c3                                                    AS usuarioSep,
       0                                                       AS usernoCD,
       ''                                                      AS usuarioCD,
       X.c4                                                    AS dataHoraCD,
       N.tipo                                                  AS tipoNota,
       0                                                       AS estoque,
       0                                                       AS quantDev,
       TRUE                                                    AS temProduto,
       TRUE                                                    AS dev,
       X.date                                                  AS dataNota,
       0                                                       AS seq
FROM
  sqldados.prd                      AS P
    INNER JOIN sqldados.xaprd2      AS X
               ON P.no = X.prdno
    LEFT JOIN  sqldados.xaprd2Marca AS M
               USING (storeno, pdvno, xano, prdno, grade)
    INNER JOIN sqldados.nf          AS N
               ON N.storeno = X.storeno AND N.pdvno = X.pdvno AND N.xano = X.xano
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
  AND M.marca = :marca
  AND (X.prdno = :prdno)
  AND (X.grade = :grade)
GROUP BY prdno, grade, seq;

SELECT D.loja,
       pdvno,
       xano,
       nota,
       D.prdno,
       dev,
       dev  AS devDB,
       codigo,
       D.grade,
       local,
       barcodeProd,
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
       preco,
       total,
       gradeAlternativa,
       marca,
       marcaImpressao,
       usernoExp,
       usuarioExp,
       dataHoraExp,
       usernoCD,
       usuarioCD,
       dataHoraCD,
       usuarioSep,
       tipoNota,
       estoque,
       temProduto,
       quantDev,
       D.seq,
       0    AS ni,
       NULL AS dataNi,
       0    AS qtDevNI
FROM
  T_DADOS AS D

