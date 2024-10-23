SELECT E.storeno                               AS loja,
       E.ordno                                 AS pedido,
       TRIM(E.prdno)                           AS codigo,
       MAX(TRIM(IFNULL(B.barcode, P.barcode))) AS barcode,
       P.mfno                                  AS refFor,
       TRIM(MID(name, 1, 37))                  AS descricao,
       TRIM(MID(P.name, 37, 3))                AS un,
       E.cost                                  AS custo,
       E.grade                                 AS grade,
       E.qtty                                  AS qttyPedido,
       E.qtty - E.qttyCancel - E.qttyRcv       AS qttyPendente
FROM sqldados.oprd AS E
       INNER JOIN sqldados.prd AS P
                  ON P.no = E.prdno
       LEFT JOIN sqldados.prdbar AS B
                 ON E.prdno = B.prdno
                   AND E.grade = B.grade
WHERE storeno = :loja
  AND ordno = :pedido
GROUP BY E.storeno, E.ordno, E.prdno, E.grade