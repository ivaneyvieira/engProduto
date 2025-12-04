USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
(
  INDEX v1 (storenoE, pdvnoE, xanoE),
  INDEX v2 (storenoE, nfnoE, nfseE),
  INDEX v3 (loja, obsNI)
)
SELECT N.storeno                                                AS loja,
       N.pdvno                                                  AS pdv,
       N.xano                                                   AS transacao,
       IF(N.xatype = 999, V.xatype, N.xatype)                   AS tipo,
       N.eordno                                                 AS pedido,
       CAST(N.issuedate AS DATE)                                AS data,
       N.nfno                                                   AS nfno,
       N.nfse                                                   AS nfse,
       N.eordno                                                 AS eordno,
       CONCAT(N.nfno, '/', N.nfse)                              AS nota,
       CASE
         WHEN N.tipo = 0  THEN 'VENDA NF'
         WHEN N.tipo = 1  THEN 'TRANSFERENCIA'
         WHEN N.tipo = 2  THEN 'DEVOLUCAO'
         WHEN N.tipo = 3  THEN 'SIMP REME'
         WHEN N.tipo = 4  THEN 'ENTRE FUT'
         WHEN N.tipo = 5  THEN 'RET DEMON'
         WHEN N.tipo = 6  THEN 'VENDA USA'
         WHEN N.tipo = 7  THEN 'OUTROS'
         WHEN N.tipo = 8  THEN 'NF CF'
         WHEN N.tipo = 9  THEN 'PERD/CONSER'
         WHEN N.tipo = 10 THEN 'REPOSICAO'
         WHEN N.tipo = 11 THEN 'RESSARCI'
         WHEN N.tipo = 12 THEN 'COMODATO'
         WHEN N.tipo = 13 THEN 'NF EMPRESA'
         WHEN N.tipo = 14 THEN 'BONIFICA'
         WHEN N.tipo = 15 THEN 'NFE'
                          ELSE 'TIPO INVALIDO'
       END                                                      AS tipoNf,
       SEC_TO_TIME(P.time)                                      AS hora,
       Q.string                                                 AS tipoPgto,
       N.grossamt / 100                                         AS valor,
       N.custno                                                 AS cliente,
       C.name                                                   AS nomeCliente,
       IF(C.cpf_cgc LIKE 'NAO%', '', IFNULL(A.state, C.state1)) AS uf,
       CONCAT(E.no, ' - ', MID(E.sname, 1, 17))                 AS vendedor,
       IFNULL(SUM(V.amt / 100), N.grossamt / 100)               AS valorTipo,
       CONCAT(N.remarks, ' ', N.print_remarks)                  AS obs,
       IFNULL(AT.autoriza, 'N')                                 AS autoriza,
       IFNULL(AT.solicitacaoTroca, 'N')                         AS solicitacaoTroca,
       IFNULL(AT.produtoTroca, 'N')                             AS produtoTroca,
       IFNULL(AT.userTroca, 0)                                  AS userTroca,
  /*IFNULL(AT.userSolicitacao, 0)                            AS userSolicitacao,*/
       IFNULL(UT.login, '')                                     AS loginTroca,
       IFNULL(UT.name, '')                                      AS nameTroca,
  /*IFNULL(US.login, '')                                     AS loginSolicitacao,*/
       IFNULL(AT.motivoTroca, '')                               AS motivoTroca,
       IFNULL(AT.motivoTrocaCod, '')                            AS motivoTrocaCod,
       IFNULL(AT.nfEntRet, 0)                                   AS nfEntRet,
       CAST(MID(CASE
                  WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
                  WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                           ELSE ''
                END, 1, 60) AS CHAR)                            AS obsNI,
       IFNULL(EF.storeno, N.storeno)                            AS storenoE,
       IFNULL(EF.nfno, N.nfno)                                  AS nfnoE,
       IFNULL(EF.nfse, N.nfse)                                  AS nfseE,
       IFNULL(EF.pdvno, N.pdvno)                                AS pdvnoE,
       IFNULL(EF.xano, N.xano)                                  AS xanoE
FROM
  sqldados.nf                         AS N
    LEFT JOIN  sqldados.ctadd         AS A
               ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN  sqlpdv.pxa             AS P
               ON P.storeno = N.storeno AND P.pdvno = N.pdvno AND P.xano = N.xano
    LEFT JOIN  sqlpdv.pxaval          AS V
               ON V.storeno = N.storeno AND V.pdvno = N.pdvno AND V.xano = N.xano
    LEFT JOIN  sqldados.nfAutorizacao AS AT
               ON AT.storeno = N.storeno AND AT.pdvno = N.pdvno AND AT.xano = N.xano
    LEFT JOIN  sqldados.nf            AS EF
               ON N.storeno = EF.storeno AND EF.nfno = IFNULL(AT.nfEntRet, 0) AND EF.nfse = '3'
    LEFT JOIN  sqldados.users         AS UT
               ON UT.no = AT.userTroca
                 /*LEFT JOIN  sqldados.users         AS US
                            ON US.no = AT.userSolicitacao*/
    INNER JOIN sqldados.custp         AS C
               ON C.no = N.custno
    INNER JOIN sqldados.emp           AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.query1        AS Q
               ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
WHERE (N.storeno IN (2, 3, 4, 5, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
  AND (autoriza = :autoriza OR :autoriza = 'T' OR :devolucaoStatus = 'V')
GROUP BY N.storeno, N.pdvno, N.xano, N.tipo;

/****************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_XANO;
CREATE TEMPORARY TABLE T_XANO
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT loja AS storeno, pdv AS pdvno, transacao AS xano
FROM
  T_VENDA
GROUP BY storeno, pdvno, xano;

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
    INNER JOIN T_XANO
               USING (storeno, pdvno, xano)
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 8)
  AND nfse = '1'
GROUP BY storeno, ordno;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  PRIMARY KEY (storeno, ordno)
)
SELECT P.storeno,
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
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT V.storeno     AS loja,
       V.pdvno       AS pdv,
       V.xano        AS transacao,
       V.numero      AS notaVenda,
       MAX(E.numero) AS notaEntrega,
       MAX(E.data)   AS dataEntrega
FROM
  T_V             AS V
    LEFT JOIN T_E AS E
              USING (storeno, ordno)
GROUP BY V.storeno, V.pdvno, V.xano;

/****************************************************************************************/

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
  AND (I.invno = :invno OR :invno = 0)
  AND I.date >= :dataInicial;

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
SELECT loja, pdv, transacao, prdno, grade, SUM(ROUND(qtty / 1000)) AS qtty
FROM
  sqldados.iprd
    INNER JOIN T_NI
               USING (invno)
GROUP BY loja, pdv, transacao, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_XA_PRD;
CREATE TEMPORARY TABLE T_XA_PRD
(
  INDEX (loja, pdv, transacao, prdno, grade)
)
SELECT loja, pdv, transacao, prdno, grade, SUM(ROUND(qtty)) AS qtty
FROM
  T_VENDA                     AS V
    INNER JOIN sqldados.xaprd AS X
               ON V.loja = X.storeno
                 AND V.pdv = X.pdvno
                 AND V.transacao = X.xano
GROUP BY loja, pdv, transacao, prdno, grade;

DROP TEMPORARY TABLE IF EXISTS T_VENDA_PENDENTE;
CREATE TEMPORARY TABLE T_VENDA_PENDENTE
(
  PRIMARY KEY (loja, pdv, transacao)
)
SELECT loja, pdv, transacao
FROM
  T_XA_PRD              AS X
    INNER JOIN T_NI_PRD AS N
               USING (loja, pdv, transacao, prdno, grade)
WHERE (IFNULL(X.qtty, 0) - IFNULL(N.qtty, 0)) > 0
GROUP BY loja, pdv, transacao;

SELECT DISTINCT U.loja,
                pdv,
                transacao,
                pedido,
                data,
                nota,
                tipoNf,
                hora,
                tipoPgto,
                valor,
                cliente,
                nomeCliente,
                uf,
                vendedor,
                valorTipo,
                obs,
                autoriza,
                solicitacaoTroca,
                produtoTroca,
                userTroca,
                nameTroca,
/*       userSolicitacao,*/
                loginTroca,
/*       loginSolicitacao,*/
                motivoTroca,
                motivoTrocaCod,
                E.notaEntrega                     AS notaEntrega,
                nfEntRet,
                I.invno                           AS ni,
                CAST(I.date AS DATE)              AS dataNi,
                IF(P.transacao IS NULL, 'S', 'N') AS pendente
FROM
  T_VENDA                      AS U
    LEFT JOIN T_VENDA_PENDENTE AS P
              USING (loja, pdv, transacao)
    LEFT JOIN T_NI             AS I
              USING (loja, pdv, transacao)
    LEFT JOIN T_ENTREGA        AS E
              USING (loja, pdv, transacao)
WHERE (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
       tipoNf LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
       UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
       vendedor LIKE @PESQUISA_LIKE OR E.notaEntrega LIKE @PESQUISA_LIKE OR IFNULL(I.invno, 0) = @PESQUISA_INT)
  AND CASE
        WHEN :devolucaoStatus = 'V'  THEN TRUE
        WHEN :devolucaoStatus = 'P'  THEN I.invno IS NULL
        WHEN :devolucaoStatus = 'GP' THEN I.invno IS NOT NULL AND P.transacao IS NOT NULL
        WHEN :devolucaoStatus = 'G'  THEN I.invno IS NOT NULL AND P.transacao IS NULL
        WHEN :devolucaoStatus = 'T'  THEN TRUE
      END
GROUP BY U.loja, U.pdv, U.transacao, U.tipo, I.invno
