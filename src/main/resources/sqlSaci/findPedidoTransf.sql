SELECT N.storeno                                          AS loja,
       SO.sname                                           AS lojaOrigem,
       SD.sname                                           AS lojaDestino,
       ordno                                              AS ordno,
       custno                                             AS cliente,
       CAST(date AS DATE)                                 AS data,
       N.empno                                            AS vendedor,
       U.name                                             AS usuario,
       CAST(MID(IFNULL(L.localizacao, ''), 1, 4) AS CHAR) AS localizacao,
       X.c4                                               AS usuarioCD,
       SUM((X.qtty / 1000) * X.price / 100)               AS totalProdutos,
       MAX(X.s12)                                         AS marca,
       'N'                                                AS cancelada
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
WHERE N.date >= 20231101
  AND N.paymno = 69
  AND (X.s12 = :marca OR :marca = 999)
  AND (N.storeno = :storeno OR :storeno = 0)
  AND (N.ordno = :ordno OR :ordno = 0)
  AND (MID(L.localizacao, 1, 4) IN (:locais) OR 'TODOS' IN (:locais))
GROUP BY N.storeno,
         ordno,
         IF(:marca = 999, '', SUBSTRING_INDEX(X.c4, '-', 1)),
         IF(:marca = 999, '', MID(L.localizacao, 1, 4))