SELECT V.storenoOrigem                         AS loja,
       V.pdvnoOrigem                           AS pdv,
       V.xanoOrigem                            AS xano,
       CAST(V.data AS DATE)                    AS data,
       CONCAT(V.nfnoOrigem, '/', V.nfseOrigem) AS nfVenda,
       V.vlTotal / 100                         AS vlTotal,
       V.invnoDevol                            AS ni,
       N.custno                                AS cliente
FROM
  sqldados.cthcrdev         AS V
    INNER JOIN sqldados.inv AS D
               ON D.invno = V.invnoDevol
    INNER JOIN sqldados.nf  AS N
               ON N.storeno = V.storenoOrigem AND N.pdvno = V.pdvnoOrigem AND N.xano = V.xanoOrigem
WHERE (V.storenoOrigem = :loja OR :loja = 0)
  AND (V.data >= :dataInicial OR :dataInicial = 0)
  AND (V.data <= :dataFinal OR :dataFinal = 0)
