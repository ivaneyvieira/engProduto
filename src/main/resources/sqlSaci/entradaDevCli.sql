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
       CASE
         WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
         WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                  ELSE ''
       END                       AS obsNI
FROM
  sqldados.nf                 AS N
    INNER JOIN sqldados.custp AS C
               ON C.no = N.custno
WHERE (N.print_remarks REGEXP 'NI *[0-9]+' OR N.remarks REGEXP 'NI *[0-9]+')
  AND N.issuedate >= SUBDATE(:dataI, INTERVAL 1 MONTH) * 1
  AND N.storeno IN (2, 3, 4, 5, 8)
  AND N.xatype IN (1, 999)
  AND N.status <> 1;

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
       U.pdv                                                               AS pdvVenda,
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
              ON U.loja = I.storeno AND U.obsNI REGEXP CONCAT('NI *', I.invno)
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
                A.userno                                                               AS usernoAutorizacao,
                UA.name                                                                AS nameAutorizacao,
                UA.login                                                               AS loginAutorizacao,
                IF(I.remarks REGEXP '(^| )P( |$)', 'COM', 'SEM')                       AS comProduto,
                IFNULL(AT.solicitacaoTroca, 'N')                                       AS solicitacaoTroca,
                IFNULL(AT.produtoTroca, 'N')                                           AS produtoTroca,
                IFNULL(AT.motivoTrocaCod, '')                                          AS motivoTrocaCod
FROM
  T_NOTA                              AS I
    LEFT JOIN sqldados.nf             AS N
              ON N.storeno = I.loja AND N.nfno = I.nfno AND N.nfse = I.nfse
    LEFT JOIN sqldados.nfAutorizacao  AS AT
              ON AT.storeno = N.storeno AND AT.pdvno = N.pdvno AND AT.xano = N.xano
    LEFT JOIN sqldados.custp          AS C
              ON C.no = N.custno
    LEFT JOIN sqldados.emp            AS E
              ON E.no = N.empno
    LEFT JOIN sqldados.users          AS U
              ON U.no = I.userno
    LEFT JOIN sqldados.invAutorizacao AS A
              ON A.invno = I.invno AND A.storeno = IFNULL(I.storeno, N.storeno) AND
                 A.pdvno = IFNULL(I.pdvno, N.pdvno) AND A.xano = IFNULL(I.xano, N.xano)
    LEFT JOIN sqldados.users          AS UA
              ON UA.no = A.userno
WHERE (@PESQUISA = '' OR I.invno = @PESQUISANUM OR I.loja = @PESQUISANUM OR I.notaFiscal LIKE @PESQUISASTART OR
       I.vendno = @PESQUISANUM OR I.fornecedor LIKE @PESQUISALIKE OR nfVenda LIKE @PESQUISASTART OR
       IFNULL(I.custno, N.custno) = @PESQUISANUM OR IFNULL(I.cliente, C.name) LIKE @PESQUISALIKE OR
       I.remarks LIKE @PESQUISALIKE)
  AND (IFNULL(I.xano, N.xano) IS NOT NULL)


