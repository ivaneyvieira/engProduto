REPLACE sqldados.pedidoNdd(id, loja, pedido)
VALUES (:id, :loja, :pedido);

DELETE
FROM sqldados.pedidoNdd
WHERE pedido = 0