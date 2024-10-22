SELECT storeno                           AS loja,
       ordno                             AS prdido,
       TRIM(prdno)                       AS codigo,
       TRIM(MID(name, 1, 37))            AS descricao,
       E.grade                           AS grade,
       E.qtty                            AS qttyPedido,
       E.qtty - E.qttyCancel - E.qttyRcv AS qttyPendente
FROM sqldados.oprd AS E
       INNER JOIN sqldados.prd AS P
                  ON P.no = E.prdno
WHERE storeno = :loja
  AND ordno = :pedido