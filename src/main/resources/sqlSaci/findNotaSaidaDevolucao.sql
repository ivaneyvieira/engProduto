USE sqldados;

DO @DT := 20070101;

DO @PESQUISA := :pesquisa;
DO @PESQUISA_LIKE := CONCAT('%', :pesquisa, '%');
DO @PESQUISA_START := CONCAT(:pesquisa, '%');
DO @PESQUISA_NUM := IF(:pesquisa REGEXP '^[0-9]+$', :pesquisa, -1);

DROP TEMPORARY TABLE IF EXISTS T_QUERY;
CREATE TEMPORARY TABLE T_QUERY
(
  INDEX (loja, pdvno, xano)
)
SELECT N.storeno                                                              AS loja,
       N.pdvno                                                                AS pdvno,
       N.xano                                                                 AS xano,
       N.nfno                                                                 AS numero,
       N.nfse                                                                 AS serie,
       N.eordno                                                               AS pedido,
       N.custno                                                               AS cliente,
       IFNULL(C.name, '')                                                     AS nomeCliente,
       N.carrno                                                               AS codTransportadora,
       T.name                                                                 AS nomeTransportadora,
       N.grossamt / 100                                                       AS valorNota,
       CAST(N.issuedate AS DATE)                                              AS dataEmissao,
       SEC_TO_TIME(P.time)                                                    AS hora,
       N.vol_qtty / 100                                                       AS volume,
       N.vol_gross                                                            AS peso,
       N.empno                                                                AS vendedor,
       SUM((X.qtty / 1000) * X.preco)                                         AS totalProdutos,
       IF(N.status <> 1, 'N', 'S')                                            AS cancelada,
       IF(LEFT(OBS.remarks__480, 2) = 'EF ', LEFT(OBS.remarks__480, 11), ' ') AS agendado,
       CAST(IF(N.l16 = 0, NULL, N.l16) AS DATE)                               AS entrega,
       print_remarks                                                          AS observacaoPrint,
       N.remarks                                                              AS observacaoNota,
       O.observacao                                                           AS observacaoAdd,
       CASE D.status
         WHEN 0 THEN 'Incluída'
         WHEN 1 THEN 'Em cobrança'
         WHEN 2 THEN 'Quitada'
         WHEN 3 THEN 'Cartório'
         WHEN 4 THEN 'No advogado'
         WHEN 5 THEN 'Cancelada'
         WHEN 6 THEN 'Perda'
         WHEN 7 THEN 'Processada'
         WHEN 8 THEN 'Outros'
         WHEN 9 THEN 'Pago Parcial'
                ELSE 'Pendente'
       END                                                                    AS situacaoDup,
       CONCAT(D.dupno, '/', D.dupse)                                          AS duplicata,
       IFNULL(D.status, 999)                                                  AS situacaoDupStatus
FROM
  sqldados.nf                               AS N
    LEFT JOIN  sqldados.nfUserPrint         AS PT
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqlpdv.pxa                   AS P
               USING (storeno, pdvno, xano)
    INNER JOIN sqldados.xaprd2              AS X
               USING (storeno, pdvno, xano)
    LEFT JOIN  sqldados.custp               AS C
               ON C.no = N.custno
    LEFT JOIN  sqldados.eordrk              AS OBS
               ON (OBS.storeno = N.storeno AND OBS.ordno = N.eordno)
    LEFT JOIN  sqldados.nfdup               AS ND
               ON ND.nfstoreno = N.storeno AND ND.nfno = N.nfno AND ND.nfse = N.nfse
    LEFT JOIN  sqldados.dup                 AS D
               ON ND.dupstoreno = D.storeno AND ND.duptype = D.type AND ND.dupno = D.dupno AND ND.dupse = D.dupse
    LEFT JOIN  sqldados.carr                AS T
               ON T.no = N.carrno
    LEFT JOIN  sqldados.notaSaidaObservacao AS O
               ON O.storeno = N.storeno
                 AND O.pdvno = N.pdvno
                 AND O.xano = N.xano
WHERE (N.issuedate >= :dataInicial OR :dataInicial = 0)
  AND (N.issuedate <= :dataFinal OR :dataFinal = 0)
  AND N.issuedate >= @DT
  AND (X.prdno = :prdno OR :prdno = '')
  AND (X.grade = :grade OR :grade = '')
  AND (N.status <> 1)
  AND (N.tipo = 2)
GROUP BY N.storeno, N.pdvno, N.xano;

DROP TEMPORARY TABLE IF EXISTS T_FILE_COUNT;
CREATE TEMPORARY TABLE T_FILE_COUNT
SELECT A.storeno AS loja, A.pdvno, A.xano, COUNT(DISTINCT seq) AS quant
FROM
  sqldados.nfSaidaArquivoDevolucao AS A
    INNER JOIN T_QUERY             AS Q
               ON A.storeno = Q.loja AND A.pdvno = Q.pdvno AND A.xano = Q.xano
GROUP BY A.storeno, A.pdvno, A.xano;

SELECT loja,
       pdvno,
       xano,
       numero,
       pedido,
       serie,
       cliente,
       nomeCliente,
       codTransportadora,
       nomeTransportadora,
       valorNota,
       dataEmissao,
       hora,
       vendedor,
       volume,
       peso,
       vendedor,
       totalProdutos,
       cancelada,
       entrega,
       observacaoPrint,
       observacaoNota,
       observacaoAdd,
       duplicata,
       situacaoDup,
       IFNULL(C.quant, 0) AS quantArquivos
FROM
  T_QUERY                  AS Q
    LEFT JOIN T_FILE_COUNT AS C
              USING (loja, pdvno, xano)
WHERE (@PESQUISA = '' OR numero LIKE @PESQUISA_START OR cliente = @PESQUISA_NUM OR
       nomeCliente LIKE @PESQUISA_LIKE OR vendedor = @PESQUISA_NUM OR
       pedido LIKE @PESQUISA)
  AND situacaoDupStatus NOT IN (2, 5)
GROUP BY Q.loja, Q.pdvno, Q.xano

