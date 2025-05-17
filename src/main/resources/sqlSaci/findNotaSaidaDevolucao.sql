USE sqldados;

DO @DT := 20070101;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
SELECT N.storeno                                                              AS loja,
       N.pdvno                                                                AS pdvno,
       N.xano                                                                 AS xano,
       N.nfno                                                                 AS numero,
       N.nfse                                                                 AS serie,
       N.eordno                                                               AS pedido,
       N.custno                                                               AS cliente,
       IFNULL(C.name, '')                                                     AS nomeCliente,
       N.grossamt / 100                                                       AS valorNota,
       CAST(N.issuedate AS DATE)                                              AS data,
       SEC_TO_TIME(P.time)                                                    AS hora,
       N.empno                                                                AS vendedor,
       SUM((X.qtty / 1000) * X.preco)                                         AS totalProdutos,
       IF(N.status <> 1, 'N', 'S')                                            AS cancelada,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ') AS agendado,
       CAST(IF(N.l16 = 0, NULL, N.l16) AS DATE)                               AS entrega,
       print_remarks                                                          AS observacaoPrint
FROM
  sqldados.nf                       AS N
    LEFT JOIN  sqldados.nfUserPrint AS PT
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxa           AS P
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.xaprd2      AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.custp       AS C
               ON C.no = N.custno
    LEFT JOIN  sqldados.eordrk      AS OBS
               ON (OBS.storeno = N.storeno AND OBS.ordno = N.eordno)
WHERE (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.issuedate >= @DT
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
  AND (N.status <> 1)
  AND (N.tipo = 2)
GROUP BY N.storeno, N.pdvno, N.xano;

SELECT loja,
       pdvno,
       xano,
       numero,
       pedido,
       serie,
       cliente,
       nomeCliente,
       valorNota,
       data,
       hora,
       vendedor,
       totalProdutos,
       cancelada,
       entrega,
       observacaoPrint
FROM
  T_QUERY                      AS Q
WHERE (@PESQUISA = '' OR numero LIKE @PESQUISA_START OR cliente = @PESQUISA_NUM OR
       nomeCliente LIKE @PESQUISA_LIKE OR vendedor = @PESQUISA_NUM OR
       pedido LIKE @PESQUISA)
GROUP BY Q.loja, Q.pdvno, Q.xano

