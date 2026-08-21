USE sqldados;

SET sql_mode = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
(
  PRIMARY KEY (ni)
)

SELECT I.invno                                                                                            AS ni,
       I.storeno                                                                                          AS loja,
       S.sname                                                                                            AS nomeLoja,
       I.nfname                                                                                           AS nfdno,
       I.invse                                                                                            AS nfdse,
       CAST(IF(issue_date = 0, NULL, issue_date) AS date)                                                 AS dataDevolucao,
       ROUND(I.grossamt / 100, 2)                                                                         AS valorDev,
       I.remarks                                                                                          AS obs,
       @POS1 := POSITION('NF' IN I.remarks) + 2                                                           AS pos1,
       @POS2 := POSITION('(' IN I.remarks)                                                                AS pos2,
       TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1))                                                      AS nfVenda,
       TRIM(SUBSTR(I.remarks, 1, 40))                                                                     AS obsNotaVenda,
       TRIM(SUBSTR(I.remarks, 41, 40))                                                                    AS obsTipo,
       @POS1 := POSITION('(' IN I.remarks) + 1                                                            AS posData1,
       @POS2 := POSITION(')' IN I.remarks)                                                                AS posData2,
       STR_TO_DATE(TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1)), '%d/%m/%Y')                             AS dataVenda,
       SUBSTRING_INDEX(TRIM(MID(I.remarks, LOCATE('CLI', I.remarks) + LENGTH('CLI'), 100)), ' ', 1) * 1   AS custnoCli,
       SUBSTRING_INDEX(TRIM(MID(I.remarks, LOCATE('MUDA', I.remarks) + LENGTH('MUDA'), 100)), ' ', 1) * 1 AS custnoMuda,
       vendno                                                                                             AS codCliente,
       V.sname                                                                                            AS nomeCliente,
  /*dados*/
       D.userSolicitacao                                                                                  AS userSolicitacao,
       D.userTroca                                                                                        AS userTroca,
       D.produtoTroca                                                                                     AS produtoTroca,
       D.tipoDev                                                                                          AS tipoDev,
       D.nfEntRet                                                                                         AS nfEntRet,
       D.impressora                                                                                       AS impressora
FROM
  sqldados.inv                   AS I
    INNER JOIN sqldados.vend     AS V
               ON V.no = I.vendno
    LEFT JOIN  sqldados.dadosDev AS D
               USING (invno)
    LEFT JOIN  sqldados.store    AS S
               ON S.no = I.storeno
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
       N.tipo                                 AS nfTipo,
       N.custno                               AS custnoVend,
       C.name                                 AS nomeVend,
       N.empno                                AS empno,
       E.name                                 AS vendedor,
       T.impressora                           AS impressora
FROM
  T_NOTA                     AS T
    LEFT JOIN sqldados.nf    AS N
              ON N.storeno = T.loja AND N.nfno = SUBSTRING_INDEX(T.nfVenda, '/', 1) * 1 AND
                 N.nfse = SUBSTRING_INDEX(T.nfVenda, '/', -1) AND N.issuedate = T.dataVenda * 1
    LEFT JOIN sqldados.custp AS C
              ON C.no = N.custno
    LEFT JOIN sqldados.emp   AS E
              ON E.no = N.empno;

DROP TEMPORARY TABLE IF EXISTS T_DP_FILIAL;
CREATE TEMPORARY TABLE T_DP_FILIAL
(
  INDEX (custno)
)
SELECT C.no AS custno, F.no AS filial, F.name AS nameFilial
FROM
  sqldados.custp              AS C
    INNER JOIN sqldados.store AS L
               ON L.no = MID(C.no, 1, 1) * 1
    INNER JOIN sqldados.custp AS F
               ON F.cpf_cgc = L.cgc
WHERE C.no IN (200, 300, 400, 500, 800);

SELECT N.ni                                               AS ni,
       N.loja                                             AS loja,
       N.nomeLoja                                         AS nomeLoja,
       N.nfdno                                            AS nfdno,
       N.nfdse                                            AS nfdse,
       N.dataDevolucao                                    AS dataDevolucao,
       N.valorDev                                         AS valorDev,
       N.obs                                              AS obs,
       N.nfVenda                                          AS nfVenda,
       N.obsNotaVenda                                     AS obsNotaVenda,
       N.obsTipo                                          AS obsTipo,
       N.dataVenda                                        AS dataVenda,
       N.codCliente                                       AS codCliente,
       N.nomeCliente                                      AS nomeCliente,
       IF(N.custnoCli = 0, N.custnoMuda, N.custnoCli)     AS custnoObs,
       CO.name                                            AS nomeClienteObs,
  /*Dados*/
       NF.nfno                                            AS nfno,
       NF.nfse                                            AS nfse,
       NF.pdvno                                           AS pdvno,
       NF.xano                                            AS xano,
       NF.nfTipo                                          AS nfTipo,
       NF.empno                                           AS empno,
       NF.vendedor                                        AS vendedor,
       ''                                                 AS notaEntrega,
       NF.custnoVend                                      AS custnoVend,
       NF.nomeVend                                        AS nomeVend,
       N.userSolicitacao                                  AS userSolicitacao,
       US.login                                           AS loginSolicitacao,
       US.name                                            AS nomeSolicitacao,
       N.userTroca                                        AS userTroca,
       UT.login                                           AS loginTroca,
       UT.name                                            AS nomeTroca,
       N.produtoTroca                                     AS produtoTroca,
       N.tipoDev                                          AS tipoDev,
       N.nfEntRet                                         AS nfEntRet,
  /* Produtos */
       I.prdno                                            AS prdno,
       I.grade                                            AS grade,
       P.name                                             AS descricao,
       P.unit                                             AS unidade,
       ROUND(I.qtty / 1000)                               AS quantidadeDev,
       ROUND(I.fob / 100, 2)                              AS valorUnitario,
       MID(TRIM(L.localizacao), 1, 4)                     AS localizacao,
/* Dados*/
       D.quantidadeCom                                    AS quantidadeCom,
       D.quantidadeSem                                    AS quantidadeSem,
  /*Entrega / Dev*/
       UE.no                                              AS userEntregaNo,
       IFNULL(UE.login, '')                               AS userEntrega,
       IFNULL(UE.name, '')                                AS userEntregaName,
       IF(A.dataEntrega = 0, NULL, A.dataEntrega)         AS dataEntrega,
       IF(A.horaEntrega = 0, NULL, A.horaEntrega)         AS horaEntrega,
       UR.no                                              AS userRecebimentoNo,
       IFNULL(UR.login, '')                               AS userRecebimento,
       IFNULL(UR.name, '')                                AS userRecebimentoName,
       IF(A.dataRecebimento = 0, NULL, A.dataRecebimento) AS dataRecebimento,
       IF(A.horaRecebimento = 0, NULL, A.horaRecebimento) AS horaRecebimento,
       FL.filial                                          AS filial,
       N.impressora                                       AS impressora,
       L.kardec                                           AS kardec
FROM
  T_NOTA                                      AS N
    LEFT JOIN  T_NOTA_NF                      AS NF
               USING (ni)
    INNER JOIN sqldados.iprd                  AS I
               ON I.invno = N.ni
    INNER JOIN sqldados.prd                   AS P
               ON P.no = I.prdno
    LEFT JOIN  sqldados.dadosDevProduto       AS D
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.devClienteAutorizacao AS A
               USING (invno, prdno, grade)
    LEFT JOIN  sqldados.custp                 AS CO
               ON CO.no = IF(N.custnoCli = 0, N.custnoMuda, N.custnoCli)
    LEFT JOIN  sqldados.users                    UE
               ON UE.no = A.userEntrega
    LEFT JOIN  sqldados.users                 AS UR
               ON UR.no = A.userRecebimento
    LEFT JOIN  sqldados.prdAdicional          AS L
               ON L.storeno = N.loja AND L.prdno = I.prdno AND L.grade = I.grade
    LEFT JOIN  sqldados.users                 AS US
               ON US.no = N.userSolicitacao
    LEFT JOIN  sqldados.users                 AS UT
               ON UT.no = N.userTroca
    LEFT JOIN  T_DP_FILIAL                    AS FL
               ON FL.custno = NF.custnoVend
WHERE (:devolvido = 'N' OR (UT.no IS NOT NULL))
  AND ((TRIM(MID(L.localizacao, 1, 4)) IN (:localizacao)) OR ('TODOS' IN (:localizacao)) OR (L.localizacao = ''))
  AND (((:impresso = 'S') AND (IFNULL(N.impressora, '') != '')) OR
       ((:impresso = 'N') AND (IFNULL(N.impressora, '') = '')) OR (:impresso = 'T'))
