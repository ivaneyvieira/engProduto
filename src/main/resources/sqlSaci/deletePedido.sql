DELETE
FROM
  sqldados.ords
WHERE no = :pedido
  AND storeno = :loja
  AND amt = 0;

DELETE P
FROM
  sqldados.oprd              AS P
    INNER JOIN sqldados.ords AS O
               ON O.no = P.ordno
                 AND O.storeno = P.storeno
WHERE P.ordno = :pedido
  AND P.storeno = :loja
  AND O.amt = 0
