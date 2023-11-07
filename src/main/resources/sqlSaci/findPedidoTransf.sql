DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISALIKE := IF(@PESQUISA REGEXP '[0-9]+', '', CONCAT('%', @PESQUISA, '%'));


DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT N.storeno                                          AS loja,
       SO.sname                                           AS lojaOrigem,
       SD.sname                                           AS lojaDestino,
       CAST(CONCAT('Rota', SO.no, SD.no) AS CHAR)         AS rota,
       CAST(N.ordno AS CHAR)                              AS ordno,
       custno                                             AS cliente,
       CAST(date AS DATE)                                 AS data,
       N.empno                                            AS vendedor,
       U.no                                               AS userno,
       U.name                                             AS usuario,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.price / 100)               AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       'N'                                                AS cancelada,
       SEC_TO_TIME(N.l4)                                  AS hora,
       CASE N.status
         WHEN 0 THEN 'Incluído'
         WHEN 1 THEN 'Orçado'
         WHEN 2 THEN 'Reservado'
         WHEN 3 THEN 'Vendido'
         WHEN 4 THEN 'Expirado'
         WHEN 5 THEN 'Cancelado'
         WHEN 6 THEN 'Reserva B'
         WHEN 7 THEN 'Trânsito'
         WHEN 8 THEN 'Futura'
         ELSE 'Outro'
       END                                                AS situacaoPedido,
       CAST(CONCAT(MID(R.remarks__480, 1, 40),
                   MID(R.remarks__480, 41, 40),
                   MID(R.remarks__480, 81, 40),
                   MID(R.remarks__480, 121, 40),
                   MID(R.remarks__480, 161, 40)) AS CHAR) AS observacao
FROM sqldados.eord AS N
       INNER JOIN sqldados.eoprd AS X
                  USING (storeno, ordno)
       LEFT JOIN sqldados.prdloc AS L
                 ON L.prdno = X.prdno AND L.storeno = 4
       LEFT JOIN sqldados.store AS SO
                 ON SO.no = N.storeno
       LEFT JOIN sqldados.custp AS C
                 ON C.no = N.custno
       LEFT JOIN sqldados.store AS SD
                 ON SD.cgc = C.cpf_cgc
       LEFT JOIN sqldados.users AS U
                 ON U.no = N.userno
       LEFT JOIN sqldados.eordrk AS R
                 ON R.storeno = N.storeno AND R.ordno = N.ordno
WHERE N.date > 20231106
  AND N.paymno = 69
  AND CASE :marca
        WHEN 0 THEN N.status IN (1, 2, 3, 5, 6, 7, 8)
        WHEN 1 THEN N.status IN (4)
        WHEN 999 THEN TRUE
        ELSE FALSE
      END
  AND (N.storeno = :storeno OR :storeno = 0)
  AND IFNULL(SD.no, 0) != IFNULL(SO.no, 0)
  AND (N.date >= :dataInicial OR :dataInicial = 0)
  AND (N.date <= :dataFinal OR :dataFinal = 0)
GROUP BY N.storeno, ordno;


SELECT loja,
       lojaOrigem,
       lojaDestino,
       rota,
       ordno,
       cliente,
       data,
       vendedor,
       userno,
       usuario,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       hora,
       situacaoPedido,
       observacao
FROM T_PEDIDO
WHERE (lojaOrigem = @PESQUISA OR
       lojaDestino = @PESQUISA OR
       cliente = @PESQUISANUM OR
       ordno = @PESQUISANUM OR
       vendedor = @PESQUISANUM OR
       userno = @PESQUISANUM OR
       usuario LIKE @PESQUISALIKE OR
       situacaoPedido LIKE @PESQUISALIKE OR
       observacao LIKE @PESQUISALIKE OR
       @PESQUISA = '')