USE sqldados;

SET sql_mode = '';

DO @PESQUISA := :query;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA * 1, -1);
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
SELECT N.storeno                 AS loja,
       N.pdvno                   AS pdv,
       N.xano                    AS xano,
       CONCAT(nfno, '/', nfse)   AS nfVenda,
       CAST(N.issuedate AS DATE) AS data,
       N.grossamt / 100          AS nfValor,
       N.custno                  AS cliente,
       C.name                    AS clienteNome,
       CAST(CASE
              WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
              WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                       ELSE ''
            END AS CHAR)         AS obsNI
FROM
  sqldados.nf                 AS N
    INNER JOIN sqldados.custp AS C
               ON C.no = N.custno
WHERE (N.print_remarks REGEXP 'NI *[0-9]+' OR N.remarks REGEXP 'NI *[0-9]+')
  AND N.issuedate >= SUBDATE(:dataI, INTERVAL 6 MONTH) * 1
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.status <> 1;

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
  AND date >= SUBDATE(:dataI, INTERVAL 6 MONTH)
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
  INDEX (loja, pdv, transacao),
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
       IFNULL(NF1.storeno, NF2.storeno)                                    AS storeno,
       IFNULL(NF1.pdvno, NF2.pdvno)                                        AS pdvno,
       IFNULL(NF1.xano, NF2.xano)                                          AS xano,
       NF1.xano                                                            AS xanoNf1,
       nfNfno                                                              AS nfNfno,
       IFNULL(NF1.custno, NF2.custno)                                      AS custno,
       IFNULL(NF1.cfo, NF2.cfo)                                            AS cfo,
       CAST(CONCAT(IFNULL(NF1.nfno, NF2.nfno), '/',
                   IFNULL(NF1.nfse, NF2.nfse)) AS CHAR)                    AS nfVenda,
       DATE(IFNULL(NF1.issuedate, NF2.issuedate))                          AS nfData,
       IFNULL(NF1.grossamt / 100, NF2.grossamt / 100)                      AS nfValor,
       IFNULL(NF1.empno, NF2.empno)                                        AS empno,
       C.name                                                              AS cliente,
       E.name                                                              AS vendedor,
       @NOTA := TRIM(SUBSTRING_INDEX(
           TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(I.remarks, 'NFE', 'NF'), 'NF', 2), 'NF', -1)), ' ',
           1))                                                             AS nfRmk,
       SUBSTRING_INDEX(@NOTA, '/', 1) * 1                                  AS nfno,
       MID(SUBSTRING_INDEX(SUBSTRING_INDEX(@NOTA, '/', 2), '/', -1), 1, 2) AS nfse,
       I.c9                                                                AS impressora,
       U.loja                                                              AS lojaVenda,
       U.pdv                                                               AS pdvVenda,
       U.xano                                                              AS xanoVenda,
       U.nfVenda                                                           AS nfVendaVenda,
       U.data                                                              AS dataVenda,
       U.cliente                                                           AS clienteVenda,
       U.clienteNome                                                       AS clienteNome,
       U.nfValor                                                           AS nfValorVenda,
       IF(I.remarks LIKE '%EST CARTAO%' OR I.remarks LIKE '%EST BOLETO%' OR I.remarks LIKE '%EST DEP%' OR
          I.remarks LIKE '%REEMBOLSO%', 'S',
          'N')                                                             AS estorno,
       R.pdvReembolso                                                      AS pdvReembolso,
       obsNI                                                               AS obsNI
FROM
  sqldados.inv               AS I
    LEFT JOIN T_VENDA        AS U
              ON U.loja = I.storeno AND U.obsNI LIKE CONCAT('NI%', I.invno, '%') AND U.obsNI LIKE 'NI%'
    LEFT JOIN T_REEMBOLSO    AS R
              ON R.loja = I.storeno AND R.obs LIKE CONCAT('REEMBOLSO%', I.invno, '%')
    LEFT JOIN sqldados.nf    AS NF1
              ON (NF1.nfno = I.nfNfno AND NF1.storeno = I.nfStoreno AND NF1.nfse = I.nfNfse)
    LEFT JOIN sqldados.nf    AS NF2
              ON (NF2.storeno = I.s1 AND NF2.pdvno = I.s2 AND NF2.xano = I.l2)
    LEFT JOIN sqldados.vend  AS V
              ON V.no = I.vendno
    LEFT JOIN sqldados.custp AS CD
              ON CD.cpf_cgc = V.cgc
    LEFT JOIN sqldados.custp AS C
              ON C.no = IFNULL(NF1.custno, NF2.custno)
    LEFT JOIN sqldados.emp   AS E
              ON E.no = IFNULL(NF1.empno, NF2.empno)
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
                   TIME_FORMAT(CURRENT_TIME, '%H:%i'))                                 AS hora,
                I.vendno,
                I.fornecedor,
                I.custnoDev,
                I.clienteDev,
                I.remarks,
                I.valor,
                IFNULL(I.storeno, N.storeno)                                           AS storeno,
                IFNULL(I.pdvno, N.pdvno)                                               AS pdvno,
                IFNULL(I.xano, N.xano)                                                 AS xano,
                IFNULL(I.custno, N.custno)                                             AS custno,
                IFNULL(I.nfVenda, CONCAT(I.nfno, '/', I.nfse))                         AS nfVenda,
                IFNULL(I.nfData, DATE(N.issuedate))                                    AS nfData,
                IFNULL(I.nfValor, N.grossamt / 100)                                    AS nfValor,
                IFNULL(I.empno, N.empno)                                               AS empno,
                IFNULL(I.cliente, C.name)                                              AS cliente,
                IFNULL(I.cfo, N.cfo)                                                   AS cfo,
                TRIM(IFNULL(I.vendedor, E.name))                                       AS vendedor,
                SUBSTRING_INDEX(impressora, '/', 1)                                    AS impressora,
                U.name                                                                 AS userName,
                U.login                                                                AS userLogin,
                IF(I.estorno = 'N', pdvVenda, pdvVenda)                                AS pdvVenda,
                nfVendaVenda                                                           AS nfVendaVenda,
                dataVenda                                                              AS dataVenda,
                clienteVenda                                                           AS clienteVenda,
                clienteNome                                                            AS clienteNome,
                nfValorVenda                                                           AS nfValorVenda,
                IF(IF(I.estorno = 'N', pdvVenda, N.remarks LIKE '') IS NULL, 'N', 'S') AS fezTroca,
                UA.no                                                                  AS usernoAutorizacao,
                UA.name                                                                AS nameAutorizacao,
                UA.login                                                               AS loginAutorizacao,
                IF(I.remarks REGEXP '(^| )P( |$)', 'COM', 'SEM')                       AS comProduto,
                IFNULL(AT.solicitacaoTroca, 'N')                                       AS solicitacaoTroca,
                IFNULL(AT.produtoTroca, 'N')                                           AS produtoTroca,
                IFNULL(AT.motivoTrocaCod, '')                                          AS motivoTrocaCod
FROM
  T_NOTA                             AS I
    LEFT JOIN sqldados.nf            AS N
              ON N.storeno = I.loja AND N.nfno = I.nfno AND N.nfse = I.nfse
    LEFT JOIN T_ENTREGA              AS EF
              ON EF.lojaE = I.storeno
                AND EF.pdvE = I.pdvno
                AND EF.transacaoE = I.xano
    LEFT JOIN sqldados.nfAutorizacao AS AT
              ON AT.storeno = IFNULL(EF.loja, IFNULL(I.storeno, N.storeno))
                AND AT.pdvno = IFNULL(EF.pdv, IFNULL(I.pdvno, N.pdvno))
                AND AT.xano = IFNULL(EF.transacao, IFNULL(I.xano, N.xano))
    LEFT JOIN sqldados.nfAutorizacao AS ATV
              ON ATV.storeno = I.lojaVenda
                AND ATV.pdvno = I.pdvVenda
                AND ATV.xano = I.xanoVenda
    LEFT JOIN sqldados.custp         AS C
              ON C.no = N.custno
    LEFT JOIN sqldados.emp           AS E
              ON E.no = N.empno
    LEFT JOIN sqldados.users         AS U
              ON U.no = I.userno
    LEFT JOIN sqldados.users         AS UA
              ON UA.no = IFNULL(AT.userTroca, ATV.userTroca)
WHERE (@PESQUISA = '' OR I.invno = @PESQUISANUM OR I.loja = @PESQUISANUM OR I.notaFiscal LIKE @PESQUISASTART OR
       I.vendno = @PESQUISANUM OR I.fornecedor LIKE @PESQUISALIKE OR nfVenda LIKE @PESQUISASTART OR
       IFNULL(I.custno, N.custno) = @PESQUISANUM OR IFNULL(I.cliente, C.name) LIKE @PESQUISALIKE OR
       I.remarks LIKE @PESQUISALIKE)
  AND (IFNULL(I.xano, N.xano) IS NOT NULL)