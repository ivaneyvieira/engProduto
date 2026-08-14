USE sqldados;

SET sql_mode = '';

DROP TEMPORARY TABLE IF EXISTS T_NOTA;
CREATE TEMPORARY TABLE T_NOTA
SELECT invno                                              AS ni,
       storeno                                            AS loja,
       nfname                                             AS nfdno,
       invse                                              AS nfdse,
       CAST(IF(issue_date = 0, NULL, issue_date) AS date) AS dataDevolucao,
       ROUND(grossamt / 100, 2)                           AS valorDev,
       I.remarks                                          AS obs,
       @POS1 := POSITION('NF' IN I.remarks) + 2           AS pos1,
       @POS2 := POSITION('(' IN I.remarks)                AS pos2,
       TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1))      AS nfVenda,
       TRIM(SUBSTR(I.remarks, 41, 40))                    AS obsTipo,
       @POS1 := POSITION('(' IN I.remarks) + 1            AS posData1,
       @POS2 := POSITION(')' IN I.remarks)                AS posData2,
       TRIM(SUBSTR(I.remarks, @POS1, @POS2 - @POS1))      AS dataVenda,
       vendno                                             AS codCliente,
       V.sname                                            AS nomeCliente
FROM
  sqldados.inv               AS I
    INNER JOIN sqldados.vend AS V
               ON V.no = I.vendno
WHERE I.type = 2
  AND I.bits & POW(2, 4) = 0
  AND I.invno NOT IN ( SELECT nfNfno FROM sqldados.inv WHERE auxShort13 & POW(2, 15) != 0 )
  AND (I.storeno = :loja OR :loja = 0)
  AND (I.issue_date >= :dataInicial OR :dataInicial = 0)
  AND (I.issue_date <= :dataFinal OR :dataFinal = 0)
HAVING (:pesquisa = '' OR ni = :pesquisa OR nfdno = :pesquisa OR nfVenda LIKE CONCAT(:pesquisa, '%') OR
        obsTipo LIKE CONCAT('%', :pesquisa, '%') OR codCliente = :pesquisa OR nomeCliente LIKE CONCAT(:pesquisa, '%'));

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
  /* Produtos */
       I.prdno               AS prdno,
       I.grade               AS grade,
       P.name                AS descricao,
       P.unit                AS unidade,
       ROUND(I.qtty / 1000)  AS quantidadeDev,
       ROUND(I.fob / 100, 2) AS valorUnitario
FROM
  T_NOTA                     AS N
    INNER JOIN sqldados.iprd AS I
               ON I.invno = N.ni
    INNER JOIN sqldados.prd  AS P
               ON P.no = I.prdno

