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
  INDEX v2 (storenoE, nfnoE, nfseE)
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
       IFNULL(UT.login, '')                                     AS loginTroca,
       IFNULL(UT.name, '')                                      AS nameTroca,
       IFNULL(AT.motivoTroca, '')                               AS motivoTroca,
       IFNULL(AT.motivoTrocaCod, '')                            AS motivoTrocaCod,
       IFNULL(AT.nfEntRet, 0)                                   AS nfEntRet,
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
GROUP BY N.storeno, N.pdvno, N.xano, N.tipo;

DROP TEMPORARY TABLE IF EXISTS T_XA;
CREATE TEMPORARY TABLE T_XA
(
  PRIMARY KEY v1 (storeno, pdvno, xano)
)
SELECT loja AS storeno, pdv AS pdvno, transacao AS xano
FROM
  T_VENDA
GROUP BY storeno, pdvno, xano;

/****************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_V;
CREATE TEMPORARY TABLE T_V
(
  INDEX (storeno, ordno)
)
SELECT P.storeno,
       P.pdvno,
       P.xano,
       P.eordno                                  AS ordno,
       date                                      AS data,
       CAST(CONCAT(P.nfno, '/', P.nfse) AS CHAR) AS numero,
       nfno,
       nfse
FROM
  sqlpdv.pxa        AS P
    INNER JOIN T_XA AS X
               USING (storeno, pdvno, xano)
WHERE P.cfo IN (5922, 6922)
  AND storeno IN (2, 3, 4, 5, 8)
  AND nfse = '1'
  AND (bits & POW(2, 4)) = 0;

DROP TEMPORARY TABLE IF EXISTS T_E;
CREATE TEMPORARY TABLE T_E
(
  INDEX (storeno, ordno)
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
  AND (bits & POW(2, 4)) = 0;

DROP TEMPORARY TABLE IF EXISTS T_ENTREGA;
CREATE TEMPORARY TABLE T_ENTREGA
(
  INDEX (loja, pdv, transacao),
  INDEX (lojaE, pdvE, transacaoE)
)
SELECT V.storeno AS loja,
       V.pdvno   AS pdv,
       V.xano    AS transacao,
       E.storeno AS lojaE,
       E.pdvno   AS pdvE,
       E.xano    AS transacaoE,
       V.numero  AS notaVenda,
       V.data    AS dataVenda,
       E.numero  AS notaEntrega,
       E.data    AS dataEntrega
FROM
  T_V              AS V
    INNER JOIN T_E AS E
               USING (storeno, ordno)
GROUP BY lojaE, pdvE, transacaoE;

/****************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_INV;
CREATE TEMPORARY TABLE T_INV
(
  PRIMARY KEY (invno),
  INDEX v1 (nfStoreno, nfNfno, nfNfse)
)
SELECT invno,
       storeno,
       date,
       grossamt / 100 AS valorNi,
       nfStoreno,
       nfNfno,
       nfNfse
FROM
  sqldados.inv AS I
WHERE I.storeno IN (2, 3, 4, 5, 8)
  AND I.bits & POW(2, 4) = 0
  AND (I.invno = :invno OR :invno = 0)
  AND I.date >= :dataInicial
  AND (nfStoreno != 0 || nfNfno != 0 || nfNfse != '');

DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
(
  INDEX (loja, pdv, transacao)
)
SELECT IFNULL(E.loja, U.storeno)   AS loja,
       IFNULL(E.pdv, U.pdvno)      AS pdv,
       IFNULL(E.transacao, U.xano) AS transacao,
       E.lojaE                     AS lojaE,
       E.pdvE                      AS pdvE,
       E.transacaoE                AS transacaoE,
       I.invno                     AS invno,
       I.date                      AS date,
       valorNi                     AS valorNi
FROM
  T_INV                    AS I
    INNER JOIN sqldados.nf AS U
               ON U.storeno = I.nfStoreno AND
                  U.nfno = I.nfNfno AND
                  U.nfse = I.nfNfse
    LEFT JOIN  T_ENTREGA   AS E
               ON U.storeno = E.lojaE AND
                  U.pdvno = E.pdvE AND
                  U.xano = E.transacaoE;

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

SELECT U.loja,
       U.pdv,
       U.transacao,
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
       loginTroca,
       motivoTroca,
       motivoTrocaCod,
       E.notaEntrega                     AS notaEntrega,
       nfEntRet,
       IFNULL(I.invno, 0)                AS ni,
       CAST(I.date AS DATE)              AS dataNi,
       I.valorNi                         AS valorNi,
       IF(P.transacao IS NULL, 'S', 'N') AS pendente
FROM
  T_VENDA                      AS U
    LEFT JOIN T_VENDA_PENDENTE AS P
              USING (loja, pdv, transacao)
    LEFT JOIN T_ENTREGA        AS E
              USING (loja, pdv, transacao)
    LEFT JOIN T_NI             AS I
              USING (loja, pdv, transacao)
WHERE (@PESQUISA = '' OR pedido = @PESQUISA_INT OR U.pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
       tipoNf LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
       nomeCliente LIKE @PESQUISA_LIKE OR vendedor LIKE @PESQUISA_LIKE OR E.notaEntrega LIKE @PESQUISA_LIKE OR
       IFNULL(I.invno, 0) = @PESQUISA_INT)
GROUP BY U.loja, U.pdv, U.transacao, IFNULL(I.invno, 0)