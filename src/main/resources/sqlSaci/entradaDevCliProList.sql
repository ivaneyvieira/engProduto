USE sqldados;

SET sql_mode = '';

DO @PESQUISA := TRIM(:pesquisa);
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (invno)
)
SELECT I.invno                                            AS invno,
       IF(I.usernoFirst = 0, I.usernoLast, I.usernoFirst) AS userno,
       CAST(I.date AS DATE)                               AS data,
       I.storeno                                          AS codLoja,
       CONCAT(I.nfname, '/', I.invse)                     AS nota,
       S.otherName                                        AS loja,
       I.remarks                                          AS observacao,
       ROUND(I.grossamt / 100, 2)                         AS valor
FROM
  sqldados.inv               AS I
    LEFT JOIN sqldados.store AS S
              ON S.no = I.storeno
WHERE (I.date = :data)
  AND (I.storeno = :loja)
  AND I.bits & POW(2, 4) = 0
  AND I.account = '2.01.25'
  AND I.cfo NOT LIKE '%949';

DROP TEMPORARY TABLE IF EXISTS T_RESULT;
CREATE TEMPORARY TABLE T_RESULT
SELECT CAST(I.data AS DATE)                                                  AS data,
       I.codLoja                                                             AS codLoja,
       I.loja                                                                AS loja,
       X.prdno                                                               AS prdno,
       U.name                                                                AS userName,
       U.login                                                               AS userLogin,
       TRIM(X.prdno)                                                         AS codigo,
       TRIM(MID(P.name, 1, 37))                                              AS descricao,
       X.grade                                                               AS grade,
       SUM(ROUND(X.qtty / 1000))                                             AS quantidade,
       observacao                                                            AS observacao,
       TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(I.observacao, ')', 2), ')', -1)) AS tipo,
       SUBSTRING_INDEX(X.c10, '|', 1)                                        AS tipoPrd,
       IF(X.c10 LIKE '%|%',
          SUBSTRING_INDEX(SUBSTRING_INDEX(X.c10, '|', 2), '|', -1),
          '0') * 1                                                           AS tipoQtd,
       I.invno                                                               AS ni,
       I.nota                                                                AS nota,
       I.valor                                                               AS valor
FROM
  T_NOTA                      AS I
    INNER JOIN sqldados.iprd  AS X
               ON I.invno = X.invno
    LEFT JOIN  sqldados.prd   AS P
               ON P.no = X.prdno
    LEFT JOIN  sqldados.users AS U
               ON U.no = I.userno
WHERE (@PESQUISA = '' OR I.codLoja = @PESQUISANUM OR TRIM(prdno) = @PESQUISANUM OR
       TRIM(MID(P.name, 1, 37)) LIKE @PESQUISALIKE OR X.grade LIKE @PESQUISAS OR I.observacao LIKE @PESQUISALIKE OR
       I.nota LIKE @PESQUISASTART OR I.invno = @PESQUISANUM)
GROUP BY I.codLoja, X.prdno, X.grade, I.observacao
ORDER BY descricao, grade, codigo;

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
  sqldados.nf                         AS N
    INNER JOIN sqldados.nfAutorizacao AS AT
               ON AT.storeno = N.storeno AND AT.pdvno = N.pdvno AND AT.xano = N.xano
    LEFT JOIN  sqldados.nf            AS EF
               ON N.storeno = EF.storeno AND EF.nfno = IFNULL(AT.nfEntRet, 0) AND EF.nfse = '3'
WHERE N.issuedate >= SUBDATE(:data, INTERVAL 45 DAY);

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
WHERE (I.date = :data)
  AND (I.storeno = :loja)
  AND I.bits & POW(2, 4) = 0
  AND I.account = '2.01.25'
  AND I.cfo NOT LIKE '%949';

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
SELECT loja, pdv, transacao, I.prdno, I.grade, T_NI.invno, T_NI.date, D.temProduto
FROM
  sqldados.iprd                         AS I
    INNER JOIN T_NI
               USING (invno)
    LEFT JOIN  sqldados.xaprd2Devolucao AS D
               ON D.storeno = loja AND
                  D.pdvno = pdv AND
                  D.xano = transacao AND
                  D.prdno = I.prdno AND
                  D.grade = I.grade AND
                  D.dev = TRUE
GROUP BY loja, pdv, transacao, prdno, grade;

/****************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_V;
CREATE TEMPORARY TABLE T_V
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       nfno,
       nfse
FROM
  sqlpdv.pxa AS P
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 8)
  AND nfse = '1'
  AND date >= SUBDATE(:data, INTERVAL 4 MONTH)
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       P.date                                    AS data
FROM
  sqlpdv.pxa       AS P
    INNER JOIN T_V AS V
               ON P.storeno = V.storeno
                 AND P.eordno = V.ordno
WHERE P.cfo IN (5117, 6117)
  AND P.storeno IN (2, 3, 4, 5, 8)
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_ENTREGA;
CREATE TEMPORARY TABLE T_ENTREGA
(
  PRIMARY KEY (loja, pdv, transacao),
  INDEX (lojaE, pdvE, transacaoE)
)
SELECT V.storeno AS loja,
       V.pdvno   AS pdv,
       V.xano    AS transacao,
       V.numero  AS notaVenda,
       E.storeno AS lojaE,
       E.pdvno   AS pdvE,
       E.xano    AS transacaoE
FROM
  T_V             AS V
    LEFT JOIN T_E AS E
              USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

/****************************************************************************************/


DROP TEMPORARY TABLE IF EXISTS T_PRODUTOS;
CREATE TEMPORARY TABLE T_PRODUTOS
SELECT data,
       codLoja,
       R.loja,
       R.prdno,
       userName,
       userLogin,
       UA.name                                         AS autorizacaoName,
       UA.login                                        AS autorizacaoLogin,
       codigo,
       descricao,
       R.grade,
       quantidade,
       R.observacao,
       tipo,
       IF(tipo REGEXP '^TRO.* M.*' OR
          tipo REGEXP '^EST.* M.*' OR
          tipo REGEXP '^REE.* M.*', tipoPrd, tipo)     AS tipoPrd,
       IFNULL(temProduto, '')                          AS temProduto,
       tipoQtd,
       IF(IFNULL(tipoQtd, 0) = 0, quantidade, tipoQtd) AS tipoQtdEfetiva,
       ni,
       nota,
       valor
FROM
  T_RESULT                           AS R
    LEFT JOIN T_NI_PRD               AS N
              ON R.ni = N.invno
                AND R.prdno = N.prdno
                AND R.grade = N.grade
    LEFT JOIN T_ENTREGA              AS EF
              ON EF.lojaE = N.loja
                AND EF.pdvE = N.pdv
                AND EF.transacaoE = N.transacao
    LEFT JOIN sqldados.nfAutorizacao AS A
              ON A.storeno = IFNULL(EF.loja, N.loja)
                AND A.pdvno = IFNULL(EF.pdv, N.pdv)
                AND A.xano = IFNULL(EF.transacao, N.transacao)
    LEFT JOIN sqldados.users         AS UA
              ON UA.no = A.userTroca;

SELECT data,
       codLoja,
       loja,
       prdno,
       userName,
       userLogin,
       autorizacaoName,
       autorizacaoLogin,
       codigo,
       descricao,
       grade,
       quantidade,
       observacao,
       tipo,
       IF(tipoPrd = '',
          TRIM(CONCAT(SUBSTRING_INDEX(tipo, ' ', 1),
                      IF(temProduto, ' P', '')))
         , tipoPrd) AS tipoPrd,
       tipoQtd,
       tipoQtdEfetiva,
       ni,
       nota,
       valor
FROM
  T_PRODUTOS
GROUP BY ni, data, codLoja, loja, prdno, grade
ORDER BY data, codLoja, loja, prdno, grade


