USE sqldados;

SET sql_mode = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (ni)
)

SELECT I.invno                                                                AS ni,
       I.storeno                                                              AS loja,
       I.nfname                                                               AS nfdno,
       I.invse                                                                AS nfdse,
       CAST(IF(issue_date = 0, NULL, issue_date) AS date)                     AS dataDevolucao,
       ROUND(I.grossamt / 100, 2)                                             AS valorDev,
       I.remarks                                                              AS obs,
       @POS1 := POSITION('NF' IN I.remarks) + 2                               AS pos1,
       @POS2 := POSITION('(' IN I.remarks)                                    AS pos2,
       TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1))                          AS nfVenda,
       TRIM(SUBSTR(I.remarks, 41, 40))                                        AS obsTipo,
       @POS1 := POSITION('(' IN I.remarks) + 1                                AS posData1,
       @POS2 := POSITION(')' IN I.remarks)                                    AS posData2,
       STR_TO_DATE(TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1)), '%d/%m/%Y') AS dataVenda,
       vendno                                                                 AS codCliente,
       V.sname                                                                AS nomeCliente,
  /*dados*/
       D.userSolicitacao                                                      AS userSolicitacao,
       D.userTroca                                                            AS userTroca,
       D.produtoTroca                                                         AS produtoTroca,
       D.tipoDev                                                              AS tipoDev,
       D.nfEntRet                                                             AS nfEntRet
FROM
  sqldados.inv                   AS I
    INNER JOIN sqldados.vend     AS V
               ON V.no = I.vendno
    LEFT JOIN  sqldados.dadosDev AS D
               USING (invno)
WHERE I.type = 2
  AND I.bits & POW(2, 4) = 0
  AND I.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.issue_date >= :dataInicial OR :dataInicial = 0)
  AND (I.issue_date <= :dataFinal OR :dataFinal = 0)
HAVING (:pesquisa = '' OR ni = :pesquisa OR nfdno = :pesquisa OR nfVenda LIKE CONCAT(:pesquisa, '%') OR
        obsTipo LIKE CONCAT('%', :pesquisa, '%') OR codCliente = :pesquisa OR nomeCliente LIKE CONCAT(:pesquisa, '%'));

DROP TEMPORARY TABLE IF EXISTS T_NOTA_NF;
CREATE TEMPORARY TABLE T_NOTA_NF
(
  PRIMARY KEY (ni)
)
SELECT ni,
       SUBSTRING_INDEX(T.nfVenda, '/', 1) * 1 AS nfno,
       SUBSTRING_INDEX(T.nfVenda, '/', -1)    AS nfse,
       N.storeno                              AS storeno,
       N.pdvno                                AS pdvno,
       N.xano                                 AS xano,
       N.tipo                                 AS nfTipo
FROM
  T_NOTA                  AS T
    LEFT JOIN sqldados.nf AS N
              ON N.storeno = T.loja AND N.nfno = SUBSTRING_INDEX(T.nfVenda, '/', 1) * 1 AND
                 N.nfse = SUBSTRING_INDEX(T.nfVenda, '/', -1) AND N.issuedate = T.dataVenda * 1;

SELECT N.ni                  AS ni,
       N.loja                AS loja,
       N.nfdno               AS nfdno,
       N.nfdse               AS nfdse,
       N.dataDevolucao       AS dataDevolucao,
       N.valorDev            AS valorDev,
       N.obs                 AS obs,
       N.nfVenda             AS nfVenda,
       N.obsTipo             AS obsTipo,
       N.dataVenda           AS dataVenda,
       N.codCliente          AS codCliente,
       N.nomeCliente         AS nomeCliente,
  /*Dados*/
       NF.nfno               AS nfno,
       NF.nfse               AS nfse,
       NF.pdvno              AS pdvno,
       NF.xano               AS xano,
       NF.nfTipo             AS nfTipo,
       N.userSolicitacao     AS userSolicitacao,
       US.login              AS loginSolicitacao,
       US.name               AS nomeSolicitacao,
       N.userTroca           AS userTroca,
       UT.login              AS loginTroca,
       UT.name               AS nomeTroca,
       N.produtoTroca        AS produtoTroca,
       N.tipoDev             AS tipoDev,
       N.nfEntRet            AS nfEntRet,
  /* Produtos */
       I.prdno               AS prdno,
       I.grade               AS grade,
       P.name                AS descricao,
       P.unit                AS unidade,
       ROUND(I.qtty / 1000)  AS quantidadeDev,
       ROUND(I.fob / 100, 2) AS valorUnitario,
/* Dados*/
       D.produtoTroca        AS produtoTrocaItem,
       D.quantidade          AS quantidadeTipo
FROM
  T_NOTA                                AS N
    LEFT JOIN  T_NOTA_NF                AS NF
               USING (ni)
    INNER JOIN sqldados.iprd            AS I
               ON I.invno = N.ni
    INNER JOIN sqldados.prd             AS P
               ON P.no = I.prdno
    LEFT JOIN  sqldados.dadosDevProduto AS D
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.users           AS US
               ON US.no = N.userSolicitacao
    LEFT JOIN  sqldados.users           AS UT
               ON US.no = N.userTroca

