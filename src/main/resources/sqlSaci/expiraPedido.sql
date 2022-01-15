UPDATE sqldados.eoprd
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido;

UPDATE sqldados.eoprd2
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido;

UPDATE sqldados.eord
SET status = :status
WHERE storeno = :loja
  AND ordno = :pedido