USE sqldados;

SET SQL_MODE = '';

DROP TEMPORARY TABLE IF EXISTS T_LOC;
CREATE TEMPORARY TABLE T_LOC
(
  PRIMARY KEY (prdno, grade)
)
SELECT A.prdno AS prdno, A.grade AS grade, TRIM(MID(A.localizacao, 1, 4)) AS localizacao
FROM
  sqldados.prdAdicional AS A
WHERE ((TRIM(MID(A.localizacao, 1, 4)) IN (:local)) OR ('TODOS' IN (:local)) OR (A.localizacao = ''))
  AND (A.storeno = IF(:loja = 0, 4, :loja))
  AND (A.prdno = :prdno OR :prdno = '')
  AND (A.grade = :grade OR :grade = '')
GROUP BY A.prdno, A.grade;

DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (codigo, grade)
)
SELECT X.storeno                                                     AS loja,
       X.pdvno                                                         AS pdvno,
       X.xano                                                          AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)                     AS nota,
       CAST(TRIM(P.no) AS CHAR)                                      AS codigo,
       IFNULL(X.grade, '')                                           AS grade,
       IF(LENGTH(TRIM(P.barcode)) = 13, TRIM(P.barcode), NULL)       AS barcodeProd,
       CASE
         WHEN P.clno BETWEEN 10000 AND 19999 THEN P.barcode
                                             ELSE TRIM(IFNULL(GROUP_CONCAT(DISTINCT BC.barcode SEPARATOR ','), P.barcode))
       END                                                           AS barcodeStrList,
       TRIM(MID(P.name, 1, 37))                                      AS descricao,
       P.mfno                                                        AS vendno,
       IFNULL(F.auxChar1, '')                                        AS fornecedor,
       P.typeno                                                      AS typeno,
       IFNULL(T.name, '')                                            AS typeName,
       CAST(LPAD(P.clno, 6, '0') AS CHAR)                            AS clno,
       IFNULL(cl.name, '')                                           AS clname,
       P.m6                                                          AS altura,
       P.m4                                                          AS comprimento,
       P.m5                                                          AS largura,
       P.sp / 100                                                    AS precoCheio,
       IFNULL(S.ncm, '')                                             AS ncm,
       X.qtty / 1000                                                 AS quantidade,
       X.preco                                                       AS preco,
       (X.qtty / 1000) * X.preco                                     AS total,
       X.c6                                                          AS gradeAlternativa,
       IFNULL(M.marca, 0)                                            AS marca,
       IFNULL(M.impresso, 0)                                         AS marcaImpressao,
       EE.no                                                         AS usernoExp,
       EE.login                                                      AS usuarioExp,
       X.c5                                                          AS dataHoraExp,
       IFNULL(L.localizacao, '')                                     AS local,
       X.c3                                                          AS usuarioSep,
       EC.no                                                         AS usernoCD,
       EC.login                                                      AS usuarioCD,
       X.c4                                                          AS dataHoraCD,
       N.tipo                                                        AS tipoNota,
       ROUND(IFNULL((STK.qtty_atacado + STK.qtty_varejo), 0) / 1000) AS estoque
FROM
  sqldados.prd                           AS P
    INNER JOIN sqldados.xaprd2           AS X
               ON P.no = X.prdno
    LEFT JOIN  sqldados.xaprd2Marca      AS M
               USING (storeno, pdvno, xano, prdno, grade)
    LEFT JOIN  T_LOC                     AS L
               ON L.prdno = X.prdno AND L.grade = X.grade
    LEFT JOIN  sqldados.users            AS EC
               ON EC.no = X.s4
    LEFT JOIN  sqldados.users            AS EE
               ON EE.no = X.s5
    INNER JOIN sqldados.nf               AS N
               ON N.storeno =  X.storeno AND  N.pdvno = X.pdvno AND  N.xano = X.xano
    LEFT JOIN  sqldados.prdbar           AS BC
               ON P.no = BC.prdno AND BC.grade = X.grade AND LENGTH(TRIM(BC.barcode)) = 13
    LEFT JOIN  ( SELECT prdno, grade, SUM(qtty_atacado) AS qtty_atacado, SUM(qtty_varejo) AS qtty_varejo
                 FROM
                   sqldados.stk
                 WHERE storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
                 GROUP BY prdno, grade ) AS STK
               ON X.prdno = STK.prdno AND X.grade = STK.grade
    LEFT JOIN  sqldados.vend             AS F
               ON F.no = P.mfno
    LEFT JOIN  sqldados.type             AS T
               ON T.no = P.typeno
    LEFT JOIN  sqldados.cl
               ON cl.no = P.clno
    LEFT JOIN  sqldados.spedprd          AS S
               ON P.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
  AND (IFNULL(M.marca, 0) = :marca OR :marca = 999)
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
GROUP BY codigo, grade;

SELECT loja,
       pdvno,
       xano,
       nota,
       codigo,
       grade,
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
       estoque
FROM
  T_DADOS
WHERE (:todosLocais = 'S' OR IFNULL(local, '') != '')
