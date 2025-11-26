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

DROP TEMPORARY TABLE IF EXISTS T_STK;
CREATE TEMPORARY TABLE T_STK
(
  PRIMARY KEY (prdno, grade)
)
SELECT prdno, grade, SUM(qtty_atacado) AS qtty_atacado, SUM(qtty_varejo) AS qtty_varejo
FROM
  sqldados.stk
WHERE storeno IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND (prdno = :prdno OR :prdno = '')
  AND (grade = :grade OR :grade = '')
GROUP BY prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_DADOS;
CREATE TEMPORARY TABLE T_DADOS
(
  PRIMARY KEY (prdno, grade)
)
SELECT X.storeno                                                     AS loja,
       X.pdvno                                                       AS pdvno,
       X.xano                                                        AS xano,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)                     AS nota,
       P.no                                                          AS prdno,
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
       ROUND(IFNULL((STK.qtty_atacado + STK.qtty_varejo), 0) / 1000) AS estoque,
       IFNULL(D.quantDev, ROUND(X.qtty / 1000))                      AS quantDev,
       IFNULL(D.temProduto, FALSE)                                   AS temProduto,
       IFNULL(dev, FALSE)                                            AS dev,
       X.date                                                        AS dataNota
FROM
  sqldados.prd                          AS P
    INNER JOIN sqldados.xaprd2          AS X
               ON P.no = X.prdno
    LEFT JOIN  sqldados.xaprd2Marca     AS M
               USING (storeno, pdvno, xano, prdno, grade)
    LEFT JOIN  sqldados.xaprd2Devolucao AS D
               USING (storeno, pdvno, xano, prdno, grade)
    LEFT JOIN  T_LOC                    AS L
               ON L.prdno = X.prdno AND L.grade = X.grade
    LEFT JOIN  sqldados.users           AS EC
               ON EC.no = X.s4
    LEFT JOIN  sqldados.users           AS EE
               ON EE.no = X.s5
    INNER JOIN sqldados.nf              AS N
               ON N.storeno = X.storeno AND N.pdvno = X.pdvno AND N.xano = X.xano
    LEFT JOIN  sqldados.prdbar          AS BC
               ON P.no = BC.prdno AND BC.grade = X.grade AND LENGTH(TRIM(BC.barcode)) = 13
    LEFT JOIN  T_STK                    AS STK
               ON X.prdno = STK.prdno AND X.grade = STK.grade
    LEFT JOIN  sqldados.vend            AS F
               ON F.no = P.mfno
    LEFT JOIN  sqldados.type            AS T
               ON T.no = P.typeno
    LEFT JOIN  sqldados.cl
               ON cl.no = P.clno
    LEFT JOIN  sqldados.spedprd         AS S
               ON P.no = S.prdno
WHERE X.storeno = :storeno
  AND X.pdvno = :pdvno
  AND X.xano = :xano
  AND (IFNULL(M.marca, 0) = :marca OR :marca = 999)
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
GROUP BY prdno, grade;

/************************************************************************/
DROP TEMPORARY TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
(
  INDEX v1 (storenoE, pdvnoE, xanoE),
  INDEX v2 (storenoE, nfnoE, nfseE),
  INDEX v3 (loja, obsNI)
)
SELECT N.storeno                     AS loja,
       N.pdvno                       AS pdv,
       N.xano                        AS transacao,
       CAST(MID(CASE
                  WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
                  WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                           ELSE ''
                END, 1, 60) AS CHAR) AS obsNI,
       IFNULL(EF.storeno, N.storeno) AS storenoE,
       IFNULL(EF.nfno, N.nfno)       AS nfnoE,
       IFNULL(EF.nfse, N.nfse)       AS nfseE,
       IFNULL(EF.pdvno, N.pdvno)     AS pdvnoE,
       IFNULL(EF.xano, N.xano)       AS xanoE
FROM
  sqldados.nf                        AS N
    LEFT JOIN sqldados.nfAutorizacao AS AT
              ON AT.storeno = N.storeno AND AT.pdvno = N.pdvno AND AT.xano = N.xano
    LEFT JOIN sqldados.nf            AS EF
              ON N.storeno = EF.storeno AND EF.nfno = IFNULL(AT.nfEntRet, 0) AND EF.nfse = '3'
WHERE N.storeno = :storeno
  AND N.pdvno = :pdvno
  AND N.xano = :xano;

DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
(
  PRIMARY KEY (invno),
  INDEX v1 (nfStoreno, nfNfno, nfNfse),
  INDEX v2 (s1, s2, l2),
  INDEX v3 (storeno, obsReg)
)
SELECT invno,
       storeno,
       date,
       nfNfno,
       nfStoreno,
       nfNfse,
       s1,
       s2,
       l2,
       CAST(CONCAT('NI *', I.invno) AS CHAR) AS obsReg
FROM
  sqldados.inv AS I
WHERE I.storeno IN (2, 3, 4, 5, 8)
  AND I.bits & POW(2, 4) = 0
  AND I.date >= ( SELECT MIN(dataNota) FROM T_DADOS );

DROP TEMPORARY TABLE IF EXISTS T_NI1;
CREATE TEMPORARY TABLE T_NI1
SELECT loja, pdv, transacao, invno, date
FROM
  T_VENDA            AS U USE INDEX (v2)
    INNER JOIN T_INV AS I
               ON U.storenoE = I.nfStoreno AND
                  U.nfnoE = I.nfNfno AND
                  U.nfseE = I.nfNfse;

DROP TEMPORARY TABLE IF EXISTS T_NI2;
CREATE TEMPORARY TABLE T_NI2
SELECT loja, pdv, transacao, invno, date
FROM
  T_INV                AS I
    INNER JOIN T_VENDA AS U
               ON U.storenoE = I.s1 AND
                  U.pdvnoE = I.s2 AND
                  U.xanoE = I.l2;

DROP TEMPORARY TABLE IF EXISTS T_NI3;
CREATE TEMPORARY TABLE T_NI3
SELECT loja, pdv, transacao, invno, I.date, U.obsNI
FROM
  T_INV                AS I
    INNER JOIN T_VENDA AS U
               ON U.loja = I.storeno AND
                  U.obsNI LIKE 'NI%' AND
                  U.obsNI LIKE CONCAT('%', I.invno, '%');

DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
(
  INDEX (loja, pdv, transacao)
)
SELECT loja, pdv, transacao, invno, date
FROM
  T_NI1
UNION
DISTINCT
SELECT loja, pdv, transacao, invno, date
FROM
  T_NI2
UNION
DISTINCT
SELECT loja, pdv, transacao, invno, date
FROM
  T_NI3;

DROP TEMPORARY TABLE IF EXISTS T_NI_PRD;
CREATE TEMPORARY TABLE T_NI_PRD
(
  INDEX (loja, pdv, transacao, prdno, grade)
)
SELECT loja, pdv, transacao, prdno, grade, T_NI.invno, T_NI.date
FROM
  sqldados.iprd
    INNER JOIN T_NI
               USING (invno)
GROUP BY loja, pdv, transacao, prdno, grade;

/************************************************************************/

SELECT D.loja,
       pdvno,
       xano,
       nota,
       D.prdno,
       dev,
       dev                  AS devDB,
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
       X.invno              AS ni,
       CAST(X.date AS date) AS dataNi
FROM
  T_DADOS              AS D
    LEFT JOIN T_NI_PRD AS X
              ON X.loja = D.loja AND X.pdv = D.pdvno AND X.transacao = D.xano AND X.prdno = D.prdno AND
                 X.grade = D.grade
WHERE (:todosLocais = 'S' OR IFNULL(local, '') != '')
