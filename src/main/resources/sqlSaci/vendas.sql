USE sqldados;

SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', @PESQUISA, '%');
DO @PESQUISA_START := CONCAT(@PESQUISA, '%');
DO @PESQUISA_INT := IF(@PESQUISA REGEXP '^[0-9]+$', @PESQUISA, NULL);

DROP TEMPORARY TABLE IF EXISTS T_VENDA;
CREATE TEMPORARY TABLE T_VENDA
SELECT N.storeno                                                AS loja,
       N.pdvno                                                  AS pdv,
       N.xano                                                   AS transacao,
       IF(N.xatype = 999, V.xatype, N.xatype)                   AS tipo,
       N.eordno                                                 AS pedido,
       CAST(N.issuedate AS DATE)                                AS data,
       CONCAT(N.nfno, '/', N.nfse)                              AS nota,
       CASE
         WHEN tipo = 0  THEN 'VENDA NF'
         WHEN tipo = 1  THEN 'TRANSFERENCIA'
         WHEN tipo = 2  THEN 'DEVOLUCAO'
         WHEN tipo = 3  THEN 'SIMP REME'
         WHEN tipo = 4  THEN 'ENTRE FUT'
         WHEN tipo = 5  THEN 'RET DEMON'
         WHEN tipo = 6  THEN 'VENDA USA'
         WHEN tipo = 7  THEN 'OUTROS'
         WHEN tipo = 8  THEN 'NF CF'
         WHEN tipo = 9  THEN 'PERD/CONSER'
         WHEN tipo = 10 THEN 'REPOSICAO'
         WHEN tipo = 11 THEN 'RESSARCI'
         WHEN tipo = 12 THEN 'COMODATO'
         WHEN tipo = 13 THEN 'NF EMPRESA'
         WHEN tipo = 14 THEN 'BONIFICA'
         WHEN tipo = 15 THEN 'NFE'
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
       IFNULL(AT.userSolicitacao, 0)                            AS userSolicitacao,
       IFNULL(UT.login, '')                                     AS loginTroca,
       IFNULL(US.login, '')                                     AS loginSolicitacao,
       CASE
         WHEN N.remarks REGEXP 'NI *[0-9]+'       THEN N.remarks
         WHEN N.print_remarks REGEXP 'NI *[0-9]+' THEN N.print_remarks
                                                  ELSE ''
       END                                                      AS obsNI
FROM
  sqldados.nf                         AS N
    LEFT JOIN  sqldados.ctadd         AS A
               ON A.custno = N.custno AND A.seqno = N.custno_addno
    LEFT JOIN  sqlpdv.pxa             AS P
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxaval          AS V
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.nfAutorizacao AS AT
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.users         AS UT
               ON UT.no = AT.userTroca
    LEFT JOIN  sqldados.users         AS US
               ON US.no = AT.userSolicitacao
    INNER JOIN sqldados.custp         AS C
               ON C.no = N.custno
    INNER JOIN sqldados.emp           AS E
               ON E.no = N.empno
    LEFT JOIN  sqldados.query1        AS Q
               ON Q.no_short = IF(N.xatype = 999, V.xatype, N.xatype)
WHERE (N.storeno IN (1, 2, 3, 4, 5, 6, 7, 8))
  AND (N.storeno = :loja OR :loja = 0)
  AND (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.tipo IN (0, 4)
  AND N.status <> 1
GROUP BY N.storeno, N.pdvno, N.xano, tipo
HAVING (@PESQUISA = '' OR pedido = @PESQUISA_INT OR pdv = @PESQUISA_INT OR nota LIKE @PESQUISA_START OR
        tipoNf LIKE @PESQUISA_LIKE OR tipoPgto LIKE @PESQUISA_LIKE OR cliente LIKE @PESQUISA_INT OR
        UPPER(obs) REGEXP CONCAT('NI[^0-9A-Z]*', @PESQUISA_INT) OR nomeCliente LIKE @PESQUISA_LIKE OR
        vendedor LIKE @PESQUISA_LIKE)
   AND (autoriza = :autoriza OR :autoriza = 'T')
ORDER BY N.storeno, N.pdvno, N.xano, tipoNf, tipoPgto;

DROP TEMPORARY TABLE IF EXISTS T_NI;
CREATE TEMPORARY TABLE T_NI
SELECT storeno AS loja, invno, date,  CONCAT('NI *', I.invno) obsNI
FROM
  sqldados.inv AS I
WHERE I.storeno IN (2, 3, 4, 5, 8)
  AND I.date >= :dataInicial;

SELECT U.loja,
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
       userSolicitacao,
       loginTroca,
       loginSolicitacao,
       I.invno              AS ni,
       CAST(I.date AS date) AS dataNi
FROM
  T_VENDA                  AS U
    LEFT JOIN T_NI AS I
              ON U.loja = I.loja AND U.obsNI REGEXP I.obsNI
GROUP BY loja, pdv, transacao, tipo
