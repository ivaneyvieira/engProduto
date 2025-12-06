USE sqldados;

SET sql_mode = '';

DO @PESQUISA := :query;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA * 1, -1);
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

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
  AND date >= SUBDATE(:dataI, INTERVAL 2 MONTH)
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
  INDEX (lojaV, pdvV, transacaoV),
  PRIMARY KEY (lojaE, pdvE, transacaoE)
)
SELECT V.storeno AS lojaV,
       V.pdvno   AS pdvV,
       V.xano    AS transacaoV,
       E.storeno AS lojaE,
       E.pdvno   AS pdvE,
       E.xano    AS transacaoE
FROM
  T_V              AS V
    INNER JOIN T_E AS E
               USING (storeno, ordno)
GROUP BY E.storeno, E.pdvno, E.xano;

/****************************************************************************************/

DROP TEMPORARY TABLE IF EXISTS T_REEMBOLSO;
CREATE TEMPORARY TABLE T_REEMBOLSO
SELECT storeno AS loja, pdvno AS pdvReembolso, remarks AS obs
FROM
  sqldados.pdvcxh
WHERE date >= :dataI
  AND remarks LIKE '%REEMBOLSO%';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  INDEX (invno)
)
SELECT I.invno                                                             AS invno,
       IF(I.usernoFirst = 0, I.usernoLast, I.usernoFirst)                  AS userno,
       I.storeno                                                           AS loja,
       S.otherName                                                         AS nomeLoja,
       CAST(CONCAT(I.nfname, '/', I.invse) AS CHAR)                        AS notaFiscal,
       CAST(I.date AS DATE)                                                AS data,
       I.vendno                                                            AS vendno,
       V.sname                                                             AS fornecedor,
       IFNULL(CD.no, 0)                                                    AS custnoDev,
       IFNULL(CD.name, '')                                                 AS clienteDev,
       I.remarks                                                           AS remarks,
       I.grossamt / 100                                                    AS valor,
       IFNULL(NV.storeno, NF.storeno)                                      AS storeno,
       IFNULL(NV.pdvno, NF.pdvno)                                          AS pdvno,
       IFNULL(NV.xano, NF.xano)                                            AS xano,
       IFNULL(NV.custno, NF.custno)                                        AS custno,
       IFNULL(NV.cfo, NF.cfo)                                              AS cfo,
       CAST(CONCAT(IFNULL(NV.nfno, NF.nfno), '/',
                   IFNULL(NV.nfse, NF.nfse)) AS CHAR)                      AS nfVenda,
       DATE(IFNULL(NV.issuedate, NF.issuedate))                            AS nfData,
       IFNULL(NV.grossamt / 100, NF.grossamt / 100)                        AS nfValor,
       IFNULL(NV.empno, NV.empno)                                          AS empno,
       C.name                                                              AS cliente,
       E.name                                                              AS vendedor,
       @NOTA := TRIM(SUBSTRING_INDEX(
           TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(I.remarks, 'NFE', 'NF'), 'NF', 2), 'NF', -1)), ' ',
           1))                                                             AS nfRmk,
       SUBSTRING_INDEX(@NOTA, '/', 1) * 1                                  AS nfno,
       MID(SUBSTRING_INDEX(SUBSTRING_INDEX(@NOTA, '/', 2), '/', -1), 1, 2) AS nfse,
       I.c9                                                                AS impressora,
       NV.storeno                                                          AS lojaVenda,
       NV.pdvno                                                            AS pdvVenda,
       NV.xano                                                             AS xanoVenda,
       CONCAT(NV.nfno, '/', NV.nfse)                                       AS nfVendaVenda,
       CAST(NV.issuedate AS date)                                          AS dataVenda,
       C.no                                                                AS clienteVenda,
       C.name                                                              AS clienteNome,
       IFNULL(NV.grossamt / 100, NF.grossamt / 100)                        AS nfValorVenda,
       IF(I.remarks LIKE '%EST CARTAO%' OR I.remarks LIKE '%EST BOLETO%' OR I.remarks LIKE '%EST DEP%' OR
          I.remarks LIKE '%REEMBOLSO%', 'S',
          'N')                                                             AS estorno,
       R.pdvReembolso                                                      AS pdvReembolso
FROM
  sqldados.inv               AS I
    LEFT JOIN sqldados.nf    AS NF
              ON (NF.nfno = I.nfNfno AND NF.storeno = I.nfStoreno AND NF.nfse = I.nfNfse)
    LEFT JOIN T_ENTREGA      AS EF
              ON EF.lojaE = NF.storeno
                AND EF.pdvE = NF.pdvno
                AND EF.transacaoE = NF.xano
    LEFT JOIN sqldados.nf    AS NV
              ON EF.lojaV = NV.storeno
                AND EF.pdvV = NV.pdvno
                AND EF.transacaoV = NV.xano
    LEFT JOIN sqldados.nf    AS NE
              ON EF.lojaE = NE.storeno
                AND EF.pdvE = NE.pdvno
                AND EF.transacaoE = NE.xano
    LEFT JOIN T_REEMBOLSO    AS R
              ON R.loja = I.storeno AND R.obs LIKE CONCAT('REEMBOLSO%', I.invno, '%')
    LEFT JOIN sqldados.vend  AS V
              ON V.no = I.vendno
    LEFT JOIN sqldados.custp AS CD
              ON CD.cpf_cgc = V.cgc
    LEFT JOIN sqldados.custp AS C
              ON C.no = IFNULL(NV.custno, NF.custno)
    LEFT JOIN sqldados.emp   AS E
              ON E.no = IFNULL(NV.empno, NF.empno)
    LEFT JOIN sqldados.store AS S
              ON S.no = I.storeno
WHERE I.account = '2.01.25'
  AND I.cfo NOT LIKE '%949'
  AND I.bits & POW(2, 4) = 0
  AND (I.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.date >= :dataI OR :dataI = 0)
  AND (I.date <= :dataF OR :dataF = 0)
  AND (I.date >= :dataLimiteInicial OR :dataLimiteInicial = 0)
  AND CASE :impresso
        WHEN 'S' THEN I.c9 != ''
        WHEN 'N' THEN I.c9 = ''
        WHEN 'T' THEN TRUE
                 ELSE FALSE
      END;

SELECT DISTINCT I.invno,
                I.loja,
                I.nomeLoja,
                I.notaFiscal,
                I.data,
                IF(LOCATE('/', impressora) > 0, SUBSTRING_INDEX(impressora, '/', -1),
                   TIME_FORMAT(CURRENT_TIME, '%H:%i'))           AS hora,
                I.vendno,
                I.fornecedor,
                I.custnoDev,
                I.clienteDev,
                I.remarks,
                I.valor,
                I.storeno                                        AS storeno,
                I.pdvno                                          AS pdvno,
                I.xano                                           AS xano,
                I.custno                                         AS custno,
                I.nfVenda                                        AS nfVenda,
                I.nfData                                         AS nfData,
                I.nfValor                                        AS nfValor,
                I.empno                                          AS empno,
                I.cliente                                        AS cliente,
                I.cfo                                            AS cfo,
                TRIM(IFNULL(I.vendedor, E.name))                 AS vendedor,
                SUBSTRING_INDEX(impressora, '/', 1)              AS impressora,
                U.name                                           AS userName,
                U.login                                          AS userLogin,
                pdvVenda                                         AS pdvVenda,
                nfVendaVenda                                     AS nfVendaVenda,
                dataVenda                                        AS dataVenda,
                clienteVenda                                     AS clienteVenda,
                clienteNome                                      AS clienteNome,
                nfValorVenda                                     AS nfValorVenda,
                IF(I.estorno = 'N', 'S', 'N')                    AS fezTroca,
                UA.no                                            AS usernoAutorizacao,
                UA.name                                          AS nameAutorizacao,
                UA.login                                         AS loginAutorizacao,
                IF(I.remarks REGEXP '(^| )P( |$)', 'COM', 'SEM') AS comProduto,
                IFNULL(AT.solicitacaoTroca, 'N')                 AS solicitacaoTroca,
                IFNULL(AT.produtoTroca, 'N')                     AS produtoTroca,
                IFNULL(AT.motivoTrocaCod, '')                    AS motivoTrocaCod
FROM
  T_NOTA                             AS I
    LEFT JOIN sqldados.nfAutorizacao AS AT
              ON AT.storeno = I.storeno
                AND AT.pdvno = I.pdvno
                AND AT.xano = I.xano
    LEFT JOIN sqldados.nfAutorizacao AS ATV
              ON ATV.storeno = I.lojaVenda
                AND ATV.pdvno = I.pdvVenda
                AND ATV.xano = I.xanoVenda
    LEFT JOIN sqldados.custp         AS C
              ON C.no = I.custno
    LEFT JOIN sqldados.emp           AS E
              ON E.no = I.empno
    LEFT JOIN sqldados.users         AS U
              ON U.no = I.userno
    LEFT JOIN sqldados.users         AS UA
              ON UA.no = IFNULL(AT.userTroca, ATV.userTroca)
WHERE (@PESQUISA = '' OR I.invno = @PESQUISANUM OR I.loja = @PESQUISANUM OR I.notaFiscal LIKE @PESQUISASTART OR
       I.vendno = @PESQUISANUM OR I.fornecedor LIKE @PESQUISALIKE OR nfVenda LIKE @PESQUISASTART OR
       I.custno = @PESQUISANUM OR IFNULL(I.cliente, C.name) LIKE @PESQUISALIKE OR
       I.remarks LIKE @PESQUISALIKE)
