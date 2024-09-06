SET SQL_MODE = '';

DO @PESQUISA := :pesquisa;
DO @PESQUISANUM := IF(@PESQUISA REGEXP '[0-9]+', @PESQUISA, '');
DO @PESQUISASTART := CONCAT(@PESQUISA, '%');
DO @PESQUISALIKE := CONCAT('%', @PESQUISA, '%');

DROP TEMPORARY TABLE IF EXISTS T_PEDIDO;
CREATE TEMPORARY TABLE T_PEDIDO
SELECT N.storeno                                          AS lojaNoOri,
       SO.sname                                           AS lojaOrigem,
       SD.no                                              AS lojaNoDes,
       SD.sname                                           AS lojaDestino,
       CAST(CONCAT('Rota', SO.no, SD.no) AS CHAR)         AS rota,
       CAST(N.ordno AS CHAR)                              AS ordno,
       N.custno                                           AS cliente,
       C.name                                             AS clienteNome,
       CAST(date AS DATE)                                 AS data,
       N.empno                                            AS vendedor,
       U.no                                               AS userno,
       U.name                                             AS usuario,
       U.login                                            AS usuarioLogin,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.price / 100)               AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       'N'                                                AS cancelada,
       SEC_TO_TIME(N.l4)                                  AS hora,
       CAST(T.issuedate AS DATE)                          AS dataTransf,
       CAST(CONCAT(T.nfno, '/', T.nfse) AS CHAR)          AS notaTransf,
       N.status                                           AS situacao,
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
       N.s11                                              AS userReserva,
       UR.name                                            AS nameReserva,
       CAST(CONCAT(MID(R.remarks__480, 1, 40),
                   MID(R.remarks__480, 41, 40),
                   MID(R.remarks__480, 81, 40),
                   MID(R.remarks__480, 121, 40),
                   MID(R.remarks__480, 161, 40)) AS CHAR) AS observacao,
       TRIM(MID(R.remarks__480, 1, 40))                   AS referente,
       TRIM(MID(R.remarks__480, 41, 40))                  AS entregue,
       TRIM(MID(R.remarks__480, 81, 40))                  AS recebido,
       TRIM(MID(R.remarks__480, 121, 40))                 AS selfColor,
       S.no                                               AS numSing,
       S.login                                            AS loginSing,
       IFNULL(SA.name, S.name)                            AS nameSing,
       T.grossamt / 100                                   AS valorTransf,
       TRIM(T.remarks)                                    AS observacaoTransf,
       T.padbits                                          AS userTransf,
       UT.name                                            AS nameTransf,
       UT.login                                           AS nameTransfLogin,
       N.s16                                              AS numImpressora,
       CASE N.paymno
         WHEN 69 THEN 'FISCAL'
         WHEN 434 THEN 'INTERNA'
         ELSE 'OUTRA'
       END                                                AS tipoTransf
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
       LEFT JOIN sqldados.users AS S
                 ON S.no = N.s10
       LEFT JOIN sqldados.users AS SA
                 ON SA.login = S.login
                   AND (SA.bits1 & 1) = 0
       LEFT JOIN sqldados.eordrk AS R
                 ON R.storeno = N.storeno AND R.ordno = N.ordno
       LEFT JOIN sqldados.nf AS T
                 ON T.storeno = N.storeno
                   AND T.eordno = N.ordno
       LEFT JOIN sqldados.users AS UT
                 ON UT.no = T.padbits
       LEFT JOIN sqldados.users AS UR
                 ON UR.no = N.s11
WHERE N.date > 20231106
  AND N.paymno IN (69, 434)
  AND CASE :marca
        WHEN 0 THEN N.status IN (1, 2, 3, 5, 6, 7, 8)
        WHEN 1 THEN N.status IN (4)
        WHEN 999 THEN TRUE
        ELSE FALSE
      END
  AND (N.storeno = :storeno OR SD.no = :storeno OR :storeno = 0)
  AND IFNULL(SD.no, 0) != IFNULL(SO.no, 0)
  AND (N.date >= :dataInicial OR :dataInicial = 0)
  AND (N.date <= :dataFinal OR :dataFinal = 0)
  AND CASE :autorizado
        WHEN 'S' THEN IFNULL(S.no, 0) > 0
        WHEN 'N' THEN IFNULL(S.no, 0) = 0
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
  AND CASE :impresso
        WHEN 'S' THEN N.s16 > 0
        WHEN 'N' THEN (N.s16 = 0) OR (IFNULL(S.no, 0) = 0)
        WHEN 'T' THEN TRUE
        ELSE FALSE
      END
  AND (L.localizacao LIKE 'CD5A%' OR :filtraCD5A = 'N')
GROUP BY N.storeno, N.ordno;

SELECT lojaNoOri,
       lojaOrigem,
       lojaNoDes,
       lojaDestino,
       rota,
       ordno,
       cliente,
       clienteNome,
       data,
       vendedor,
       userno,
       usuario,
       usuarioLogin,
       localizacao,
       usuarioCD,
       totalProdutos,
       marca,
       cancelada,
       hora,
       situacao,
       observacao,
       dataTransf,
       notaTransf,
       selfColor,
       referente,
       entregue,
       recebido,
       numSing,
       loginSing,
       nameSing,
       valorTransf,
       observacaoTransf,
       userTransf,
       nameTransf,
       nameTransfLogin,
       userReserva,
       nameReserva,
       tipoTransf
FROM T_PEDIDO
WHERE (lojaOrigem = @PESQUISA OR
       lojaDestino = @PESQUISA OR
       cliente = @PESQUISANUM OR
       ordno = @PESQUISANUM OR
       nameTransf LIKE @PESQUISALIKE OR
       notaTransf LIKE @PESQUISASTART OR
       nameSing LIKE @PESQUISALIKE OR
       usuario LIKE @PESQUISASTART OR
       situacaoPedido LIKE @PESQUISASTART OR
       @PESQUISA = '')