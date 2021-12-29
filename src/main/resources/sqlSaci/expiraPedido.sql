UPDATE sqldados.eord
SET status = 1
WHERE storeno = :loja
  AND ordno = :pedido;

UPDATE sqldados.eoprd
SET status = 1
WHERE storeno = :loja
  AND ordno = :pedido

